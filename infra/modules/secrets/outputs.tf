output "claude_api_key_id" {
  value = google_secret_manager_secret.claude_api_key.id
}

output "openai_api_key_id" {
  value = google_secret_manager_secret.openai_api_key.id
}

output "firebase_service_account_id" {
  value = google_secret_manager_secret.firebase_service_account.id
}

output "ticktick_api_key_id" {
  value = google_secret_manager_secret.ticktick_api_key.id
}
