# =============================================================================
# Logistics Platform Docker & Terraform Makefile
# =============================================================================

# Version and build information
VERSION ?= $(shell git describe --tags --always --dirty 2>/dev/null || echo "dev")
GIT_COMMIT ?= $(shell git rev-parse HEAD 2>/dev/null || echo "unknown")
GIT_BRANCH ?= $(shell git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "main")
BUILD_DATE ?= $(shell date -u +'%Y-%m-%dT%H:%M:%SZ')
AWS_REGION ?= us-east-1
AWS_ACCOUNT_ID ?= $(shell aws sts get-caller-identity --query Account --output text 2>/dev/null)

# Docker configuration
DOCKER_REGISTRY ?= $(AWS_ACCOUNT_ID).dkr.ecr.$(AWS_REGION).amazonaws.com
DOCKER_REPO_PREFIX ?= logistics
IMAGE_TAG ?= $(VERSION)

# Terraform configuration
TF_ENV ?= dev
TF_VAR_FILE ?= ./terraform/environments/$(TF_ENV)/terraform.tfvars

# Export build args for docker-compose
export BUILD_DATE
export GIT_COMMIT
export GIT_BRANCH
export VERSION

.PHONY: help build test push deploy clean

# =============================================================================
# Help
# =============================================================================
help: ## Show this help message
	@echo "Logistics Platform Build & Deployment"
	@echo "======================================"
	@echo ""
	@awk 'BEGIN {FS = ":.*##"} /^[a-zA-Z_-]+:.*?##/ { printf "  %-20s %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

# =============================================================================
# Development
# =============================================================================
dev-up: ## Start all services for development
	docker-compose -f docker/docker-compose.microservices.yml up --build -d

dev-down: ## Stop all development services
	docker-compose -f docker/docker-compose.microservices.yml down

dev-logs: ## Follow logs of all services
	docker-compose -f docker/docker-compose.microservices.yml logs -f

dev-logs-service: ## Follow logs of specific service (make dev-logs-service SERVICE=api-gateway)
	docker-compose -f docker/docker-compose.microservices.yml logs -f $(SERVICE)

dev-shell: ## Open shell in running service container
	docker-compose -f docker/docker-compose.microservices.yml exec $(SERVICE) /bin/sh

# =============================================================================
# Building Images
# =============================================================================
build: ## Build all Docker images
	@echo "Building logistics platform images..."
	@echo "Version: $(VERSION)"
	@echo "Commit: $(GIT_COMMIT)"
	@echo "Branch: $(GIT_BRANCH)"
	@echo "Build Date: $(BUILD_DATE)"
	docker-compose -f docker/docker-compose.microservices.yml build

build-desktop: ## Build JavaFX desktop application
	docker build \
		--file Dockerfile.desktop \
		--target gui-runtime \
		--build-arg BUILD_DATE="$(BUILD_DATE)" \
		--build-arg GIT_COMMIT="$(GIT_COMMIT)" \
		--build-arg GIT_BRANCH="$(GIT_BRANCH)" \
		--build-arg VERSION="$(VERSION)" \
		--tag $(DOCKER_REPO_PREFIX)/desktop-app:$(IMAGE_TAG) \
		.

build-microservices: ## Build all microservice images
	cd microservices && \
	docker build -f api-gateway/Dockerfile -t $(DOCKER_REPO_PREFIX)/api-gateway:$(IMAGE_TAG) api-gateway && \
	docker build -f company-service/Dockerfile -t $(DOCKER_REPO_PREFIX)/company-service:$(IMAGE_TAG) company-service && \
	docker build -f site-service/Dockerfile -t $(DOCKER_REPO_PREFIX)/site-service:$(IMAGE_TAG) site-service && \
	docker build -f calculation-service/Dockerfile -t $(DOCKER_REPO_PREFIX)/calculation-service:$(IMAGE_TAG) calculation-service && \
	docker build -f dashboard-service/Dockerfile -t $(DOCKER_REPO_PREFIX)/dashboard-service:$(IMAGE_TAG) dashboard-service

# =============================================================================
# Testing
# =============================================================================
test: ## Run all tests
	@echo "Running Maven tests..."
	cd microservices && mvn clean test

test-integration: ## Run integration tests
	@echo "Starting test environment..."
	docker-compose -f docker/docker-compose.microservices.yml -f docker/docker-compose.test.yml up -d --wait
	@echo "Running integration tests..."
	cd microservices && mvn verify -Dspring.profiles.active=integration
	@echo "Stopping test environment..."
	docker-compose -f docker/docker-compose.microservices.yml -f docker/docker-compose.test.yml down

test-security: ## Run security scans on Docker images
	@echo "Scanning images for vulnerabilities..."
	@for service in api-gateway company-service site-service calculation-service dashboard-service desktop-app; do \
		echo "Scanning $(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG)..."; \
		docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
			aquasec/trivy image $(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG); \
	done

# =============================================================================
# Production Deployment
# =============================================================================
prod-up: ## Start production environment
	docker-compose \
		-f docker/docker-compose.microservices.yml \
		-f docker/docker-compose.override.yml \
		up -d

prod-down: ## Stop production environment
	docker-compose \
		-f docker/docker-compose.microservices.yml \
		-f docker/docker-compose.override.yml \
		down

# =============================================================================
# AWS ECR Operations
# =============================================================================
ecr-login: ## Login to AWS ECR
	aws ecr get-login-password --region $(AWS_REGION) | \
		docker login --username AWS --password-stdin $(DOCKER_REGISTRY)

ecr-create-repos: ## Create ECR repositories for all services
	@echo "Creating ECR repositories..."
	@for service in api-gateway company-service site-service calculation-service dashboard-service desktop-app; do \
		echo "Creating repository: $(DOCKER_REPO_PREFIX)/$$service"; \
		aws ecr create-repository --repository-name $(DOCKER_REPO_PREFIX)/$$service --region $(AWS_REGION) || true; \
	done

# =============================================================================
# Image Publishing
# =============================================================================
tag: ## Tag images for ECR
	@for service in api-gateway company-service site-service calculation-service dashboard-service desktop-app; do \
		docker tag $(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG) $(DOCKER_REGISTRY)/$(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG); \
		docker tag $(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG) $(DOCKER_REGISTRY)/$(DOCKER_REPO_PREFIX)/$$service:latest; \
	done

push: ecr-login tag ## Push images to ECR
	@echo "Pushing images to ECR..."
	@for service in api-gateway company-service site-service calculation-service dashboard-service desktop-app; do \
		echo "Pushing $(DOCKER_REGISTRY)/$(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG)..."; \
		docker push $(DOCKER_REGISTRY)/$(DOCKER_REPO_PREFIX)/$$service:$(IMAGE_TAG); \
		docker push $(DOCKER_REGISTRY)/$(DOCKER_REPO_PREFIX)/$$service:latest; \
	done

# =============================================================================
# Terraform Operations
# =============================================================================
tf-init: ## Initialize Terraform
	cd terraform/environments/$(TF_ENV) && terraform init

tf-plan: ## Plan Terraform changes
	cd terraform/environments/$(TF_ENV) && terraform plan -var-file=$(TF_VAR_FILE)

tf-apply: ## Apply Terraform changes
	cd terraform/environments/$(TF_ENV) && terraform apply -var-file=$(TF_VAR_FILE)

tf-destroy: ## Destroy Terraform infrastructure
	cd terraform/environments/$(TF_ENV) && terraform destroy -var-file=$(TF_VAR_FILE)

tf-output: ## Show Terraform outputs
	cd terraform/environments/$(TF_ENV) && terraform output

# =============================================================================
# Full Deployment Pipeline
# =============================================================================
deploy-dev: build test push ## Deploy to development environment
	$(MAKE) tf-apply TF_ENV=dev

deploy-staging: build test test-security push ## Deploy to staging environment
	$(MAKE) tf-apply TF_ENV=staging

deploy-prod: build test test-security push ## Deploy to production environment
	@echo "Deploying to production..."
	@read -p "Are you sure you want to deploy to production? [y/N] " -n 1 -r; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		echo ""; \
		$(MAKE) tf-apply TF_ENV=prod; \
	else \
		echo ""; \
		echo "Deployment cancelled."; \
	fi

# =============================================================================
# Utility
# =============================================================================
clean: ## Clean up Docker resources
	docker system prune -f
	docker volume prune -f

clean-all: ## Clean up all Docker resources including images
	docker system prune -af
	docker volume prune -f

health-check: ## Check health of all running services
	@echo "Checking service health..."
	@docker-compose -f docker/docker-compose.microservices.yml ps

status: ## Show status of all services
	@echo "Service Status:"
	@echo "==============="
	@docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep logistics || echo "No logistics services running"

# =============================================================================
# Development Tools
# =============================================================================
run-desktop: build-desktop ## Run JavaFX desktop application locally
	docker run --rm -it \
		-p 5900:5900 \
		-e DISPLAY=$(DISPLAY) \
		-v /tmp/.X11-unix:/tmp/.X11-unix:rw \
		$(DOCKER_REPO_PREFIX)/desktop-app:$(IMAGE_TAG)

setup-dev: ## Setup development environment
	@echo "Setting up development environment..."
	@if [ ! -f .env ]; then cp .env.example .env; fi
	$(MAKE) ecr-create-repos
	$(MAKE) tf-init TF_ENV=dev
	@echo "Development environment ready!"

validate: ## Validate configuration files
	@echo "Validating Docker Compose files..."
	docker-compose -f docker/docker-compose.microservices.yml config > /dev/null
	docker-compose -f docker/docker-compose.microservices.yml -f docker/docker-compose.override.yml config > /dev/null
	@echo "Validating Terraform configuration..."
	cd terraform/environments/dev && terraform validate
	@echo "All configurations valid!"

# =============================================================================
# Monitoring and Debugging
# =============================================================================
open-grafana: ## Open Grafana dashboard
	@echo "Opening Grafana at http://localhost:3000"
	@echo "Default credentials: admin / admin"

open-prometheus: ## Open Prometheus UI
	@echo "Opening Prometheus at http://localhost:9090"

open-zipkin: ## Open Zipkin tracing UI
	@echo "Opening Zipkin at http://localhost:9411"

vnc-info: ## Show VNC connection information for desktop app
	@echo "VNC Connection Information:"
	@echo "=========================="
	@echo "Port: 5900"
	@echo "Connect using any VNC viewer to: localhost:5900"
	@echo "For macOS: open vnc://localhost:5900"