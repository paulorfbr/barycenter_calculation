# =============================================================================
# Variables for Blue-Green Deployment Module
# =============================================================================

variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

variable "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  type        = string
}

variable "alb_arn_suffix" {
  description = "ARN suffix of the Application Load Balancer for CloudWatch metrics"
  type        = string
}

variable "services" {
  description = "Map of services to create deployment groups for"
  type = map(object({
    service_name                = string
    target_group_name          = string
    target_group_arn_suffix    = string
    min_healthy_targets        = number
    max_response_time          = number
  }))
}

variable "deployment_config_name" {
  description = "CodeDeploy deployment configuration name"
  type        = string
  default     = "CodeDeployDefault.ECSAllAtOnceBlueGreen"
  validation {
    condition = contains([
      "CodeDeployDefault.ECSLinear10PercentEvery1Minutes",
      "CodeDeployDefault.ECSLinear10PercentEvery3Minutes",
      "CodeDeployDefault.ECSCanary10Percent5Minutes",
      "CodeDeployDefault.ECSCanary10Percent15Minutes",
      "CodeDeployDefault.ECSAllAtOnceBlueGreen"
    ], var.deployment_config_name)
    error_message = "Must be a valid ECS CodeDeploy configuration."
  }
}

variable "blue_instance_termination_delay" {
  description = "Time in minutes to wait before terminating blue instances"
  type        = number
  default     = 5
  validation {
    condition     = var.blue_instance_termination_delay >= 0 && var.blue_instance_termination_delay <= 1440
    error_message = "Termination delay must be between 0 and 1440 minutes."
  }
}

variable "notification_email" {
  description = "Email address for deployment notifications"
  type        = string
  default     = ""
}

variable "enable_deployment_hooks" {
  description = "Enable Lambda-based deployment hooks"
  type        = bool
  default     = false
}

variable "tags" {
  description = "A map of tags to assign to the resources"
  type        = map(string)
  default     = {}
}