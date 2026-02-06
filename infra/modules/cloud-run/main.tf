resource "google_cloud_run_v2_service" "jarvis_server" {
  name     = "jarvis-server"
  location = var.region

  template {
    scaling {
      min_instance_count = 0
      max_instance_count = 3
    }

    containers {
      image = "${var.repository_url}/server:latest"

      resources {
        limits = {
          memory = "512Mi"
          cpu    = "1"
        }
      }

      ports {
        container_port = 8080
      }

      env {
        name  = "ENVIRONMENT"
        value = var.environment
      }

      env {
        name  = "GCP_PROJECT_ID"
        value = var.project_id
      }
    }
  }

  labels = {
    environment = var.environment
  }
}

# Allow public access
resource "google_cloud_run_v2_service_iam_member" "public_access" {
  name     = google_cloud_run_v2_service.jarvis_server.name
  location = google_cloud_run_v2_service.jarvis_server.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}
