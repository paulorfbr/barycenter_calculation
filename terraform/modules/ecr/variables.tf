variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "repository_name" {
  description = "Name of the ECR repository."
  type        = string
}

variable "max_image_count" {
  description = "Maximum number of tagged images to retain per lifecycle policy."
  type        = number
  default     = 20
}

variable "ecs_task_execution_role_arns" {
  description = "List of ECS task execution role ARNs that can pull from this repository."
  type        = list(string)
  default     = []
}

variable "ci_role_arns" {
  description = "List of IAM role/user ARNs used by CI to push images."
  type        = list(string)
  default     = []
}

variable "tags" {
  type    = map(string)
  default = {}
}
