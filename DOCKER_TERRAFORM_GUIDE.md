# Enhanced Docker & Terraform Infrastructure Guide

## Overview

This guide covers the enhanced Docker and Terraform infrastructure for the Java Logistics Platform, implementing enterprise best practices including:

- **Multi-stage Docker builds** with security hardening
- **Microservices orchestration** with ECS Fargate
- **Blue-green deployments** with CodeDeploy
- **Comprehensive monitoring** and logging
- **CI/CD pipelines** with automated testing
- **Security scanning** and compliance
- **Auto-scaling** and performance optimization

## Architecture Components

### 1. Docker Infrastructure

#### Core Services
- **JavaFX Desktop App** - Cross-platform GUI with X11 support
- **API Gateway** - Spring Cloud Gateway with rate limiting
- **Company Service** - Company management microservice
- **Site Service** - Consumption site management
- **Calculation Service** - Barycenter calculation algorithms
- **Dashboard Service** - KPI aggregation and analytics

#### Infrastructure Services
- **PostgreSQL** - Primary database with connection pooling
- **Redis** - Caching and session storage
- **Kafka + Zookeeper** - Event streaming platform
- **Prometheus + Grafana** - Metrics and monitoring
- **Zipkin/Jaeger** - Distributed tracing

### 2. Terraform Modules

#### Core Infrastructure
- **VPC Module** - Multi-AZ networking with NAT gateways
- **ECS Microservices** - Container orchestration
- **ALB Module** - Load balancing with SSL termination
- **IAM Module** - Role-based access control
- **ECR Module** - Container registry management

#### Advanced Features
- **Blue-Green Deployment** - Zero-downtime deployments
- **CI/CD Pipeline** - Automated build and deployment
- **CloudWatch Module** - Comprehensive monitoring
- **Backup Module** - Data protection and disaster recovery

## Quick Start

### Prerequisites

```bash
# Required tools
- Docker 24.0+
- Docker Compose 2.20+
- Terraform 1.6+
- AWS CLI 2.0+
- Java 17+
- Maven 3.9+
```

### Local Development

1. **Start Development Environment**
```bash
# Basic microservices
docker compose -f docker-compose.microservices.yml up -d

# With development tools
docker compose -f docker-compose.microservices.yml \
               -f docker-compose.development.yml up -d

# Access services
echo "API Gateway: http://localhost:8080"
echo "Grafana: http://localhost:3000"
echo "Kafka UI: http://localhost:8080"
echo "pgAdmin: http://localhost:8888"
```

2. **JavaFX Desktop Application**
```bash
# Build JavaFX app with X11 support
docker build -f docker/Dockerfile.javafx -t logistics-javafx .

# Run with X11 forwarding (Linux)
docker run --rm -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
  -v ~/.Xauthority:/home/logistics/.Xauthority:rw \
  logistics-javafx

# Run on Windows (with X Server like VcXsrv)
docker run --rm -it \
  -e DISPLAY=host.docker.internal:0.0 \
  logistics-javafx
```

### Production Deployment

1. **Configure AWS Infrastructure**
```bash
# Initialize Terraform
cd terraform/environments/prod
terraform init

# Plan deployment
terraform plan -var-file="prod.tfvars"

# Deploy infrastructure
terraform apply -var-file="prod.tfvars"
```

2. **Deploy Applications**
```bash
# Build and push images
./scripts/build-and-push.sh prod

# Deploy via CodePipeline
aws codepipeline start-pipeline-execution \
  --name logistics-prod-api-gateway-pipeline
```

## Configuration Files

### Environment-Specific Configurations

#### Development (`docker-compose.development.yml`)
- Debug ports exposed (5005-5009)
- Hot reload enabled
- Development databases with UI tools
- Relaxed security for debugging

#### Production (`docker-compose.production.yml`)
- Resource limits and reservations
- Security hardening
- Performance optimizations
- Health checks and restart policies

#### Security (`docker-security.yml`)
- Container security policies
- User namespace mappings
- Resource constraints
- Network security policies

### Terraform Environments

#### Development (`terraform/environments/dev/`)
- Single AZ deployment
- FARGATE_SPOT for cost optimization
- Minimal monitoring
- HTTP-only ALB

#### Production (`terraform/environments/prod/`)
- Multi-AZ high availability
- FARGATE with deletion protection
- Comprehensive monitoring
- HTTPS with WAF protection

## Key Features

### 1. Container Security

```yaml
# Security defaults for all services
x-security-defaults: &security-defaults
  user: "1001:1001"          # Non-root user
  read_only: true            # Read-only root filesystem
  cap_drop: [ALL]            # Drop all capabilities
  security_opt:
    - no-new-privileges:true
    - seccomp:./docker/security/seccomp-profile.json
```

### 2. Auto-scaling Configuration

```hcl
# ECS auto-scaling policies
resource "aws_appautoscaling_policy" "cpu" {
  name               = "${var.project_name}-cpu-scaling"
  policy_type        = "TargetTrackingScaling"

  target_tracking_scaling_policy_configuration {
    target_value       = 70.0
    scale_in_cooldown  = 300
    scale_out_cooldown = 60

    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }
  }
}
```

### 3. Blue-Green Deployments

```hcl
# CodeDeploy configuration
resource "aws_codedeploy_deployment_group" "services" {
  deployment_config_name = "CodeDeployDefault.ECSCanary10Percent15Minutes"

  blue_green_deployment_config {
    deployment_ready_option {
      action_on_timeout = "CONTINUE_DEPLOYMENT"
    }

    terminate_blue_instances_on_deployment_success {
      action                         = "TERMINATE"
      termination_wait_time_in_minutes = 10
    }
  }
}
```

### 4. Monitoring and Alerting

```hcl
# CloudWatch alarms for automatic rollback
resource "aws_cloudwatch_metric_alarm" "response_time" {
  alarm_name          = "high-response-time"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = "2"
  metric_name         = "TargetResponseTime"
  threshold           = "2000"
  alarm_actions       = [aws_sns_topic.alerts.arn]
}
```

## Service Endpoints

### Development Environment
| Service | Port | Endpoint | Purpose |
|---------|------|----------|---------|
| API Gateway | 8080 | http://localhost:8080 | Main API |
| Company Service | 8081 | http://localhost:8081 | Company management |
| Site Service | 8082 | http://localhost:8082 | Site management |
| Calculation Service | 8083 | http://localhost:8083 | Barycenter calculations |
| Dashboard Service | 8084 | http://localhost:8084 | Analytics dashboard |
| Grafana | 3000 | http://localhost:3000 | Metrics visualization |
| Prometheus | 9090 | http://localhost:9090 | Metrics collection |
| Zipkin | 9411 | http://localhost:9411 | Distributed tracing |
| Kafka UI | 8080 | http://localhost:8080 | Kafka management |
| pgAdmin | 8888 | http://localhost:8888 | Database administration |

### Production Environment
| Service | URL | Purpose |
|---------|-----|---------|
| API Gateway | https://api.logistics.example.com | Production API |
| Grafana | https://grafana.logistics.internal | Internal monitoring |
| Prometheus | https://prometheus.logistics.internal | Internal metrics |

## CI/CD Pipeline

### Build Process

1. **Pre-build**
   - Dockerfile linting with Hadolint
   - Maven dependency caching
   - ECR authentication

2. **Build**
   - Unit test execution
   - Spring Boot application build
   - Docker image creation with security scanning

3. **Test**
   - Integration tests
   - Security scanning with Trivy
   - Health check validation

4. **Deploy**
   - Image push to ECR
   - ECS task definition update
   - Blue-green deployment via CodeDeploy

### BuildSpec Configuration

Each microservice has its own `buildspec.yml` with:
- Multi-stage Docker builds
- Security scanning
- Test execution and reporting
- Artifact generation for deployment

## Security Best Practices

### 1. Container Security
- Non-root user execution
- Read-only root filesystems
- Capability dropping
- Seccomp profiles
- AppArmor/SELinux policies

### 2. Network Security
- Private subnets for applications
- NAT gateways for outbound traffic
- Security groups with least privilege
- WAF protection for ALB

### 3. Data Protection
- Encryption at rest and in transit
- Secrets management with AWS Parameter Store
- Database connection encryption
- Regular automated backups

### 4. Access Control
- IAM roles with minimal permissions
- Service-to-service authentication
- API rate limiting
- Audit logging

## Monitoring and Observability

### Metrics Collection
- **Application metrics** via Micrometer/Prometheus
- **Infrastructure metrics** via CloudWatch
- **Custom business metrics** via dashboard service

### Distributed Tracing
- **Zipkin** for local development
- **Jaeger** for production (alternative)
- **AWS X-Ray** integration available

### Log Aggregation
- **CloudWatch Logs** for centralized logging
- **Fluentd** for log processing
- **Structured logging** with JSON format

### Alerting
- **CloudWatch Alarms** for infrastructure
- **Grafana Alerts** for application metrics
- **SNS notifications** for critical issues

## Scaling and Performance

### Horizontal Scaling
- **Auto-scaling groups** based on CPU/memory
- **Application Load Balancer** for traffic distribution
- **Database read replicas** for read scaling

### Vertical Scaling
- **Configurable resource limits** per service
- **JVM tuning** for container environments
- **Connection pooling** optimization

### Performance Optimization
- **Docker layer caching** for faster builds
- **Maven dependency caching** in CI/CD
- **CDN integration** for static assets
- **Database query optimization**

## Backup and Disaster Recovery

### Automated Backups
- **RDS automated backups** with point-in-time recovery
- **EFS backups** for application data
- **Cross-region backup** replication

### Disaster Recovery
- **Multi-AZ deployment** for high availability
- **Infrastructure as Code** for rapid recovery
- **Blue-green deployments** for zero-downtime updates

## Cost Optimization

### Development
- **FARGATE_SPOT** for non-production workloads
- **Single NAT gateway** for development
- **Shorter log retention** periods

### Production
- **Reserved Instances** for predictable workloads
- **Auto-scaling** to match demand
- **Cost monitoring** and alerts

## Troubleshooting

### Common Issues

1. **Container startup failures**
```bash
# Check container logs
docker logs logistics-api-gateway

# Check health status
docker exec container_name curl http://localhost:8080/actuator/health
```

2. **Database connection issues**
```bash
# Test database connectivity
docker exec logistics-postgres pg_isready -U logistics
```

3. **Memory issues**
```bash
# Monitor container memory usage
docker stats

# Check JVM memory settings
docker exec container_name jcmd 1 VM.flags
```

### Debugging

#### Local Debugging
- Debug ports exposed (5005-5009)
- IDE remote debugging configuration
- Hot reload with Spring DevTools

#### Production Debugging
- ECS Exec for container access
- CloudWatch Logs for application logs
- X-Ray for distributed tracing

## Migration Guide

### From Existing Infrastructure

1. **Assessment**
   - Audit current services and dependencies
   - Identify data migration requirements
   - Plan rollback procedures

2. **Preparation**
   - Set up new infrastructure in parallel
   - Configure monitoring and alerting
   - Test deployment procedures

3. **Migration**
   - Blue-green deployment for zero downtime
   - Data synchronization during transition
   - Traffic routing updates

4. **Validation**
   - End-to-end testing
   - Performance validation
   - Monitoring verification

## Maintenance

### Regular Tasks
- **Security updates** for base images
- **Dependency updates** for Maven libraries
- **Log rotation** and cleanup
- **Backup verification**

### Monitoring
- **Resource utilization** tracking
- **Error rate** monitoring
- **Performance metrics** analysis
- **Cost optimization** reviews

---

## Support and Contributing

For issues, questions, or contributions:
1. Check existing documentation
2. Review CloudWatch logs and metrics
3. Create detailed issue reports
4. Submit pull requests with tests

**Infrastructure maintained by Platform Team**