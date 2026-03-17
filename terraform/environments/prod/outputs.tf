output "alb_dns_name"     { value = module.alb.alb_dns_name }
output "ecs_cluster_name" { value = module.ecs.ecs_cluster_name }
output "ecs_service_name" { value = module.ecs.ecs_service_name }
output "ecr_repo_url"     { value = module.ecr.repository_url }
output "dashboard_url"    { value = module.cloudwatch.dashboard_url }
