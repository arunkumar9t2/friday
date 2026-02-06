resource "google_secret_manager_secret" "claude_api_key" {
  secret_id = "claude-api-key"

  replication {
    auto {}
  }

  labels = {
    environment = var.environment
  }
}

resource "google_secret_manager_secret" "openai_api_key" {
  secret_id = "openai-api-key"

  replication {
    auto {}
  }

  labels = {
    environment = var.environment
  }
}

resource "google_secret_manager_secret" "firebase_service_account" {
  secret_id = "firebase-service-account"

  replication {
    auto {}
  }

  labels = {
    environment = var.environment
  }
}

resource "google_secret_manager_secret" "ticktick_api_key" {
  secret_id = "ticktick-api-key"

  replication {
    auto {}
  }

  labels = {
    environment = var.environment
  }
}
