provider "google" {
  project = var.project_id
  region  = var.region
}

# Artifact Registry for Docker images
module "artifact_registry" {
  source      = "./modules/artifact-registry"
  project_id  = var.project_id
  region      = var.region
  environment = var.environment
}

# Cloud Run service
module "cloud_run" {
  source         = "./modules/cloud-run"
  project_id     = var.project_id
  region         = var.region
  environment    = var.environment
  repository_url = module.artifact_registry.repository_url

  depends_on = [module.artifact_registry]
}

# Firestore database
module "firestore" {
  source      = "./modules/firestore"
  project_id  = var.project_id
  region      = var.region
  environment = var.environment
}

# Cloud Functions for IFTTT webhooks
module "cloud_functions" {
  source      = "./modules/cloud-functions"
  project_id  = var.project_id
  region      = var.region
  environment = var.environment
}

# Pub/Sub for async events
module "pubsub" {
  source      = "./modules/pubsub"
  project_id  = var.project_id
  region      = var.region
  environment = var.environment
}

# Secret Manager for API keys
module "secrets" {
  source      = "./modules/secrets"
  project_id  = var.project_id
  environment = var.environment
}
