# Infrastructure & Repository Settings

## Repository Information
- **Name**: useraccess-api
- **Owner**: Kalmar-Platform
- **Branch Strategy**: main, feature/*
- **Jira Integration**: PHXCORE-{number} format required

### CI/CD Pipeline
- **GitHub Actions**: `.github/workflows/ci-validation.yml`
- **Validation**: Jira case validation + Security checks
- **Triggers**: PR to main/develop

## AWS Infrastructure
### Location
TBD
### AWS CodeBuild 
TBD

## Documentation
- [Managing a branch protection rule](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/managing-a-branch-protection-rule)