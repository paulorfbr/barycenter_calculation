# GitLab CI/CD Variables Setup Guide

Configure these in: **Settings > CI/CD > Variables**

## Required Variables

| Variable | Scope | Protected | Masked | Description |
|---|---|---|---|---|
| `AWS_ACCESS_KEY_ID` | All | Yes | No | IAM deployer user key ID |
| `AWS_SECRET_ACCESS_KEY` | All | Yes | Yes | IAM deployer user secret |
| `AWS_DEFAULT_REGION` | All | No | No | e.g. `eu-west-1` |
| `ECR_REGISTRY` | All | No | No | `<account>.dkr.ecr.<region>.amazonaws.com` |
| `ECR_REPOSITORY` | All | No | No | `logistics-api` |
| `TF_STATE_BUCKET` | All | No | No | S3 bucket name for Terraform state |
| `TF_STATE_LOCK_TABLE` | All | No | No | DynamoDB table for state locking |
| `TF_VAR_aws_account_id` | All | No | No | AWS account ID (12 digits) |

## Per-Environment Variables

| Variable | Environment | Description |
|---|---|---|
| `ALB_DNS_DEV` | `development` | ALB DNS after first dev deploy (set manually) |
| `ALB_DNS_STAGING` | `staging` | ALB DNS after first staging deploy |
| `ALB_DNS_PROD` | `production` | ALB DNS after first prod deploy |
| `TF_VAR_certificate_arn` | `staging`, `production` | ACM certificate ARN for HTTPS |
| `TF_VAR_alarm_email` | `production` | Ops team email for CloudWatch alarms |

## One-Time Bootstrap

Before the first pipeline run, execute from your local machine with AWS Admin credentials:

```bash
export AWS_DEFAULT_REGION=eu-west-1
export TF_STATE_BUCKET=my-org-logistics-tfstate
export TF_LOCK_TABLE=my-org-logistics-tflock
bash scripts/ci/bootstrap-tf-backend.sh
```

Then create a least-privilege IAM user for CI:

```bash
# Create the CI user
aws iam create-user --user-name gitlab-ci-logistics

# Attach the deployer policy (created by the iam Terraform module after first apply)
# aws iam attach-user-policy --user-name gitlab-ci-logistics \
#   --policy-arn arn:aws:iam::<account>:policy/logistics-dev-ci-deployer-policy

# Create access keys and add to GitLab CI variables
aws iam create-access-key --user-name gitlab-ci-logistics
```
