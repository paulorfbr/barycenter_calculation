# =============================================================================
# Outputs for ECS Microservices Module
# =============================================================================

output "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}

output "ecs_cluster_id" {
  description = "ID of the ECS cluster"
  value       = aws_ecs_cluster.main.id
}

output "ecs_cluster_arn" {
  description = "ARN of the ECS cluster"
  value       = aws_ecs_cluster.main.arn
}

output "service_discovery_namespace_id" {
  description = "ID of the service discovery namespace"
  value       = aws_service_discovery_private_dns_namespace.main.id
}

output "service_discovery_namespace_name" {
  description = "Name of the service discovery namespace"
  value       = aws_service_discovery_private_dns_namespace.main.name
}

output "service_arns" {
  description = "Map of service names to their ARNs"
  value = {
    for k, v in aws_ecs_service.services : k => v.id
  }
}

output "service_names" {
  description = "Map of service names to their ECS service names"
  value = {
    for k, v in aws_ecs_service.services : k => v.name
  }
}

output "task_definition_arns" {
  description = "Map of service names to their task definition ARNs"
  value = {
    for k, v in aws_ecs_task_definition.services : k => v.arn
  }
}

output "security_group_ids" {
  description = "Map of service names to their security group IDs"
  value = {
    for k, v in aws_security_group.services : k => v.id
  }
}

output "cloudwatch_log_groups" {
  description = "Map of service names to their CloudWatch log group names"
  value = {
    for k, v in aws_cloudwatch_log_group.services : k => v.name
  }
}

output "autoscaling_target_resource_ids" {
  description = "Map of service names to their autoscaling target resource IDs"
  value = {
    for k, v in aws_appautoscaling_target.services : k => v.resource_id
  }
}

output "efs_file_system_id" {
  description = "ID of the EFS file system for temporary files"
  value       = aws_efs_file_system.temp.id
}

output "efs_mount_target_ids" {
  description = "List of EFS mount target IDs"
  value       = aws_efs_mount_target.temp[*].id
}