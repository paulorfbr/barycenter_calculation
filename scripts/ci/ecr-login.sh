#!/usr/bin/env bash
# =============================================================================
# ECR Login Helper
# Authenticates Docker to the ECR registry for the current AWS account/region.
# Sourced or called from CI scripts that need to push/pull images.
# =============================================================================

set -euo pipefail

: "${AWS_DEFAULT_REGION:?Must set AWS_DEFAULT_REGION}"
: "${ECR_REGISTRY:?Must set ECR_REGISTRY (e.g. 123456789.dkr.ecr.eu-west-1.amazonaws.com)}"

echo ">>> Authenticating Docker to ECR: ${ECR_REGISTRY}"
aws ecr get-login-password --region "${AWS_DEFAULT_REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"
echo ">>> ECR login successful."
