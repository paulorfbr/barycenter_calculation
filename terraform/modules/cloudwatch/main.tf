# =============================================================================
# Terraform Module — CloudWatch Monitoring and Alerting
#
# Creates:
#   - CloudWatch Dashboard with key ECS and ALB metrics
#   - CloudWatch Alarms for CPU, memory, error rates, and ALB latency
#   - SNS topic for alarm notifications
#   - Log metric filters to surface application errors from CloudWatch Logs
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
# SNS Topic — alarm notifications
# ---------------------------------------------------------------------------
resource "aws_sns_topic" "alarms" {
  name = "${local.name_prefix}-alarms"

  tags = var.tags
}

resource "aws_sns_topic_subscription" "email" {
  count     = var.alarm_email != "" ? 1 : 0
  topic_arn = aws_sns_topic.alarms.arn
  protocol  = "email"
  endpoint  = var.alarm_email
}

# ---------------------------------------------------------------------------
# ECS Alarms
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_metric_alarm" "ecs_cpu_high" {
  alarm_name          = "${local.name_prefix}-ecs-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 60
  statistic           = "Average"
  threshold           = 85
  alarm_description   = "ECS service CPU utilisation above 85% for 2 minutes."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_service_name
  }

  tags = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_memory_high" {
  alarm_name          = "${local.name_prefix}-ecs-memory-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "MemoryUtilization"
  namespace           = "AWS/ECS"
  period              = 60
  statistic           = "Average"
  threshold           = 85
  alarm_description   = "ECS service memory utilisation above 85% for 2 minutes."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_service_name
  }

  tags = var.tags
}

resource "aws_cloudwatch_metric_alarm" "ecs_running_tasks_low" {
  alarm_name          = "${local.name_prefix}-ecs-running-tasks-low"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "RunningTaskCount"
  namespace           = "ECS/ContainerInsights"
  period              = 60
  statistic           = "Average"
  threshold           = var.min_running_tasks
  alarm_description   = "ECS running task count dropped below minimum (${var.min_running_tasks})."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "breaching"

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_service_name
  }

  tags = var.tags
}

# ---------------------------------------------------------------------------
# ALB Alarms
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_metric_alarm" "alb_5xx_high" {
  alarm_name          = "${local.name_prefix}-alb-5xx-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "HTTPCode_Target_5XX_Count"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Sum"
  threshold           = 10
  alarm_description   = "More than 10 HTTP 5xx errors from backend in 1 minute."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
  }

  tags = var.tags
}

resource "aws_cloudwatch_metric_alarm" "alb_latency_high" {
  alarm_name          = "${local.name_prefix}-alb-latency-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  extended_statistic  = "p99"
  threshold           = 3.0   # 3 seconds
  alarm_description   = "ALB p99 response time exceeded 3 seconds for 3 minutes."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
  }

  tags = var.tags
}

resource "aws_cloudwatch_metric_alarm" "alb_unhealthy_hosts" {
  alarm_name          = "${local.name_prefix}-alb-unhealthy-hosts"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "UnHealthyHostCount"
  namespace           = "AWS/ApplicationELB"
  period              = 60
  statistic           = "Average"
  threshold           = 0
  alarm_description   = "One or more ALB target group hosts are unhealthy."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "breaching"

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
    TargetGroup  = var.target_group_arn_suffix
  }

  tags = var.tags
}

# ---------------------------------------------------------------------------
# Log Metric Filter — surface Java exceptions from application logs
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_log_metric_filter" "java_exceptions" {
  name           = "${local.name_prefix}-java-exceptions"
  pattern        = "?ERROR ?Exception ?\"at com.logistics\""
  log_group_name = var.ecs_log_group_name

  metric_transformation {
    name          = "JavaExceptionCount"
    namespace     = "${var.project_name}/${var.environment}"
    value         = "1"
    default_value = "0"
  }
}

resource "aws_cloudwatch_metric_alarm" "java_exception_spike" {
  alarm_name          = "${local.name_prefix}-java-exception-spike"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "JavaExceptionCount"
  namespace           = "${var.project_name}/${var.environment}"
  period              = 60
  statistic           = "Sum"
  threshold           = 5
  alarm_description   = "More than 5 Java exceptions in application logs in 1 minute."
  alarm_actions       = [aws_sns_topic.alarms.arn]
  ok_actions          = [aws_sns_topic.alarms.arn]
  treat_missing_data  = "notBreaching"

  tags = var.tags
}

# ---------------------------------------------------------------------------
# CloudWatch Dashboard
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "${local.name_prefix}-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      # ECS CPU
      {
        type = "metric", x = 0, y = 0, width = 12, height = 6,
        properties = {
          title  = "ECS CPU Utilization (%)"
          region = var.aws_region
          metrics = [[
            "AWS/ECS", "CPUUtilization",
            "ClusterName", var.ecs_cluster_name,
            "ServiceName", var.ecs_service_name,
            { stat = "Average", period = 60 }
          ]]
          view   = "timeSeries"
          yAxis  = { left = { min = 0, max = 100 } }
        }
      },
      # ECS Memory
      {
        type = "metric", x = 12, y = 0, width = 12, height = 6,
        properties = {
          title  = "ECS Memory Utilization (%)"
          region = var.aws_region
          metrics = [[
            "AWS/ECS", "MemoryUtilization",
            "ClusterName", var.ecs_cluster_name,
            "ServiceName", var.ecs_service_name,
            { stat = "Average", period = 60 }
          ]]
          view   = "timeSeries"
          yAxis  = { left = { min = 0, max = 100 } }
        }
      },
      # ALB Request Count
      {
        type = "metric", x = 0, y = 6, width = 12, height = 6,
        properties = {
          title  = "ALB Request Count (per minute)"
          region = var.aws_region
          metrics = [[
            "AWS/ApplicationELB", "RequestCount",
            "LoadBalancer", var.alb_arn_suffix,
            { stat = "Sum", period = 60 }
          ]]
          view = "timeSeries"
        }
      },
      # ALB p99 Latency
      {
        type = "metric", x = 12, y = 6, width = 12, height = 6,
        properties = {
          title  = "ALB p99 Response Time (seconds)"
          region = var.aws_region
          metrics = [[
            "AWS/ApplicationELB", "TargetResponseTime",
            "LoadBalancer", var.alb_arn_suffix,
            { stat = "p99", period = 60 }
          ]]
          view = "timeSeries"
        }
      },
      # ALB HTTP 5xx
      {
        type = "metric", x = 0, y = 12, width = 12, height = 6,
        properties = {
          title  = "ALB HTTP 5xx Errors"
          region = var.aws_region
          metrics = [[
            "AWS/ApplicationELB", "HTTPCode_Target_5XX_Count",
            "LoadBalancer", var.alb_arn_suffix,
            { stat = "Sum", period = 60, color = "#d62728" }
          ]]
          view = "timeSeries"
        }
      },
      # Java Exceptions
      {
        type = "metric", x = 12, y = 12, width = 12, height = 6,
        properties = {
          title  = "Java Exception Count"
          region = var.aws_region
          metrics = [[
            "${var.project_name}/${var.environment}",
            "JavaExceptionCount",
            { stat = "Sum", period = 60, color = "#ff7f0e" }
          ]]
          view = "timeSeries"
        }
      }
    ]
  })
}
