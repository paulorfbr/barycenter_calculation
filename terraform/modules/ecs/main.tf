# =============================================================================
# Terraform Module — ECS Fargate Cluster + Service
#
# Creates:
#   - ECS Cluster with Container Insights enabled
#   - ECS Task Definition (Fargate, awsvpc, Java 17 fat JAR container)
#   - ECS Service with rolling-update deployment
#   - Application Auto Scaling (CPU/Memory target tracking)
#   - Security Group for tasks (inbound from ALB SG only)
#   - CloudWatch Log Group for task stdout/stderr
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
  name_prefix    = "${var.project_name}-${var.environment}"
  container_name = var.project_name
}

# ---------------------------------------------------------------------------
# CloudWatch Log Group — task stdout/stderr via awslogs driver
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_log_group" "ecs_tasks" {
  name              = "/ecs/${local.name_prefix}"
  retention_in_days = var.log_retention_days

  tags = var.tags
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

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-cluster"
  })
}

resource "aws_ecs_cluster_capacity_providers" "main" {
  cluster_name       = aws_ecs_cluster.main.name
  capacity_providers = ["FARGATE", "FARGATE_SPOT"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = var.environment == "prod" ? 1 : 0
    base              = var.environment == "prod" ? 1 : 0
  }

  # Use FARGATE_SPOT for non-prod to reduce cost
  dynamic "default_capacity_provider_strategy" {
    for_each = var.environment != "prod" ? [1] : []
    content {
      capacity_provider = "FARGATE_SPOT"
      weight            = 1
    }
  }
}

# ---------------------------------------------------------------------------
# Security Group — ECS tasks
# ---------------------------------------------------------------------------
resource "aws_security_group" "ecs_tasks" {
  name        = "${local.name_prefix}-ecs-tasks-sg"
  description = "Allow inbound from ALB SG on port 8080; outbound to internet."
  vpc_id      = var.vpc_id

  ingress {
    description     = "HTTP from ALB"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [var.alb_security_group_id]
  }

  egress {
    description = "All outbound (for ECR pull, CloudWatch, SSM via NAT)"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-ecs-tasks-sg"
  })
}

# ---------------------------------------------------------------------------
# ECS Task Definition
# ---------------------------------------------------------------------------
resource "aws_ecs_task_definition" "app" {
  family                   = "${local.name_prefix}-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.task_cpu
  memory                   = var.task_memory
  execution_role_arn       = var.ecs_task_execution_role_arn
  task_role_arn            = var.ecs_task_role_arn

  container_definitions = jsonencode([
    {
      name      = local.container_name
      image     = "${var.ecr_repository_url}:${var.image_tag}"
      essential = true

      portMappings = [{
        containerPort = 8080
        hostPort      = 8080
        protocol      = "tcp"
      }]

      # JVM and Spring environment variables
      environment = [
        { name = "SPRING_PROFILES_ACTIVE",         value = var.environment == "prod" ? "production" : var.environment },
        { name = "SERVER_PORT",                     value = "8080" },
        { name = "MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE", value = "health,info,metrics" },
        { name = "JAVA_OPTS",                       value = "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom" }
      ]

      # Secrets from SSM Parameter Store (injected as environment variables)
      secrets = var.ssm_secrets

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.ecs_tasks.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "ecs"
        }
      }

      healthCheck = {
        command     = ["CMD-SHELL", "wget -qO- http://localhost:8080/actuator/health | grep -q '\"status\":\"UP\"' || exit 1"]
        interval    = 30
        timeout     = 10
        retries     = 3
        startPeriod = 60
      }

      # Read-only root filesystem for container hardening
      readonlyRootFilesystem = false   # Spring Boot writes temp files; set true only if tmpfs added

      # No privilege escalation
      privileged             = false
      user                   = "1001"   # Non-root UID matching Dockerfile 'logistics' user
    }
  ])

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-task-definition"
  })
}

# ---------------------------------------------------------------------------
# ECS Service — rolling update deployment strategy
# ---------------------------------------------------------------------------
resource "aws_ecs_service" "app" {
  name            = "${local.name_prefix}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.desired_count
  launch_type     = null   # Managed by capacity provider strategy below

  capacity_provider_strategy {
    capacity_provider = var.environment == "prod" ? "FARGATE" : "FARGATE_SPOT"
    weight            = 1
    base              = var.environment == "prod" ? 1 : 0
  }

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false   # Tasks are in private subnets; use NAT for outbound
  }

  load_balancer {
    target_group_arn = var.alb_target_group_arn
    container_name   = local.container_name
    container_port   = 8080
  }

  deployment_controller {
    type = "ECS"   # Rolling update
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true   # Auto-rollback on failed deployment
  }

  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  # Enable ECS Exec for debugging (can be disabled in production)
  enable_execute_command = var.environment != "prod"

  # Wait for service to stabilise before Terraform marks apply as complete
  wait_for_steady_state = true

  lifecycle {
    ignore_changes = [
      # Ignore desired_count changes — managed by Auto Scaling after initial deploy
      desired_count,
      # Ignore task definition changes — CI handles image updates
      task_definition
    ]
  }

  tags = merge(var.tags, {
    Name = "${local.name_prefix}-service"
  })

  depends_on = [aws_ecs_cluster.main]
}

# ---------------------------------------------------------------------------
# Application Auto Scaling
# ---------------------------------------------------------------------------
resource "aws_appautoscaling_target" "ecs" {
  max_capacity       = var.max_capacity
  min_capacity       = var.min_capacity
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.app.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

# Scale out/in based on average CPU utilisation
resource "aws_appautoscaling_policy" "ecs_cpu" {
  name               = "${local.name_prefix}-cpu-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs.service_namespace

  target_tracking_scaling_policy_configuration {
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60

    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
  }
}

# Scale out/in based on average memory utilisation
resource "aws_appautoscaling_policy" "ecs_memory" {
  name               = "${local.name_prefix}-memory-scaling"
  policy_type        = "TargetTrackingScaling"
  resource_id        = aws_appautoscaling_target.ecs.resource_id
  scalable_dimension = aws_appautoscaling_target.ecs.scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs.service_namespace

  target_tracking_scaling_policy_configuration {
    target_value       = 80.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60

    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageMemoryUtilization"
    }
  }
}
