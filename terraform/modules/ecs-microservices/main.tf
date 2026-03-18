# =============================================================================
# Terraform Module — ECS Microservices Platform
#
# Creates a production-ready ECS cluster for microservices with:
#   - Multiple service definitions (API Gateway, Company, Site, Calculation, Dashboard)
#   - Service discovery via AWS Cloud Map
#   - Application Load Balancer with path-based routing
#   - Auto-scaling for each service
#   - Blue-green deployment capability
#   - Container security best practices
#   - Comprehensive monitoring and logging
# =============================================================================

terraform {
  required_version = ">= 1.6.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
    }
  }
}

locals {
  name_prefix = "${var.project_name}-${var.environment}"

  # Service definitions for microservices
  services = {
    api-gateway = {
      port           = 8080
      health_path    = "/actuator/health"
      cpu           = var.environment == "prod" ? 512 : 256
      memory        = var.environment == "prod" ? 1024 : 512
      desired_count = var.environment == "prod" ? 3 : 1
      priority      = 100
      path_pattern  = "/*"
    }
    company-service = {
      port           = 8081
      health_path    = "/actuator/health/readiness"
      cpu           = var.environment == "prod" ? 512 : 256
      memory        = var.environment == "prod" ? 1024 : 512
      desired_count = var.environment == "prod" ? 2 : 1
      priority      = 200
      path_pattern  = "/api/v1/companies*"
    }
    site-service = {
      port           = 8082
      health_path    = "/actuator/health/readiness"
      cpu           = var.environment == "prod" ? 512 : 256
      memory        = var.environment == "prod" ? 1024 : 512
      desired_count = var.environment == "prod" ? 2 : 1
      priority      = 300
      path_pattern  = "/api/v1/sites*"
    }
    calculation-service = {
      port           = 8083
      health_path    = "/actuator/health/readiness"
      cpu           = var.environment == "prod" ? 1024 : 512
      memory        = var.environment == "prod" ? 2048 : 1024
      desired_count = var.environment == "prod" ? 2 : 1
      priority      = 400
      path_pattern  = "/api/v1/calculations*"
    }
    dashboard-service = {
      port           = 8084
      health_path    = "/actuator/health/readiness"
      cpu           = var.environment == "prod" ? 256 : 256
      memory        = var.environment == "prod" ? 512 : 512
      desired_count = var.environment == "prod" ? 2 : 1
      priority      = 500
      path_pattern  = "/api/v1/dashboard*"
    }
  }
}

# ---------------------------------------------------------------------------
# Service Discovery — AWS Cloud Map
# ---------------------------------------------------------------------------
resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "${local.name_prefix}.local"
  vpc         = var.vpc_id
  description = "Service discovery for ${local.name_prefix} microservices"

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-service-discovery"
  })
}

resource "aws_service_discovery_service" "services" {
  for_each = local.services

  name = each.key

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.main.id

    dns_records {
      ttl  = 10
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_grace_period_seconds = 30

  tags = merge(var.tags, {
    Name    = "${local.name_prefix}-${each.key}-discovery"
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# CloudWatch Log Groups — Per Service
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_log_group" "services" {
  for_each = local.services

  name              = "/ecs/${local.name_prefix}/${each.key}"
  retention_in_days = var.log_retention_days

  tags = merge(var.tags, {
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# Security Groups — Per Service
# ---------------------------------------------------------------------------
resource "aws_security_group" "services" {
  for_each = local.services

  name        = "${local.name_prefix}-${each.key}-sg"
  description = "Security group for ${each.key} service"
  vpc_id      = var.vpc_id

  # Allow inbound from ALB and other services
  ingress {
    description     = "HTTP from ALB"
    from_port       = each.value.port
    to_port         = each.value.port
    protocol        = "tcp"
    security_groups = [var.alb_security_group_id]
  }

  # Allow inter-service communication
  ingress {
    description = "Inter-service communication"
    from_port   = each.value.port
    to_port     = each.value.port
    protocol    = "tcp"
    self        = true
  }

  # Management port for actuator endpoints
  ingress {
    description     = "Management port from ALB"
    from_port       = each.value.port + 10  # e.g., 8090 for service on 8080
    to_port         = each.value.port + 10
    protocol        = "tcp"
    security_groups = [var.alb_security_group_id]
  }

  # Allow all outbound
  egress {
    description = "All outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name    = "${local.name_prefix}-${each.key}-sg"
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# Task Definitions — Per Service
# ---------------------------------------------------------------------------
resource "aws_ecs_task_definition" "services" {
  for_each = local.services

  family                   = "${local.name_prefix}-${each.key}"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = each.value.cpu
  memory                   = each.value.memory
  execution_role_arn       = var.ecs_task_execution_role_arn
  task_role_arn            = var.ecs_task_role_arn

  container_definitions = jsonencode([
    {
      name      = each.key
      image     = "${var.ecr_repository_url}/${each.key}:${var.image_tag}"
      essential = true

      portMappings = [
        {
          containerPort = each.value.port
          hostPort      = each.value.port
          protocol      = "tcp"
        },
        # Management port
        {
          containerPort = each.value.port + 10
          hostPort      = each.value.port + 10
          protocol      = "tcp"
        }
      ]

      environment = concat([
        { name = "SPRING_PROFILES_ACTIVE", value = var.environment == "prod" ? "production" : var.environment },
        { name = "SERVER_PORT", value = tostring(each.value.port) },
        { name = "MANAGEMENT_SERVER_PORT", value = tostring(each.value.port + 10) },
        { name = "MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE", value = "health,info,metrics,prometheus" },
        { name = "SPRING_CLOUD_CONSUL_ENABLED", value = "false" },
        { name = "SPRING_CLOUD_DISCOVERY_ENABLED", value = "true" },
        { name = "AWS_REGION", value = var.aws_region },
        { name = "SERVICE_NAME", value = each.key },
        { name = "CLUSTER_NAME", value = aws_ecs_cluster.main.name }
      ], var.common_environment_variables)

      # Service-specific environment variables
      environment = each.key == "api-gateway" ? concat(
        [
          { name = "COMPANY_SERVICE_URL", value = "http://company-service.${local.name_prefix}.local:8081" },
          { name = "SITE_SERVICE_URL", value = "http://site-service.${local.name_prefix}.local:8082" },
          { name = "CALCULATION_SERVICE_URL", value = "http://calculation-service.${local.name_prefix}.local:8083" },
          { name = "DASHBOARD_SERVICE_URL", value = "http://dashboard-service.${local.name_prefix}.local:8084" }
        ]
      ) : []

      secrets = var.ssm_secrets

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.services[each.key].name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command = [
          "CMD-SHELL",
          "curl -f http://localhost:${each.value.port + 10}${each.value.health_path} || exit 1"
        ]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 60
      }

      # Container security
      readonlyRootFilesystem = false  # Spring Boot needs temp files
      privileged             = false
      user                   = "1001"

      # Resource limits
      ulimits = [
        {
          name      = "nofile"
          softLimit = 65536
          hardLimit = 65536
        }
      ]

      # Mount points for configuration and temp files
      mountPoints = [
        {
          sourceVolume  = "temp"
          containerPath = "/tmp"
          readOnly      = false
        }
      ]
    }
  ])

  # Temporary filesystem for Spring Boot
  volume {
    name = "temp"

    efs_volume_configuration {
      file_system_id = aws_efs_file_system.temp.id
    }
  }

  tags = merge(var.tags, {
    Name    = "${local.name_prefix}-${each.key}-task"
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# EFS File System for temporary files
# ---------------------------------------------------------------------------
resource "aws_efs_file_system" "temp" {
  creation_token   = "${local.name_prefix}-temp-fs"
  performance_mode = "generalPurpose"
  throughput_mode  = "provisioned"
  provisioned_throughput_in_mibps = 10

  lifecycle_policy {
    transition_to_ia = "AFTER_30_DAYS"
  }

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-temp-fs"
  })
}

resource "aws_efs_mount_target" "temp" {
  count = length(var.private_subnet_ids)

  file_system_id  = aws_efs_file_system.temp.id
  subnet_id       = var.private_subnet_ids[count.index]
  security_groups = [aws_security_group.efs.id]
}

resource "aws_security_group" "efs" {
  name        = "${local.name_prefix}-efs-sg"
  description = "Security group for EFS mount targets"
  vpc_id      = var.vpc_id

  ingress {
    from_port       = 2049
    to_port         = 2049
    protocol        = "tcp"
    security_groups = values(aws_security_group.services)[*].id
  }

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-efs-sg"
  })
}

# ---------------------------------------------------------------------------
# ECS Services — Per Microservice
# ---------------------------------------------------------------------------
resource "aws_ecs_service" "services" {
  for_each = local.services

  name            = "${local.name_prefix}-${each.key}"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.services[each.key].arn
  desired_count   = each.value.desired_count
  launch_type     = null

  # Use capacity provider strategy
  capacity_provider_strategy {
    capacity_provider = var.environment == "prod" ? "FARGATE" : "FARGATE_SPOT"
    weight            = 1
    base              = var.environment == "prod" ? 1 : 0
  }

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [aws_security_group.services[each.key].id]
    assign_public_ip = false
  }

  # Load balancer configuration (only for API Gateway initially)
  dynamic "load_balancer" {
    for_each = each.key == "api-gateway" ? [1] : []
    content {
      target_group_arn = var.alb_target_group_arn
      container_name   = each.key
      container_port   = each.value.port
    }
  }

  # Service discovery
  service_registries {
    registry_arn = aws_service_discovery_service.services[each.key].arn
  }

  # Deployment configuration
  deployment_controller {
    type = var.enable_blue_green ? "CODE_DEPLOY" : "ECS"
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  # Enable ECS Exec for debugging in non-prod
  enable_execute_command = var.environment != "prod"

  wait_for_steady_state = true

  lifecycle {
    ignore_changes = [
      desired_count,
      task_definition
    ]
  }

  tags = merge(var.tags, {
    Name    = "${local.name_prefix}-${each.key}-service"
    Service = each.key
  })

  depends_on = [
    aws_ecs_cluster.main,
    aws_service_discovery_service.services
  ]
}

# ---------------------------------------------------------------------------
# Auto Scaling — Per Service
# ---------------------------------------------------------------------------
resource "aws_appautoscaling_target" "services" {
  for_each = local.services

  max_capacity       = var.environment == "prod" ? each.value.desired_count * 3 : each.value.desired_count * 2
  min_capacity       = each.value.desired_count
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.services[each.key].name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"

  tags = merge(var.tags, {
    Service = each.key
  })
}

# CPU-based auto scaling
resource "aws_appautoscaling_policy" "cpu" {
  for_each = local.services

  name               = "${local.name_prefix}-${each.key}-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.services[each.key].resource_id
  scalable_dimension = aws_appautoscaling_target.services[each.key].scalable_dimension
  service_namespace  = aws_appautoscaling_target.services[each.key].service_namespace

  target_tracking_scaling_policy_configuration {
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60

    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
  }
}

# Memory-based auto scaling
resource "aws_appautoscaling_policy" "memory" {
  for_each = local.services

  name               = "${local.name_prefix}-${each.key}-memory-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.services[each.key].resource_id
  scalable_dimension = aws_appautoscaling_target.services[each.key].scalable_dimension
  service_namespace  = aws_appautoscaling_target.services[each.key].service_namespace

  target_tracking_scaling_policy_configuration {
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60

    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
  }
}

# ---------------------------------------------------------------------------
# ECS Cluster
# ---------------------------------------------------------------------------
resource "aws_ecs_cluster" "main" {
  name = "${local.name_prefix}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  configuration {
    execute_command_configuration {
      logging = "OVERRIDE"

      log_configuration {
        cloud_watch_encryption_enabled = true
        cloud_watch_log_group_name     = aws_cloudwatch_log_group.exec.name
      }
    }
  }

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-cluster"
  })
}

resource "aws_cloudwatch_log_group" "exec" {
  name              = "/ecs/${local.name_prefix}/exec"
  retention_in_days = 7

  tags = var.tags
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = var.environment == "prod" ? 1 : 0
    base              = var.environment == "prod" ? 1 : 0
  }

  dynamic "default_capacity_provider_strategy" {
    for_each = var.environment != "prod" ? [1] : []
    content {
      capacity_provider = "FARGATE_SPOT"
      weight            = 1
    }
  }
}