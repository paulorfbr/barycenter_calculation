# =============================================================================
# Terraform Module — CI/CD Pipeline for Microservices
#
# Creates:
#   - CodeBuild projects for each microservice
#   - CodePipeline for automated deployments
#   - S3 bucket for artifacts
#   - IAM roles and policies
#   - CloudWatch monitoring for pipeline
#   - CodeCommit/GitHub integration
# =============================================================================

terraform {
  required_version = ">= 1.6.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0"
    }
  }
}

locals {
  name_prefix = "${var.project_name}-${var.environment}"

  # Microservices that need CI/CD pipelines
  services = [
    "api-gateway",
    "company-service",
    "site-service",
    "calculation-service",
    "dashboard-service"
  ]
}

# ---------------------------------------------------------------------------
# S3 Bucket for CodePipeline Artifacts
# ---------------------------------------------------------------------------
resource "aws_s3_bucket" "codepipeline_artifacts" {
  bucket = "${local.name_prefix}-codepipeline-artifacts-${random_string.bucket_suffix.result}"

  tags = var.tags
}

resource "aws_s3_bucket_versioning" "codepipeline_artifacts" {
  bucket = aws_s3_bucket.codepipeline_artifacts.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "codepipeline_artifacts" {
  bucket = aws_s3_bucket.codepipeline_artifacts.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_public_access_block" "codepipeline_artifacts" {
  bucket = aws_s3_bucket.codepipeline_artifacts.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "random_string" "bucket_suffix" {
  length  = 8
  special = false
  upper   = false
}

# ---------------------------------------------------------------------------
# IAM Role for CodeBuild
# ---------------------------------------------------------------------------
resource "aws_iam_role" "codebuild" {
  name = "${local.name_prefix}-codebuild-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "codebuild.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy" "codebuild" {
  role = aws_iam_role.codebuild.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "ecr:BatchCheckLayerAvailability",
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:GetAuthorizationToken",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "s3:GetObject",
          "s3:GetObjectVersion",
          "s3:PutObject",
          "ssm:GetParameters",
          "secretsmanager:GetSecretValue"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:GetObjectVersion",
          "s3:PutObject"
        ]
        Resource = "${aws_s3_bucket.codepipeline_artifacts.arn}/*"
      }
    ]
  })
}

# ---------------------------------------------------------------------------
# IAM Role for CodePipeline
# ---------------------------------------------------------------------------
resource "aws_iam_role" "codepipeline" {
  name = "${local.name_prefix}-codepipeline-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "codepipeline.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy" "codepipeline" {
  role = aws_iam_role.codepipeline.name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetBucketVersioning",
          "s3:GetObject",
          "s3:GetObjectVersion",
          "s3:PutObject"
        ]
        Resource = [
          aws_s3_bucket.codepipeline_artifacts.arn,
          "${aws_s3_bucket.codepipeline_artifacts.arn}/*"
        ]
      },
      {
        Effect = "Allow"
        Action = [
          "codebuild:BatchGetBuilds",
          "codebuild:StartBuild"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "codedeploy:CreateDeployment",
          "codedeploy:GetApplication",
          "codedeploy:GetApplicationRevision",
          "codedeploy:GetDeployment",
          "codedeploy:GetDeploymentConfig",
          "codedeploy:RegisterApplicationRevision"
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "ecs:DescribeServices",
          "ecs:DescribeTaskDefinition",
          "ecs:DescribeTasks",
          "ecs:ListTasks",
          "ecs:RegisterTaskDefinition",
          "ecs:UpdateService"
        ]
        Resource = "*"
      }
    ]
  })
}

# ---------------------------------------------------------------------------
# CodeBuild Projects for Each Service
# ---------------------------------------------------------------------------
resource "aws_codebuild_project" "services" {
  for_each = toset(local.services)

  name          = "${local.name_prefix}-${each.key}-build"
  description   = "Build project for ${each.key} microservice"
  service_role  = aws_iam_role.codebuild.arn

  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type                = var.codebuild_compute_type
    image                      = var.codebuild_image
    type                       = "LINUX_CONTAINER"
    image_pull_credentials_type = "CODEBUILD"
    privileged_mode            = true  # Required for Docker builds

    environment_variable {
      name  = "AWS_DEFAULT_REGION"
      value = var.aws_region
    }

    environment_variable {
      name  = "AWS_ACCOUNT_ID"
      value = var.aws_account_id
    }

    environment_variable {
      name  = "IMAGE_REPO_NAME"
      value = "${var.ecr_repository_url}/${each.key}"
    }

    environment_variable {
      name  = "IMAGE_TAG"
      value = "latest"
    }

    environment_variable {
      name  = "SERVICE_NAME"
      value = each.key
    }

    environment_variable {
      name  = "ENVIRONMENT"
      value = var.environment
    }
  }

  source {
    type = "CODEPIPELINE"
    buildspec = "microservices/${each.key}/buildspec.yml"
  }

  cache {
    type = "S3"
    location = "${aws_s3_bucket.codepipeline_artifacts.bucket}/build-cache/${each.key}"
  }

  logs_config {
    cloudwatch_logs {
      group_name  = "/aws/codebuild/${local.name_prefix}-${each.key}"
      stream_name = "build-log"
    }
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

# CloudWatch Log Groups for CodeBuild
resource "aws_cloudwatch_log_group" "codebuild" {
  for_each = toset(local.services)

  name              = "/aws/codebuild/${local.name_prefix}-${each.key}"
  retention_in_days = var.log_retention_days

  tags = var.tags
}

# ---------------------------------------------------------------------------
# CodePipeline for Continuous Deployment
# ---------------------------------------------------------------------------
resource "aws_codepipeline" "main" {
  for_each = toset(local.services)

  name     = "${local.name_prefix}-${each.key}-pipeline"
  role_arn = aws_iam_role.codepipeline.arn

  artifact_store {
    location = aws_s3_bucket.codepipeline_artifacts.bucket
    type     = "S3"
  }

  stage {
    name = "Source"

    action {
      name             = "Source"
      category         = "Source"
      owner            = "AWS"
      provider         = var.source_provider
      version          = "1"
      output_artifacts = ["source_output"]

      configuration = var.source_provider == "GitHub" ? {
        Owner      = var.github_owner
        Repo       = var.github_repo
        Branch     = var.source_branch
        OAuthToken = var.github_token
      } : {
        RepositoryName = var.codecommit_repo_name
        BranchName     = var.source_branch
      }
    }
  }

  stage {
    name = "Build"

    action {
      name             = "Build"
      category         = "Build"
      owner            = "AWS"
      provider         = "CodeBuild"
      version          = "1"
      input_artifacts  = ["source_output"]
      output_artifacts = ["build_output"]

      configuration = {
        ProjectName = aws_codebuild_project.services[each.key].name
      }
    }
  }

  dynamic "stage" {
    for_each = var.enable_manual_approval ? [1] : []
    content {
      name = "Approval"

      action {
        name     = "ManualApproval"
        category = "Approval"
        owner    = "AWS"
        provider = "Manual"
        version  = "1"

        configuration = {
          CustomData = "Please review the build artifacts and approve deployment to ${var.environment}"
        }
      }
    }
  }

  stage {
    name = "Deploy"

    action {
      name            = "Deploy"
      category        = "Deploy"
      owner           = "AWS"
      provider        = var.enable_blue_green ? "CodeDeployToECS" : "ECS"
      version         = "1"
      input_artifacts = var.enable_blue_green ? ["build_output"] : ["build_output"]

      configuration = var.enable_blue_green ? {
        ApplicationName                = var.codedeploy_application_name
        DeploymentGroupName           = "${each.key}-deployment-group"
        TaskDefinitionTemplateArtifact = "build_output"
        AppSpecTemplateArtifact       = "build_output"
      } : {
        ClusterName = var.ecs_cluster_name
        ServiceName = "${local.name_prefix}-${each.key}"
        FileName    = "imagedefinitions.json"
      }
    }
  }

  tags = merge(var.tags, {
    Service = each.key
  })
}

# ---------------------------------------------------------------------------
# CloudWatch Dashboard for Pipeline Monitoring
# ---------------------------------------------------------------------------
resource "aws_cloudwatch_dashboard" "pipeline" {
  dashboard_name = "${local.name_prefix}-pipeline-dashboard"

  dashboard_body = jsonencode({
    widgets = [
      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 12
        height = 6

        properties = {
          metrics = [
            for service in local.services : [
              "AWS/CodePipeline",
              "PipelineExecutionSuccess",
              "PipelineName",
              "${local.name_prefix}-${service}-pipeline"
            ]
          ]
          period = 300
          stat   = "Sum"
          region = var.aws_region
          title  = "Pipeline Success Rate"
        }
      },
      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 12
        height = 6

        properties = {
          metrics = [
            for service in local.services : [
              "AWS/CodeBuild",
              "Duration",
              "ProjectName",
              "${local.name_prefix}-${service}-build"
            ]
          ]
          period = 300
          stat   = "Average"
          region = var.aws_region
          title  = "Build Duration"
        }
      }
    ]
  })
}

# ---------------------------------------------------------------------------
# SNS Topic for Pipeline Notifications
# ---------------------------------------------------------------------------
resource "aws_sns_topic" "pipeline_notifications" {
  name = "${local.name_prefix}-pipeline-notifications"

  tags = var.tags
}

resource "aws_sns_topic_subscription" "email" {
  count = var.notification_email != "" ? 1 : 0

  topic_arn = aws_sns_topic.pipeline_notifications.arn
  protocol  = "email"
  endpoint  = var.notification_email
}

# CloudWatch Event Rules for Pipeline State Changes
resource "aws_cloudwatch_event_rule" "pipeline_state_change" {
  for_each = toset(local.services)

  name        = "${local.name_prefix}-${each.key}-pipeline-state-change"
  description = "Capture pipeline state changes for ${each.key}"

  event_pattern = jsonencode({
    source      = ["aws.codepipeline"]
    detail-type = ["CodePipeline Pipeline Execution State Change"]
    detail = {
      pipeline = [aws_codepipeline.main[each.key].name]
      state    = ["FAILED", "SUCCEEDED"]
    }
  })

  tags = var.tags
}

resource "aws_cloudwatch_event_target" "pipeline_notification" {
  for_each = toset(local.services)

  rule      = aws_cloudwatch_event_rule.pipeline_state_change[each.key].name
  target_id = "SendToSNS"
  arn       = aws_sns_topic.pipeline_notifications.arn
}