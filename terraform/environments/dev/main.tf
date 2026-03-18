# =============================================================================
# Environment: dev
# Purpose: Feature testing; cheap FARGATE_SPOT, single AZ, no HTTPS cert.
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
    # Configured via -backend-config flags in CI (see .gitlab-ci.yml)
    # bucket         = set via CI variable TF_STATE_BUCKET
    key            = "dev/terraform.tfstate"
    region         = "eu-west-1"
    # dynamodb_table = set via CI variable TF_STATE_LOCK_TABLE
    encrypt        = true
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = local.common_tags
  }
}

locals {
  environment = "dev"
  project     = "logistics"

  common_tags = {
    Project     = local.project
    Environment = local.environment
    ManagedBy   = "Terraform"
    Repository  = "define-company-logistic-place"
    CostCenter  = "engineering"
  }
}

# ---------------------------------------------------------------------------
# VPC
# ---------------------------------------------------------------------------
module "vpc" {
  source = "../../modules/vpc"

  project_name       = local.project
  environment        = local.environment
  vpc_cidr           = "10.10.0.0/16"
  availability_zones = ["${var.aws_region}a", "${var.aws_region}b"]
  log_retention_days = 7
  tags               = local.common_tags
}

# ---------------------------------------------------------------------------
# ECR Repository
# ---------------------------------------------------------------------------
module "ecr" {
  source = "../../modules/ecr"

  project_name    = local.project
  environment     = local.environment
  repository_name = "logistics-api"
  max_image_count = 10

  ecs_task_execution_role_arns = [module.iam.ecs_task_execution_role_arn]
  ci_role_arns                 = var.ci_role_arns

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# IAM
# ---------------------------------------------------------------------------
module "iam" {
  source = "../../modules/iam"

  project_name    = local.project
  environment     = local.environment
  aws_account_id  = var.aws_account_id
  aws_region      = var.aws_region
  tf_state_bucket = var.tf_state_bucket
  tf_lock_table   = var.tf_lock_table
  ecr_repository_arns = [module.ecr.repository_arn]

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# ALB (no HTTPS in dev)
# ---------------------------------------------------------------------------
module "alb" {
  source = "../../modules/alb"

  project_name               = local.project
  environment                = local.environment
  vpc_id                     = module.vpc.vpc_id
  vpc_cidr                   = module.vpc.vpc_cidr_block
  public_subnet_ids          = module.vpc.public_subnet_ids
  aws_account_id             = var.aws_account_id
  certificate_arn            = ""   # HTTP-only in dev
  enable_deletion_protection = false
  log_retention_days         = 7

  tags = local.common_tags
}

# ---------------------------------------------------------------------------
# ECS Fargate
# ---------------------------------------------------------------------------
module "ecs" {
  source = "../../modules/ecs"

  project_name               = local.project
  environment                = local.environment
  aws_region                 = var.aws_region
  vpc_id                     = module.vpc.vpc_id
  private_subnet_ids         = module.vpc.private_subnet_ids
  alb_security_group_id      = module.alb.alb_security_group_id
  alb_target_group_arn       = module.alb.target_group_arn
  ecr_repository_url         = module.ecr.repository_url
  ecs_task_execution_role_arn = module.iam.ecs_task_execution_role_arn
  ecs_task_role_arn           = module.iam.ecs_task_role_arn

  image_tag     = var.image_tag
  task_cpu      = 256
  task_memory   = 512
  desired_count = 1
  min_capacity  = 1
  max_capacity  = 3

  log_retention_days = 7
  tags               = local.common_tags
}

# ---------------------------------------------------------------------------
# CloudWatch Monitoring
# ---------------------------------------------------------------------------
module "cloudwatch" {
  source = "../../modules/cloudwatch"

  project_name            = local.project
  environment             = local.environment
  aws_region              = var.aws_region
  ecs_cluster_name        = module.ecs.ecs_cluster_name
  ecs_service_name        = module.ecs.ecs_service_name
  ecs_log_group_name      = module.ecs.cloudwatch_log_group
  alb_arn_suffix          = module.alb.alb_arn
  target_group_arn_suffix = module.alb.target_group_arn
  alarm_email             = var.alarm_email
  min_running_tasks       = 1

  tags = local.common_tags
}
