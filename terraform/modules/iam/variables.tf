variable "project_name" { type = string }
variable "environment"  { type = string }
variable "aws_account_id" { type = string }
variable "aws_region"     { type = string }

variable "kms_key_arn" {
  description = "ARN of the KMS key used to decrypt SSM SecureString parameters. Leave empty to allow all."
  type        = string
  default     = ""
}

variable "ecr_repository_arns" {
  description = "List of ECR repository ARNs the CI deployer is allowed to push to."
  type        = list(string)
  default     = []
}

variable "tf_state_bucket" {
  description = "S3 bucket name used for Terraform remote state."
  type        = string
}

variable "tf_lock_table" {
  description = "DynamoDB table name used for Terraform state locking."
  type        = string
}

variable "tags" {
  type    = map(string)
  default = {}
}
