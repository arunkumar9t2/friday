# Placeholder for IFTTT webhook functions
# Will be implemented when needed

# Storage bucket for function source
resource "google_storage_bucket" "function_source" {
  name     = "${var.project_id}-function-source"
  location = var.region

  labels = {
    environment = var.environment
  }
}
