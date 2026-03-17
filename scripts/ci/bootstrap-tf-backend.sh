#!/usr/bin/env bash
# =============================================================================
# Bootstrap Terraform Remote State Backend
#
# Run this ONCE per AWS account before the first Terraform apply.
# Creates:
#   - S3 bucket for Terraform state (versioned, encrypted, access-logged)
#   - DynamoDB table for state locking
#   - S3 bucket for access logs
#
# Usage:
#   AWS_DEFAULT_REGION=eu-west-1 \
#   TF_STATE_BUCKET=my-org-logistics-tfstate \
#   TF_LOCK_TABLE=my-org-logistics-tflock \
#   bash scripts/ci/bootstrap-tf-backend.sh
# =============================================================================

set -euo pipefail

: "${AWS_DEFAULT_REGION:?Must set AWS_DEFAULT_REGION}"
: "${TF_STATE_BUCKET:?Must set TF_STATE_BUCKET}"
: "${TF_LOCK_TABLE:?Must set TF_LOCK_TABLE}"

ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
LOG_BUCKET="${TF_STATE_BUCKET}-access-logs"

echo "Account: ${ACCOUNT_ID}"
echo "Region:  ${AWS_DEFAULT_REGION}"
echo "State bucket: ${TF_STATE_BUCKET}"
echo "Lock table:   ${TF_LOCK_TABLE}"
echo ""

# ---- S3 access log bucket --------------------------------------------------
echo ">>> Creating access log bucket: ${LOG_BUCKET}"
if ! aws s3api head-bucket --bucket "${LOG_BUCKET}" 2>/dev/null; then
  aws s3api create-bucket \
    --bucket "${LOG_BUCKET}" \
    --region "${AWS_DEFAULT_REGION}" \
    $([ "${AWS_DEFAULT_REGION}" != "us-east-1" ] && echo "--create-bucket-configuration LocationConstraint=${AWS_DEFAULT_REGION}") \
    --no-cli-pager
fi

aws s3api put-public-access-block \
  --bucket "${LOG_BUCKET}" \
  --public-access-block-configuration \
    "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"

aws s3api put-bucket-server-side-encryption \
  --bucket "${LOG_BUCKET}" \
  --server-side-encryption-configuration \
    '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"},"BucketKeyEnabled":true}]}'

echo ">>> Access log bucket ready."

# ---- S3 state bucket -------------------------------------------------------
echo ">>> Creating Terraform state bucket: ${TF_STATE_BUCKET}"
if ! aws s3api head-bucket --bucket "${TF_STATE_BUCKET}" 2>/dev/null; then
  aws s3api create-bucket \
    --bucket "${TF_STATE_BUCKET}" \
    --region "${AWS_DEFAULT_REGION}" \
    $([ "${AWS_DEFAULT_REGION}" != "us-east-1" ] && echo "--create-bucket-configuration LocationConstraint=${AWS_DEFAULT_REGION}") \
    --no-cli-pager
fi

# Versioning — required for state recovery
aws s3api put-bucket-versioning \
  --bucket "${TF_STATE_BUCKET}" \
  --versioning-configuration Status=Enabled

# Encryption at rest
aws s3api put-bucket-server-side-encryption \
  --bucket "${TF_STATE_BUCKET}" \
  --server-side-encryption-configuration \
    '{"Rules":[{"ApplyServerSideEncryptionByDefault":{"SSEAlgorithm":"AES256"},"BucketKeyEnabled":true}]}'

# Block all public access
aws s3api put-public-access-block \
  --bucket "${TF_STATE_BUCKET}" \
  --public-access-block-configuration \
    "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"

# Enable server access logging
aws s3api put-bucket-logging \
  --bucket "${TF_STATE_BUCKET}" \
  --bucket-logging-status "{
    \"LoggingEnabled\": {
      \"TargetBucket\": \"${LOG_BUCKET}\",
      \"TargetPrefix\": \"tf-state-access-logs/\"
    }
  }"

# Lifecycle: expire old state file versions after 90 days
aws s3api put-bucket-lifecycle-configuration \
  --bucket "${TF_STATE_BUCKET}" \
  --lifecycle-configuration '{
    "Rules": [{
      "ID": "expire-noncurrent-versions",
      "Status": "Enabled",
      "Filter": {},
      "NoncurrentVersionExpiration": {"NoncurrentDays": 90},
      "AbortIncompleteMultipartUpload": {"DaysAfterInitiation": 7}
    }]
  }'

echo ">>> Terraform state bucket ready."

# ---- DynamoDB lock table ---------------------------------------------------
echo ">>> Creating DynamoDB lock table: ${TF_LOCK_TABLE}"
if ! aws dynamodb describe-table --table-name "${TF_LOCK_TABLE}" 2>/dev/null; then
  aws dynamodb create-table \
    --table-name "${TF_LOCK_TABLE}" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --sse-specification Enabled=true \
    --region "${AWS_DEFAULT_REGION}" \
    --no-cli-pager

  echo "Waiting for table to become active..."
  aws dynamodb wait table-exists \
    --table-name "${TF_LOCK_TABLE}" \
    --region "${AWS_DEFAULT_REGION}"
fi
echo ">>> DynamoDB lock table ready."

echo ""
echo "Backend bootstrap complete."
echo ""
echo "Set these in GitLab CI/CD variables:"
echo "  TF_STATE_BUCKET = ${TF_STATE_BUCKET}"
echo "  TF_STATE_LOCK_TABLE = ${TF_LOCK_TABLE}"
echo "  AWS_DEFAULT_REGION = ${AWS_DEFAULT_REGION}"
