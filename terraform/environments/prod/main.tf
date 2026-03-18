# =============================================================================
# Environment: production
# Purpose: Production workload with high availability, security, and monitoring
# =============================================================================

terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
    }
  }

  backend "s3" {
    # Configured via -backend-config flags in CI
    # bucket         = set via CI variable TF_STATE_BUCKET
    # key            = "production/terraform.tfstate"
    # region         = set via CI variable AWS_DEFAULT_REGION
    # dynamodb_table = set via CI variable TF_STATE_LOCK_TABLE
    # encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = local.common_tags
  }
}

locals {
  environment = "prod"
  project     = "logistics"

  common_tags = {
    Project       = local.project
    Environment   = local.environment
    ManagedBy     = "Terraform"
    Repository    = "define-company-logistic-place"
    CostCenter    = "production"
    Owner         = "platform-team"
    Compliance    = "required"
    BackupPolicy  = "daily"
    MonitoringTier = "critical"
  }

  # Production-specific configuration
  availability_zones = ["${var.aws_region}a", "${var.aws_region}b", "${var.aws_region}c"]
}

# ---------------------------------------------------------------------------
# VPC with Multi-AZ Configuration
# ---------------------------------------------------------------------------
module "vpc" {
  source = "../../modules/vpc"

  project_name       = local.project
  environment        = local.environment
  vpc_cidr           = "10.0.0.0/16"
  availability_zones = local.availability_zones
  log_retention_days = 90  # Extended retention for production
  enable_nat_gateway = true
  single_nat_gateway = false  # Multi-AZ NAT gateways for HA

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# ECR Repositories for All Services
# ---------------------------------------------------------------------------
module "ecr" {
  source = "../../modules/ecr"

  project_name    = local.project
  environment     = local.environment
  repository_names = [
    "api-gateway",
    "company-service",
    "site-service",
    "calculation-service",
    "dashboard-service"
  ]
  max_image_count = 30  # Keep more images in production

  ecs_task_execution_role_arns = [module.iam.ecs_task_execution_role_arn]
  ci_role_arns                 = var.ci_role_arns

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# IAM Roles and Policies
# ---------------------------------------------------------------------------
module "iam" {
  source = "../../modules/iam"

  project_name    = local.project
  environment     = local.environment
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  tf_state_bucket = var.tf_state_bucket
  tf_lock_table   = var.tf_lock_table

  # Production-specific IAM configuration
  enable_cross_account_access = var.enable_cross_account_access
  trusted_account_ids         = var.trusted_account_ids

  ecr_repository_arns = values(module.ecr.repository_arns)

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# Application Load Balancer with SSL/TLS
# ---------------------------------------------------------------------------
module "alb" {
  source = "../../modules/alb"

  project_name               = local.project
  environment                = local.environment
  vpc_id                     = module.vpc.vpc_id
  vpc_cidr                   = module.vpc.vpc_cidr_block
  public_subnet_ids          = module.vpc.public_subnet_ids
  aws_account_id             = var.aws_account_id
  certificate_arn            = var.ssl_certificate_arn
  enable_deletion_protection = true  # Protect production ALB
  log_retention_days         = 90

  # Production security configuration
  enable_waf              = true
  enable_access_logs      = true
  drop_invalid_headers    = true
  enable_http2            = true
  idle_timeout            = 60

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# ECS Microservices Platform
# ---------------------------------------------------------------------------
module "ecs_microservices" {
  source = "../../modules/ecs-microservices"

  project_name               = local.project
  environment                = local.environment
  aws_region                 = var.aws_region
  vpc_id                     = module.vpc.vpc_id
  private_subnet_ids         = module.vpc.private_subnet_ids
  alb_security_group_id      = module.alb.alb_security_group_id
  alb_target_group_arn       = module.alb.target_group_arn
  ecr_repository_url         = module.ecr.base_repository_url
  ecs_task_execution_role_arn = module.iam.ecs_task_execution_role_arn
  ecs_task_role_arn           = module.iam.ecs_task_role_arn

  image_tag                = var.image_tag
  log_retention_days       = 90
  enable_blue_green        = true

  # Production environment variables
  common_environment_variables = [
    {
      name  = "JAVA_OPTS"
      value = "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
    },
    {
      name  = "SPRING_PROFILES_ACTIVE"
      value = "production"
    },
    {
      name  = "LOGGING_LEVEL_ROOT"
      value = "WARN"
    },
    {
      name  = "MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE"
      value = "health,info,metrics,prometheus"
    }
  ]

  # Production secrets from Parameter Store
  ssm_secrets = [
    {
      name      = "DATABASE_PASSWORD"
      valueFrom = "/logistics/prod/database/password"
    },
    {
      name      = "REDIS_PASSWORD"
      valueFrom = "/logistics/prod/redis/password"
    },
    {
      name      = "JWT_SECRET"
      valueFrom = "/logistics/prod/jwt/secret"
    },
    {
      name      = "ENCRYPTION_KEY"
      valueFrom = "/logistics/prod/encryption/key"
    }
  ]

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# Blue-Green Deployment Configuration
# ---------------------------------------------------------------------------
module "blue_green_deployment" {
  source = "../../modules/blue-green-deployment"

  project_name      = local.project
  environment       = local.environment
  ecs_cluster_name  = module.ecs_microservices.ecs_cluster_name
  alb_arn_suffix    = module.alb.alb_arn_suffix

  services = {
    api-gateway = {
      service_name                = module.ecs_microservices.service_names["api-gateway"]
      target_group_name          = "logistics-prod-api-gateway-tg"
      target_group_arn_suffix    = module.alb.target_group_arn_suffix
      min_healthy_targets        = 2
      max_response_time          = 500
    }
    company-service = {
      service_name                = module.ecs_microservices.service_names["company-service"]
      target_group_name          = "logistics-prod-company-service-tg"
      target_group_arn_suffix    = module.alb.target_group_arn_suffix
      min_healthy_targets        = 1
      max_response_time          = 1000
    }
    site-service = {
      service_name                = module.ecs_microservices.service_names["site-service"]
      target_group_name          = "logistics-prod-site-service-tg"
      target_group_arn_suffix    = module.alb.target_group_arn_suffix
      min_healthy_targets        = 1
      max_response_time          = 1000
    }
    calculation-service = {
      service_name                = module.ecs_microservices.service_names["calculation-service"]
      target_group_name          = "logistics-prod-calculation-service-tg"
      target_group_arn_suffix    = module.alb.target_group_arn_suffix
      min_healthy_targets        = 1
      max_response_time          = 2000
    }
    dashboard-service = {
      service_name                = module.ecs_microservices.service_names["dashboard-service"]
      target_group_name          = "logistics-prod-dashboard-service-tg"
      target_group_arn_suffix    = module.alb.target_group_arn_suffix
      min_healthy_targets        = 1
      max_response_time          = 1500
    }
  }

  deployment_config_name           = "CodeDeployDefault.ECSCanary10Percent15Minutes"
  blue_instance_termination_delay  = 10
  notification_email              = var.deployment_notification_email
  enable_deployment_hooks         = true

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# CI/CD Pipeline
# ---------------------------------------------------------------------------
module "cicd_pipeline" {
  source = "../../modules/cicd-pipeline"

  project_name    = local.project
  environment     = local.environment
  aws_region      = var.aws_region
  aws_account_id  = var.aws_account_id

  # Source configuration
  source_provider        = var.source_provider
  source_branch         = "main"
  github_owner          = var.github_owner
  github_repo           = var.github_repo
  github_token          = var.github_token
  codecommit_repo_name  = var.codecommit_repo_name

  # Build configuration
  codebuild_compute_type = "BUILD_GENERAL1_MEDIUM"
  codebuild_image       = "aws/codebuild/standard:7.0"

  # Pipeline configuration
  enable_manual_approval          = true
  enable_blue_green              = true
  codedeploy_application_name    = module.blue_green_deployment.codedeploy_application_name
  ecs_cluster_name               = module.ecs_microservices.ecs_cluster_name
  ecr_repository_url             = module.ecr.base_repository_url

  notification_email    = var.pipeline_notification_email
  log_retention_days   = 90

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# Enhanced CloudWatch Monitoring
# ---------------------------------------------------------------------------
module "cloudwatch" {
  source = "../../modules/cloudwatch"

  project_name            = local.project
  environment             = local.environment
  aws_region              = var.aws_region
  ecs_cluster_name        = module.ecs_microservices.ecs_cluster_name
  ecs_service_names       = values(module.ecs_microservices.service_names)
  ecs_log_group_names     = values(module.ecs_microservices.cloudwatch_log_groups)
  alb_arn_suffix          = module.alb.alb_arn_suffix
  target_group_arn_suffix = module.alb.target_group_arn_suffix

  alarm_email             = var.alarm_email
  min_running_tasks       = 2  # Production minimum

  # Production-specific monitoring
  enable_detailed_monitoring = true
  create_dashboard          = true
  enable_anomaly_detection  = true

  # Custom metrics and alarms
  custom_metrics = {
    error_rate_threshold    = 5    # 5% error rate threshold
    response_time_threshold = 2000 # 2 second response time threshold
    cpu_threshold          = 80   # 80% CPU threshold
    memory_threshold       = 85   # 85% memory threshold
  }

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# AWS Systems Manager Parameters for Configuration
# ---------------------------------------------------------------------------
resource "aws_ssm_parameter" "database_config" {
  for_each = {
    host     = var.database_host
    port     = var.database_port
    name     = var.database_name
    username = var.database_username
  }

  name  = "/logistics/prod/database/${each.key}"
  type  = "String"
  value = each.value

  tags = local.common_tags
}

resource "aws_ssm_parameter" "database_password" {
  name  = "/logistics/prod/database/password"
  type  = "SecureString"
  value = var.database_password

  tags = local.common_tags
}

resource "aws_ssm_parameter" "application_secrets" {
  for_each = {
    redis_password  = var.redis_password
    jwt_secret     = var.jwt_secret
    encryption_key = var.encryption_key
  }

  name  = "/logistics/prod/${replace(each.key, "_", "/")}/"
  type  = "SecureString"
  value = each.value

  tags = local.common_tags
}
