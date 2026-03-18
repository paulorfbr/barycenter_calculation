# =============================================================================
# Terraform Module — Blue-Green Deployment with AWS CodeDeploy
#
# Creates:
#   - CodeDeploy application for ECS blue-green deployments
#   - Deployment groups for each microservice
#   - IAM roles and policies for CodeDeploy
#   - CloudWatch alarms for automatic rollback
#   - SNS notifications for deployment events
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
}

# ---------------------------------------------------------------------------
# CodeDeploy Application
# ---------------------------------------------------------------------------
resource "aws_codedeploy_app" "main" {
  compute_platform = "ECS"
  name             = "${local.name_prefix}-codedeploy"

  tags = var.tags
}

# ---------------------------------------------------------------------------
# IAM Role for CodeDeploy
# ---------------------------------------------------------------------------
resource "aws_iam_role" "codedeploy" {
  name = "${local.name_prefix}-codedeploy-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "codedeploy.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "codedeploy" {
  role       = aws_iam_role.codedeploy.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployRoleForECS"
}

# Additional permissions for blue-green deployments
resource "aws_iam_role_policy" "codedeploy_additional" {
  name = "${local.name_prefix}-codedeploy-additional"
  role = aws_iam_role.codedeploy.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:CreateTaskSet",
          "ecs:UpdateTaskSet",
          "ecs:DeleteTaskSet",
          "ecs:DescribeTaskSets",
          "elasticloadbalancing:DescribeTargetGroups",
          "elasticloadbalancing:DescribeListeners",
          "elasticloadbalancing:ModifyListener",
          "elasticloadbalancing:DescribeRules",
          "elasticloadbalancing:ModifyRule",
          "lambda:InvokeFunction",
          "cloudwatch:DescribeAlarms",
          "sns:Publish"
        ]
        Resource = "*"
      }
    ]
  })
}

# ---------------------------------------------------------------------------
# SNS Topic for Deployment Notifications
# ---------------------------------------------------------------------------
resource "aws_sns_topic" "deployments" {
  name = "${local.name_prefix}-deployments"

  tags = var.tags
}

resource "aws_sns_topic_subscription" "email" {
  count = var.notification_email != "" ? 1 : 0

  topic_arn = aws_sns_topic.deployments.arn
  protocol  = "email"
  endpoint  = var.notification_email
}

# ---------------------------------------------------------------------------
# CloudWatch Alarms for Rollback Triggers
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_metric_alarm" "alb_target_health" {
  for_each = var.services

  alarm_name          = "${local.name_prefix}-${each.key}-target-health"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "HealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  period              = "60"
  statistic           = "Average"
  threshold           = each.value.min_healthy_targets
  alarm_description   = "This metric monitors healthy targets for ${each.key}"
  alarm_actions       = [aws_sns_topic.deployments.arn]

  dimensions = {
    TargetGroup  = each.value.target_group_arn_suffix
    LoadBalancer = var.alb_arn_suffix
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

resource "aws_cloudwatch_metric_alarm" "alb_response_time" {
  for_each = var.services

  alarm_name          = "${local.name_prefix}-${each.key}-response-time"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  period              = "60"
  statistic           = "Average"
  threshold           = each.value.max_response_time
  alarm_description   = "This metric monitors response time for ${each.key}"
  alarm_actions       = [aws_sns_topic.deployments.arn]

  dimensions = {
    TargetGroup  = each.value.target_group_arn_suffix
    LoadBalancer = var.alb_arn_suffix
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

resource "aws_cloudwatch_metric_alarm" "ecs_service_cpu" {
  for_each = var.services

  alarm_name          = "${local.name_prefix}-${each.key}-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = "300"
  statistic           = "Average"
  threshold           = "90"
  alarm_description   = "This metric monitors CPU utilization for ${each.key}"
  alarm_actions       = [aws_sns_topic.deployments.arn]

  dimensions = {
    ServiceName = each.value.service_name
    ClusterName = var.ecs_cluster_name
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# CodeDeploy Deployment Groups
# ---------------------------------------------------------------------------
resource "aws_codedeploy_deployment_group" "services" {
  for_each = var.services

  app_name              = aws_codedeploy_app.main.name
  deployment_group_name = "${each.key}-deployment-group"
  service_role_arn      = aws_iam_role.codedeploy.arn

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    green_fleet_provisioning_option {
      action = "COPY_AUTO_SCALING_GROUP"
    }

    terminate_blue_instances_on_deployment_success {
      action                         = "TERMINATE"
      termination_wait_time_in_minutes = var.blue_instance_termination_delay
    }
  }

  ecs_service {
    cluster_name = var.ecs_cluster_name
    service_name = each.value.service_name
  }

  load_balancer_info {
    target_group_info {
      name = each.value.target_group_name
    }
  }

  deployment_style {
    deployment_option = "WITH_TRAFFIC_CONTROL"
    deployment_type   = "BLUE_GREEN"
  }

  # Automatic rollback configuration
  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE", "DEPLOYMENT_STOP_ON_ALARM"]
  }

  alarm_configuration {
    enabled = true
    alarms = [
      aws_cloudwatch_metric_alarm.alb_target_health[each.key].alarm_name,
      aws_cloudwatch_metric_alarm.alb_response_time[each.key].alarm_name,
      aws_cloudwatch_metric_alarm.ecs_service_cpu[each.key].alarm_name
    ]
  }

  # Deployment configuration
  deployment_config_name = var.deployment_config_name

  # Triggers for SNS notifications
  trigger_configuration {
    trigger_events = [
      "DeploymentStart",
      "DeploymentSuccess",
      "DeploymentFailure",
      "DeploymentStop",
      "DeploymentRollback"
    ]
    trigger_name       = "${each.key}-deployment-trigger"
    trigger_target_arn = aws_sns_topic.deployments.arn
  }

  tags = merge(var.tags, {
    Service = each.key
  })

  depends_on = [aws_iam_role_policy_attachment.codedeploy]
}

# ---------------------------------------------------------------------------
# Lambda Functions for Pre/Post-deployment Hooks
# ---------------------------------------------------------------------------
resource "aws_lambda_function" "pre_deployment_hook" {
  count = var.enable_deployment_hooks ? 1 : 0

  filename         = data.archive_file.pre_deployment_hook[0].output_path
  function_name    = "${local.name_prefix}-pre-deployment-hook"
  role            = aws_iam_role.lambda[0].arn
  handler         = "index.handler"
  source_code_hash = data.archive_file.pre_deployment_hook[0].output_base64sha256
  runtime         = "python3.11"
  timeout         = 60

  environment {
    variables = {
      CLUSTER_NAME = var.ecs_cluster_name
      SNS_TOPIC_ARN = aws_sns_topic.deployments.arn
    }
  }

  tags = var.tags
}

resource "aws_lambda_function" "post_deployment_hook" {
  count = var.enable_deployment_hooks ? 1 : 0

  filename         = data.archive_file.post_deployment_hook[0].output_path
  function_name    = "${local.name_prefix}-post-deployment-hook"
  role            = aws_iam_role.lambda[0].arn
  handler         = "index.handler"
  source_code_hash = data.archive_file.post_deployment_hook[0].output_base64sha256
  runtime         = "python3.11"
  timeout         = 60

  environment {
    variables = {
      CLUSTER_NAME = var.ecs_cluster_name
      SNS_TOPIC_ARN = aws_sns_topic.deployments.arn
    }
  }

  tags = var.tags
}

# Lambda deployment packages
data "archive_file" "pre_deployment_hook" {
  count = var.enable_deployment_hooks ? 1 : 0

  type        = "zip"
  output_path = "/tmp/pre-deployment-hook.zip"

  source {
    content = templatefile("${path.module}/lambda/pre_deployment_hook.py", {
      cluster_name  = var.ecs_cluster_name
      sns_topic_arn = aws_sns_topic.deployments.arn
    })
    filename = "index.py"
  }
}

data "archive_file" "post_deployment_hook" {
  count = var.enable_deployment_hooks ? 1 : 0

  type        = "zip"
  output_path = "/tmp/post-deployment-hook.zip"

  source {
    content = templatefile("${path.module}/lambda/post_deployment_hook.py", {
      cluster_name  = var.ecs_cluster_name
      sns_topic_arn = aws_sns_topic.deployments.arn
    })
    filename = "index.py"
  }
}

# IAM role for Lambda functions
resource "aws_iam_role" "lambda" {
  count = var.enable_deployment_hooks ? 1 : 0

  name = "${local.name_prefix}-deployment-hooks-lambda"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "lambda_basic" {
  count = var.enable_deployment_hooks ? 1 : 0

  role       = aws_iam_role.lambda[0].name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy" "lambda_deployment_hooks" {
  count = var.enable_deployment_hooks ? 1 : 0

  name = "${local.name_prefix}-lambda-deployment-hooks"
  role = aws_iam_role.lambda[0].id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:DescribeTasks",
          "ecs:ListTasks",
          "sns:Publish",
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "*"
      }
    ]
  })
}