variable "aws_region"      { type = string; default = "eu-west-1" }
variable "aws_account_id"  { type = string }
variable "image_tag"       { type = string; default = "latest" }
variable "environment"     { type = string; default = "staging" }
variable "alarm_email"     { type = string; default = "" }
variable "ci_role_arns"    { type = list(string); default = [] }
variable "tf_state_bucket" { type = string }
variable "tf_lock_table"   { type = string }
variable "certificate_arn" { type = string; default = "" }
