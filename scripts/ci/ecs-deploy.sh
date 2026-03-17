#!/usr/bin/env bash
# =============================================================================
# ECS Rolling Deploy Helper
#
# Forces a new ECS service deployment and waits for it to stabilise.
# Called from CI after Terraform has updated the task definition.
#
# Usage:
#   ECS_CLUSTER=logistics-dev-cluster \
#   ECS_SERVICE=logistics-dev-service \
#   AWS_DEFAULT_REGION=eu-west-1 \
#   bash scripts/ci/ecs-deploy.sh
#
# Exit codes:
#   0 — deployment stabilised successfully
#   1 — deployment timed out or failed (circuit breaker may roll back)
# =============================================================================

set -euo pipefail

: "${ECS_CLUSTER:?Must set ECS_CLUSTER}"
: "${ECS_SERVICE:?Must set ECS_SERVICE}"
: "${AWS_DEFAULT_REGION:?Must set AWS_DEFAULT_REGION}"

TIMEOUT_SECONDS="${DEPLOY_TIMEOUT:-600}"

echo ">>> Forcing new ECS deployment..."
echo "    Cluster: ${ECS_CLUSTER}"
echo "    Service: ${ECS_SERVICE}"
echo "    Region:  ${AWS_DEFAULT_REGION}"
echo "    Timeout: ${TIMEOUT_SECONDS}s"

aws ecs update-service \
  --cluster "${ECS_CLUSTER}" \
  --service "${ECS_SERVICE}" \
  --force-new-deployment \
  --region "${AWS_DEFAULT_REGION}" \
  --no-cli-pager > /dev/null

echo ">>> Waiting for service to reach steady state..."
DEADLINE=$((SECONDS + TIMEOUT_SECONDS))

while [ "${SECONDS}" -lt "${DEADLINE}" ]; do
  STATUS=$(aws ecs describe-services \
    --cluster "${ECS_CLUSTER}" \
    --services "${ECS_SERVICE}" \
    --region "${AWS_DEFAULT_REGION}" \
    --query 'services[0].deployments[?status==`PRIMARY`].[rolloutState]' \
    --output text 2>/dev/null || echo "UNKNOWN")

  RUNNING=$(aws ecs describe-services \
    --cluster "${ECS_CLUSTER}" \
    --services "${ECS_SERVICE}" \
    --region "${AWS_DEFAULT_REGION}" \
    --query 'services[0].runningCount' \
    --output text 2>/dev/null || echo "0")

  echo "    $(date -u +%H:%M:%S) — rolloutState=${STATUS} runningCount=${RUNNING}"

  case "${STATUS}" in
    COMPLETED)
      echo ">>> Deployment completed successfully. Running tasks: ${RUNNING}"
      exit 0
      ;;
    FAILED)
      echo "ERROR: Deployment failed. ECS circuit breaker may have initiated rollback."
      aws ecs describe-services \
        --cluster "${ECS_CLUSTER}" \
        --services "${ECS_SERVICE}" \
        --region "${AWS_DEFAULT_REGION}" \
        --query 'services[0].events[:5]' \
        --output table 2>/dev/null || true
      exit 1
      ;;
  esac

  sleep 15
done

echo "ERROR: Deployment timed out after ${TIMEOUT_SECONDS} seconds."
exit 1
