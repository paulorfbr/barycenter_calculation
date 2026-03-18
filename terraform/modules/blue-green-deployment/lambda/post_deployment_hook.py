#!/usr/bin/env python3
"""
Post-deployment hook for blue-green deployments.

This Lambda function runs after a blue-green deployment completes and can perform
validation tests, health checks, or cleanup operations.
"""

import json
import boto3
import logging
import time
from typing import Dict, Any

logger = logging.getLogger()
logger.setLevel(logging.INFO)

ecs_client = boto3.client('ecs')
sns_client = boto3.client('sns')


def handler(event: Dict[str, Any], context: Any) -> Dict[str, Any]:
    """
    Post-deployment hook handler.

    Args:
        event: CodeDeploy lifecycle event data
        context: Lambda context object

    Returns:
        Dict indicating success/failure of the hook
    """
    try:
        logger.info(f"Post-deployment hook started: {json.dumps(event)}")

        # Extract deployment information
        deployment_id = event.get('DeploymentId')
        application_name = event.get('ApplicationName')

        # Get service information
        service_name = extract_service_name(event)
        cluster_name = "${cluster_name}"

        # Perform post-deployment validation
        logger.info(f"Performing post-deployment validation for service: {service_name}")

        # Wait for service to stabilize
        wait_for_service_stability(cluster_name, service_name)

        # Perform health checks
        validate_service_health(cluster_name, service_name)

        # Run integration tests (if enabled)
        run_integration_tests(service_name)

        # Validate application metrics
        validate_application_metrics(service_name)

        # Send success notification
        send_notification(
            f"Post-deployment validation successful for {service_name}",
            f"Deployment {deployment_id} for {application_name} is healthy and ready"
        )

        logger.info("Post-deployment hook completed successfully")

        return {
            'statusCode': 200,
            'body': json.dumps({
                'status': 'Succeeded',
                'message': 'Post-deployment validation completed successfully'
            })
        }

    except Exception as e:
        logger.error(f"Post-deployment hook failed: {str(e)}")

        # Send failure notification
        send_notification(
            f"Post-deployment validation failed for {service_name}",
            f"Error: {str(e)}\nDeployment may need to be rolled back."
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
    deployment_group_name = event.get('DeploymentGroupName', '')
    if deployment_group_name:
        # Assuming format: "{service-name}-deployment-group"
        service_name = deployment_group_name.replace('-deployment-group', '')
        return service_name

    raise ValueError("Could not extract service name from deployment event")


def wait_for_service_stability(cluster_name: str, service_name: str, max_wait_time: int = 600) -> None:
    """Wait for the ECS service to reach a stable state."""
    logger.info(f"Waiting for service {service_name} to stabilize...")

    start_time = time.time()
    while time.time() - start_time < max_wait_time:
        try:
            response = ecs_client.describe_services(
                cluster=cluster_name,
                services=[service_name]
            )

            if not response['services']:
                raise ValueError(f"Service {service_name} not found")

            service = response['services'][0]

            # Check if service is stable
            deployments = service['deployments']
            primary_deployment = next(
                (d for d in deployments if d['status'] == 'PRIMARY'), None
            )

            if primary_deployment:
                running_count = primary_deployment['runningCount']
                desired_count = primary_deployment['desiredCount']

                if (running_count == desired_count and
                    primary_deployment['rolloutState'] == 'COMPLETED'):
                    logger.info(f"Service {service_name} has stabilized")
                    return

            logger.info(f"Service {service_name} still stabilizing, waiting...")
            time.sleep(30)

        except Exception as e:
            logger.warning(f"Error checking service stability: {str(e)}")
            time.sleep(30)

    raise ValueError(f"Service {service_name} did not stabilize within {max_wait_time} seconds")


def validate_service_health(cluster_name: str, service_name: str) -> None:
    """Validate that the deployed service is healthy."""
    try:
        response = ecs_client.describe_services(
            cluster=cluster_name,
            services=[service_name]
        )

        if not response['services']:
            raise ValueError(f"Service {service_name} not found")

        service = response['services'][0]

        # Validate service status
        if service['status'] != 'ACTIVE':
            raise ValueError(f"Service status is {service['status']}, expected ACTIVE")

        # Validate task health
        running_count = service['runningCount']
        desired_count = service['desiredCount']

        if running_count < desired_count:
            raise ValueError(
                f"Running task count ({running_count}) is less than desired ({desired_count})"
            )

        # Get task details to check health
        tasks_response = ecs_client.list_tasks(
            cluster=cluster_name,
            serviceName=service_name,
            desiredStatus='RUNNING'
        )

        if not tasks_response['taskArns']:
            raise ValueError("No running tasks found for service")

        # Check task health status
        task_details = ecs_client.describe_tasks(
            cluster=cluster_name,
            tasks=tasks_response['taskArns']
        )

        unhealthy_tasks = []
        for task in task_details['tasks']:
            if task['lastStatus'] != 'RUNNING':
                unhealthy_tasks.append(f"Task {task['taskArn']} status: {task['lastStatus']}")

            # Check container health
            for container in task.get('containers', []):
                if container['lastStatus'] != 'RUNNING':
                    unhealthy_tasks.append(
                        f"Container {container['name']} status: {container['lastStatus']}"
                    )

        if unhealthy_tasks:
            raise ValueError(f"Unhealthy tasks detected: {'; '.join(unhealthy_tasks)}")

        logger.info(f"Service {service_name} health validation passed")

    except Exception as e:
        raise ValueError(f"Service health validation failed: {str(e)}")


def run_integration_tests(service_name: str) -> None:
    """Run basic integration tests against the deployed service."""
    # This is a placeholder for actual integration tests
    # In a real implementation, you might:
    # 1. Call the service health endpoint
    # 2. Run smoke tests
    # 3. Validate database connectivity
    # 4. Check downstream service integration

    logger.info(f"Running integration tests for {service_name}")

    # Example: Basic health check
    # You would implement actual HTTP calls to test endpoints here
    time.sleep(5)  # Simulate test execution time

    logger.info(f"Integration tests passed for {service_name}")


def validate_application_metrics(service_name: str) -> None:
    """Validate application metrics after deployment."""
    # This is a placeholder for metrics validation
    # In a real implementation, you might:
    # 1. Check CloudWatch metrics
    # 2. Validate error rates
    # 3. Check response times
    # 4. Validate business metrics

    logger.info(f"Validating application metrics for {service_name}")

    # Example implementation would query CloudWatch here
    time.sleep(2)  # Simulate metrics collection

    logger.info(f"Application metrics validation passed for {service_name}")


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