variable "project_name"               { type = string }
variable "environment"                { type = string }
variable "aws_region"                 { type = string }
variable "vpc_id"                     { type = string }
variable "private_subnet_ids"         { type = list(string) }
variable "alb_security_group_id"      { type = string }
variable "alb_target_group_arn"       { type = string }
variable "ecr_repository_url"         { type = string }
variable "ecs_task_execution_role_arn" { type = string }
variable "ecs_task_role_arn"          { type = string }

variable "image_tag" {
  description = "Docker image tag to deploy (e.g. git commit SHA or semver)."
  type        = string
  default     = "latest"
}

variable "task_cpu" {
  description = "ECS task CPU units (256 = 0.25 vCPU)."
  type        = number
  default     = 512
}

variable "task_memory" {
  description = "ECS task memory in MiB."
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Initial number of ECS task replicas."
  type        = number
  default     = 2
}

variable "min_capacity" {
  description = "Minimum number of ECS tasks (auto-scaling floor)."
  type        = number
  default     = 1
}

variable "max_capacity" {
  description = "Maximum number of ECS tasks (auto-scaling ceiling)."
  type        = number
  default     = 10
}

variable "log_retention_days" {
  description = "CloudWatch log retention for ECS task logs."
  type        = number
  default     = 30
}

variable "ssm_secrets" {
  description = "List of SSM Parameter Store secrets to inject as env vars. Each item: {name, valueFrom}."
  type = list(object({
    name      = string
    valueFrom = string
  }))
  default = []
}

variable "tags" {
  type    = map(string)
  default = {}
}
