#!/usr/bin/env python3
"""
Cross-platform ECS image deployment script
Supports both Windows PowerShell and Unix environments
"""

import json
import subprocess
import sys
import argparse
import os
from typing import Dict, Any


class ECSDeployer:
    def __init__(self, cluster_name: str, service_name: str, task_family: str, region: str):
        self.cluster_name = cluster_name
        self.service_name = service_name
        self.task_family = task_family
        self.region = region
        
    def run_command(self, command: list) -> Dict[str, Any]:
        """Execute AWS CLI command and return JSON response"""
        try:
            result = subprocess.run(
                command,
                capture_output=True,
                text=True,
                check=True
            )
            return json.loads(result.stdout) if result.stdout.strip() else {}
        except subprocess.CalledProcessError as e:
            print(f"Error executing command: {' '.join(command)}")
            print(f"Error output: {e.stderr}")
            sys.exit(1)
        except json.JSONDecodeError as e:
            print(f"Error parsing JSON response: {e}")
            sys.exit(1)

    def get_current_task_definition(self) -> Dict[str, Any]:
        """Retrieve current task definition"""
        print("Retrieving current task definition...")
        
        command = [
            "aws", "ecs", "describe-task-definition",
            "--task-definition", self.task_family,
            "--region", self.region,
            "--query", "taskDefinition.{family:family,networkMode:networkMode,requiresCompatibilities:requiresCompatibilities,cpu:cpu,memory:memory,executionRoleArn:executionRoleArn,taskRoleArn:taskRoleArn,containerDefinitions:containerDefinitions}"
        ]
        
        return self.run_command(command)

    def update_task_definition_image(self, task_def: Dict[str, Any], new_image: str) -> Dict[str, Any]:
        """Update the container image in task definition"""
        print(f"Updating task definition with new image: {new_image}")
        
        # Update the first container's image (assuming single container)
        if task_def.get("containerDefinitions") and len(task_def["containerDefinitions"]) > 0:
            task_def["containerDefinitions"][0]["image"] = new_image
        else:
            print("Error: No container definitions found in task definition")
            sys.exit(1)
            
        return task_def

    def register_task_definition(self, task_def: Dict[str, Any]) -> str:
        """Register new task definition and return ARN"""
        print("Registering new task definition...")
        
        # Save updated task definition to temporary file
        temp_file = "updated-task-def.json"
        try:
            with open(temp_file, 'w') as f:
                json.dump(task_def, f, indent=2)
            
            command = [
                "aws", "ecs", "register-task-definition",
                "--cli-input-json", f"file://{temp_file}",
                "--region", self.region,
                "--query", "taskDefinition.taskDefinitionArn",
                "--output", "text"
            ]
            
            result = subprocess.run(
                command,
                capture_output=True,
                text=True,
                check=True
            )
            
            task_def_arn = result.stdout.strip()
            print(f"New task definition registered: {task_def_arn}")
            return task_def_arn
            
        except subprocess.CalledProcessError as e:
            print(f"Error registering task definition: {e.stderr}")
            sys.exit(1)
        finally:
            # Cleanup temporary file
            if os.path.exists(temp_file):
                os.remove(temp_file)

    def update_service(self):
        """Update ECS service with new task definition"""
        print("Updating ECS service...")
        
        command = [
            "aws", "ecs", "update-service",
            "--cluster", self.cluster_name,
            "--service", self.service_name,
            "--task-definition", self.task_family,
            "--region", self.region,
            "--force-new-deployment"
        ]
        
        self.run_command(command)
        print("Service update initiated")

    def wait_for_deployment(self):
        """Wait for deployment to stabilize"""
        print("Waiting for deployment to stabilize...")
        
        command = [
            "aws", "ecs", "wait", "services-stable",
            "--cluster", self.cluster_name,
            "--services", self.service_name,
            "--region", self.region
        ]
        
        try:
            subprocess.run(command, check=True)
            print("✅ Deployment completed successfully!")
        except subprocess.CalledProcessError as e:
            print(f"❌ Deployment failed or timed out: {e}")
            sys.exit(1)

    def deploy(self, new_image: str):
        """Complete deployment workflow"""
        print(f"🚀 Starting deployment of image: {new_image}")
        print(f"📊 Target: {self.cluster_name}/{self.service_name}")
        
        # Get current task definition
        current_task_def = self.get_current_task_definition()
        
        # Update image
        updated_task_def = self.update_task_definition_image(current_task_def, new_image)
        
        # Register new task definition
        new_task_def_arn = self.register_task_definition(updated_task_def)
        
        # Update service
        self.update_service()
        
        # Wait for deployment
        self.wait_for_deployment()


def main():
    parser = argparse.ArgumentParser(
        description="Deploy new container image to ECS service",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Deploy specific image
  python deploy-ecs-image.py 602259772901.dkr.ecr.eu-north-1.amazonaws.com/useraccess-api:31e3524
  
  # Deploy with environment variables (from CI/CD)
  python deploy-ecs-image.py $REPOSITORY_URI:$IMAGE_TAG
        """
    )
    
    parser.add_argument(
        "image_uri",
        help="New container image URI to deploy"
    )
    
    parser.add_argument(
        "--cluster",
        default="useraccess-api-test-k-cluster",
        help="ECS cluster name (default: useraccess-api-test-k-cluster)"
    )
    
    parser.add_argument(
        "--service",
        default="useraccess-api-test-k-service",
        help="ECS service name (default: useraccess-api-test-k-service)"
    )
    
    parser.add_argument(
        "--task-family",
        default="useraccess-api-test-k-family",
        help="Task definition family (default: useraccess-api-test-k-family)"
    )
    
    parser.add_argument(
        "--region",
        default="eu-north-1",
        help="AWS region (default: eu-north-1)"
    )
    
    args = parser.parse_args()
    
    # Validate image URI
    if not args.image_uri or args.image_uri.isspace():
        print("❌ Error: Image URI cannot be empty")
        sys.exit(1)
    
    # Create deployer and run deployment
    deployer = ECSDeployer(
        cluster_name=args.cluster,
        service_name=args.service,
        task_family=args.task_family,
        region=args.region
    )
    
    try:
        deployer.deploy(args.image_uri)
    except KeyboardInterrupt:
        print("\n❌ Deployment cancelled by user")
        sys.exit(1)
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()