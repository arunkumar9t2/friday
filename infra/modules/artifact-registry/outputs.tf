output "repository_name" {
  value = google_artifact_registry_repository.jarvis.name
}

output "repository_url" {
  value = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.jarvis.repository_id}"
}
