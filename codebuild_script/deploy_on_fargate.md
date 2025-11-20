# deploy_on_fargate.py

A cross-platform Python script for deploying new container images to Amazon ECS (Elastic Container Service) Fargate services. This script automates the process of updating task definitions and deploying new container images to running ECS services.

## Overview

This script provides a complete deployment workflow for ECS services:
1. Retrieves the current task definition
2. Updates the container image URI in the task definition
3. Registers a new task definition revision
4. Updates the ECS service to use the new task definition
5. Waits for the deployment to stabilize

## Prerequisites

- Python 3.6 or later
- AWS CLI installed and configured with appropriate permissions
- Valid AWS credentials with ECS permissions

### Required AWS Permissions

The script requires the following AWS IAM permissions:
- `ecs:DescribeTaskDefinition`
- `ecs:RegisterTaskDefinition`
- `ecs:UpdateService`
- `ecs:DescribeServices`

## Usage

### Basic Usage

```bash
python deploy_on_fargate.py <image_uri>
```

### Examples

```bash
# Deploy specific image
python deploy_on_fargate.py 602259772901.dkr.ecr.eu-north-1.amazonaws.com/useraccess-api:31e3524

# Deploy with environment variables (from CI/CD)
python deploy_on_fargate.py $REPOSITORY_URI:$IMAGE_TAG

# Deploy to custom cluster/service
python deploy_on_fargate.py \
  --cluster my-cluster \
  --service my-service \
  --task-family my-task-family \
  --region us-west-2 \
  602259772901.dkr.ecr.eu-north-1.amazonaws.com/useraccess-api:latest
```

## Command Line Arguments

### Required Arguments

- `image_uri`: The new container image URI to deploy (e.g., ECR repository URI with tag)

### Optional Arguments

- `--cluster`: ECS cluster name (default: `useraccess-api-test-k-cluster`)
- `--service`: ECS service name (default: `useraccess-api-test-k-service`)
- `--task-family`: Task definition family name (default: `useraccess-api-test-k-family`)
- `--region`: AWS region (default: `eu-north-1`)

## Features

- **Cross-platform compatibility**: Works on both Windows PowerShell and Unix environments
- **Error handling**: Comprehensive error handling with descriptive messages
- **JSON validation**: Validates AWS CLI JSON responses
- **Deployment monitoring**: Waits for deployment to stabilize before completing
- **Temporary file cleanup**: Automatically cleans up temporary files
- **Interactive feedback**: Provides real-time status updates during deployment

## Workflow Details

1. **Task Definition Retrieval**: Fetches the current task definition using `aws ecs describe-task-definition`
2. **Image Update**: Updates the first container's image URI in the task definition
3. **Registration**: Registers a new task definition revision with the updated image
4. **Service Update**: Updates the ECS service to use the new task definition with force new deployment
5. **Stabilization Wait**: Uses `aws ecs wait services-stable` to ensure deployment completes successfully

## Error Handling

The script includes robust error handling for:
- AWS CLI command failures
- JSON parsing errors
- Missing container definitions
- Network timeouts
- User interruption (Ctrl+C)

## Exit Codes

- `0`: Successful deployment
- `1`: Error during execution (AWS CLI errors, validation failures, etc.)

## Integration with CI/CD

This script is designed to be used in CI/CD pipelines, particularly with AWS CodeBuild. It can be called with environment variables for dynamic image URIs:

```bash
python deploy_on_fargate.py ${REPOSITORY_URI}:${IMAGE_TAG}
```

## Troubleshooting

### Common Issues

1. **AWS CLI not found**: Ensure AWS CLI is installed and in PATH
2. **Permission denied**: Verify AWS credentials have required ECS permissions
3. **Service not found**: Check cluster and service names are correct
4. **Task definition not found**: Verify task family name exists
5. **Region mismatch**: Ensure the region parameter matches your ECS resources

### Debug Tips

- Run with verbose AWS CLI output: set `AWS_CLI_COMMAND_TIMEOUT` environment variable
- Check AWS CloudTrail logs for detailed API call information
- Verify ECS service events in the AWS console for deployment status

## Dependencies

- Python standard library modules:
  - `json`
  - `subprocess`
  - `sys`
  - `argparse`
  - `os`
  - `typing`

No external Python packages required.