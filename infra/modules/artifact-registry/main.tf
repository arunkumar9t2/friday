resource "google_artifact_registry_repository" "jarvis" {
  location      = var.region
  repository_id = "jarvis"
  description   = "Docker images for Jarvis backend"
  format        = "DOCKER"

  labels = {
    environment = var.environment
  }
}
