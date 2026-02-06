output "service_url" {
  value = google_cloud_run_v2_service.jarvis_server.uri
}

output "service_name" {
  value = google_cloud_run_v2_service.jarvis_server.name
}
