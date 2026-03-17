variable "aws_region"      { type = string; default = "eu-west-1" }
variable "aws_account_id"  { type = string }
variable "image_tag"       { type = string; default = "latest" }
variable "environment"     { type = string; default = "prod" }
variable "ci_role_arns"    { type = list(string); default = [] }
variable "tf_state_bucket" { type = string }
variable "tf_lock_table"   { type = string }

variable "alarm_email" {
  description = "Required: email for production alarms."
  type        = string
}

variable "certificate_arn" {
  description = "Required: ACM certificate ARN for HTTPS listener."
  type        = string
}
