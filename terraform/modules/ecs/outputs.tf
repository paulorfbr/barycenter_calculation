output "ecs_cluster_id"        { value = aws_ecs_cluster.main.id }
output "ecs_cluster_name"      { value = aws_ecs_cluster.main.name }
output "ecs_cluster_arn"       { value = aws_ecs_cluster.main.arn }
output "ecs_service_name"      { value = aws_ecs_service.app.name }
output "ecs_service_id"        { value = aws_ecs_service.app.id }
output "ecs_task_sg_id"        { value = aws_security_group.ecs_tasks.id }
output "cloudwatch_log_group"  { value = aws_cloudwatch_log_group.ecs_tasks.name }
