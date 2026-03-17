variable "project_name"          { type = string }
variable "environment"           { type = string }
variable "aws_region"            { type = string }
variable "ecs_cluster_name"      { type = string }
variable "ecs_service_name"      { type = string }
variable "ecs_log_group_name"    { type = string }
variable "alb_arn_suffix"        { type = string }
variable "target_group_arn_suffix" { type = string }

variable "alarm_email" {
  description = "Email address to receive CloudWatch alarm notifications. Leave empty to disable."
  type        = string
  default     = ""
}

variable "min_running_tasks" {
  description = "Minimum expected running task count for the low-task alarm."
  type        = number
  default     = 1
}

variable "tags" {
  type    = map(string)
  default = {}
}
