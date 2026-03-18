#!/usr/bin/env python3
"""
Pre-deployment hook for blue-green deployments.

This Lambda function runs before a blue-green deployment starts and can perform
validation checks, scale-out operations, or other pre-deployment tasks.
"""

import json
import boto3
import logging
from typing import Dict, Any

logger = logging.getLogger()
logger.setLevel(logging.INFO)

ecs_client = boto3.client('ecs')
sns_client = boto3.client('sns')


def handler(event: Dict[str, Any], context: Any) -> Dict[str, Any]:
    """
    Pre-deployment hook handler.

    Args:
        event: CodeDeploy lifecycle event data
        context: Lambda context object

    Returns:
        Dict indicating success/failure of the hook
    """
    try:
        logger.info(f"Pre-deployment hook started: {json.dumps(event)}")

        # Extract deployment information
        deployment_id = event.get('DeploymentId')
        application_name = event.get('ApplicationName')

        # Get service information from the deployment
        service_name = extract_service_name(event)
        cluster_name = "${cluster_name}"

        # Perform pre-deployment checks
        logger.info(f"Performing pre-deployment checks for service: {service_name}")

        # Check current service status
        check_service_health(cluster_name, service_name)

        # Validate resources and capacity
        validate_cluster_capacity(cluster_name)

        # Send notification
        send_notification(
            f"Pre-deployment validation completed for {service_name}",
            f"Deployment {deployment_id} for {application_name} passed all pre-checks"
        )

        logger.info("Pre-deployment hook completed successfully")

        return {
            'statusCode': 200,
            'body': json.dumps({
                'status': 'Succeeded',
                'message': 'Pre-deployment validation completed'
            })
        }

    except Exception as e:
        logger.error(f"Pre-deployment hook failed: {str(e)}")

        # Send failure notification
        send_notification(
            f"Pre-deployment validation failed",
            f"Error: {str(e)}"
        )

        return {
            'statusCode': 500,
            'body': json.dumps({
                'status': 'Failed',
                'message': str(e)
            })
        }


def extract_service_name(event: Dict[str, Any]) -> str:
    """Extract the ECS service name from the deployment event."""
    # Parse the event to extract service name
    # This is a simplified implementation - adjust based on actual event structure
    lifecycle_event_hook_execution_id = event.get('LifecycleEventHookExecutionId', '')

    # Extract service name from deployment group name or other identifiers
    deployment_group_name = event.get('DeploymentGroupName', '')
    if deployment_group_name:
        # Assuming format: "{service-name}-deployment-group"
        service_name = deployment_group_name.replace('-deployment-group', '')
        return service_name

    raise ValueError("Could not extract service name from deployment event")


def check_service_health(cluster_name: str, service_name: str) -> None:
    """Check if the current service is healthy before starting deployment."""
    try:
        response = ecs_client.describe_services(
            cluster=cluster_name,
            services=[service_name]
        )

        if not response['services']:
            raise ValueError(f"Service {service_name} not found in cluster {cluster_name}")

        service = response['services'][0]

        # Check service status
        if service['status'] != 'ACTIVE':
            raise ValueError(f"Service {service_name} is not in ACTIVE status: {service['status']}")

        # Check running vs desired tasks
        running_count = service['runningCount']
        desired_count = service['desiredCount']

        if running_count < desired_count:
            logger.warning(
                f"Service {service_name} has {running_count} running tasks "
                f"but desires {desired_count}"
            )

        # Check if service is stable
        deployments = service['deployments']
        primary_deployment = next(
            (d for d in deployments if d['status'] == 'PRIMARY'), None
        )

        if primary_deployment and primary_deployment['runningCount'] != primary_deployment['desiredCount']:
            raise ValueError(
                f"Service {service_name} is not stable - "
                f"running: {primary_deployment['runningCount']}, "
                f"desired: {primary_deployment['desiredCount']}"
            )

        logger.info(f"Service {service_name} health check passed")

    except Exception as e:
        raise ValueError(f"Service health check failed: {str(e)}")


def validate_cluster_capacity(cluster_name: str) -> None:
    """Validate that the cluster has sufficient capacity for deployment."""
    try:
        # Get cluster information
        response = ecs_client.describe_clusters(clusters=[cluster_name])

        if not response['clusters']:
            raise ValueError(f"Cluster {cluster_name} not found")

        cluster = response['clusters'][0]

        # Check cluster status
        if cluster['status'] != 'ACTIVE':
            raise ValueError(f"Cluster {cluster_name} is not ACTIVE: {cluster['status']}")

        # For Fargate, capacity is managed by AWS, so we primarily check cluster health
        running_tasks = cluster['runningTasksCount']
        active_services = cluster['activeServicesCount']

        logger.info(
            f"Cluster {cluster_name} capacity check: "
            f"{running_tasks} running tasks, {active_services} active services"
        )

    except Exception as e:
        raise ValueError(f"Cluster capacity validation failed: {str(e)}")


def send_notification(subject: str, message: str) -> None:
    """Send SNS notification about the deployment hook status."""
    try:
        sns_topic_arn = "${sns_topic_arn}"

        sns_client.publish(
            TopicArn=sns_topic_arn,
            Subject=subject,
            Message=message
        )

        logger.info(f"Notification sent: {subject}")

    except Exception as e:
        logger.error(f"Failed to send notification: {str(e)}")
        # Don't fail the deployment for notification errors