# GitHub Actions Workflows

## Overview

This directory contains CI/CD workflows for the Jarvis monorepo.

## Workflows

### android.yml
- **Triggers**: Push/PR on mobile, shared, build-logic, gradle files
- **Jobs**: Build debug APK, run tests, run lint, test shared module
- **Artifacts**: Debug APK, test results

### backend.yml
- **Triggers**: Push/PR on backend, shared files
- **Jobs**:
  - `test`: Run backend tests
  - `deploy`: Build Docker image, push to Artifact Registry, deploy to Cloud Run (main only)
- **Artifacts**: Test results

### terraform.yml
- **Triggers**: Push/PR on infra files
- **Jobs**:
  - `plan`: Validate and plan infrastructure changes
  - `apply`: Apply changes to production (main only, requires approval)
- **Environment**: Uses `production` environment for apply job

## Required Secrets

Configure these in repository settings:

### Secrets
- `WIF_PROVIDER`: Workload Identity Federation provider (e.g., `projects/123/locations/global/workloadIdentityPools/github/providers/github-provider`)
- `WIF_SERVICE_ACCOUNT`: GCP service account email (e.g., `github-actions@PROJECT_ID.iam.gserviceaccount.com`)

### Variables
- `GCP_PROJECT_ID`: Your GCP project ID
- `GCP_REGION`: GCP region (default: `us-central1`)

## Workload Identity Federation Setup

To set up WIF for GitHub Actions:

1. Create workload identity pool and provider
2. Create service account with necessary permissions
3. Bind the pool to the service account
4. Configure attribute mappings

See: https://cloud.google.com/iam/docs/workload-identity-federation-with-deployment-pipelines
