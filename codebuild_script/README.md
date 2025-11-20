# CodeBuild Scripts

This directory contains deployment and build automation scripts for AWS CodeBuild CI/CD pipeline operations.

## Contents

### [deploy_on_fargate.py](./deploy_on_fargate.py)
Cross-platform Python script for deploying container images to Amazon ECS Fargate services.

- **Purpose**: Automates ECS deployment workflow with task definition updates
- **Usage**: `python deploy_on_fargate.py <image_uri> [options]`
- **Documentation**: See [deploy_on_fargate.md](./deploy_on_fargate.md) for detailed usage

## Directory Structure

```
codebuild_script/
├── README.md                 # This file
├── deploy_on_fargate.py     # ECS deployment script
└── deploy_on_fargate.md     # Detailed script documentation
```