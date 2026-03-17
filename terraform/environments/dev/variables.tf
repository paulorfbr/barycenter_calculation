variable "aws_region" {
  type    = string
  default = "eu-west-1"
}

variable "aws_account_id" {
  description = "AWS account ID."
  type        = string
}

variable "image_tag" {
  description = "Docker image tag to deploy. Set by CI pipeline via -var flag."
  type        = string
  default     = "latest"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "alarm_email" {
  description = "Email to receive CloudWatch alarm notifications."
  type        = string
  default     = ""
}

variable "ci_role_arns" {
  description = "IAM role ARNs for CI to push ECR images."
  type        = list(string)
  default     = []
}

variable "tf_state_bucket" {
  description = "S3 bucket for Terraform remote state."
  type        = string
}

variable "tf_lock_table" {
  description = "DynamoDB table for Terraform state locking."
  type        = string
}
