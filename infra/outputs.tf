# Disabled until Cloud Run module is deployed
# output "cloud_run_url" {
#   description = "Cloud Run service URL"
#   value       = module.cloud_run.service_url
# }

output "artifact_registry_repository" {
  description = "Artifact Registry repository name"
  value       = module.artifact_registry.repository_name
}

output "firestore_database" {
  description = "Firestore database name"
  value       = module.firestore.database_name
}
