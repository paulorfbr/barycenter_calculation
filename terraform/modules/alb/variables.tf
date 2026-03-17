variable "project_name"       { type = string }
variable "environment"        { type = string }
variable "vpc_id"             { type = string }
variable "vpc_cidr"           { type = string }
variable "public_subnet_ids"  { type = list(string) }
variable "aws_account_id"     { type = string }

variable "certificate_arn" {
  description = "ACM certificate ARN for the HTTPS listener. Leave empty for HTTP-only (dev)."
  type        = string
  default     = ""
}

variable "enable_deletion_protection" {
  description = "Enable ALB deletion protection. Should be true for prod."
  type        = bool
  default     = false
}

variable "log_retention_days" {
  description = "ALB access log retention in S3 (days)."
  type        = number
  default     = 30
}

variable "tags" {
  type    = map(string)
  default = {}
}
