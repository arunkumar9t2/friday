resource "google_pubsub_topic" "events" {
  name = "jarvis-events"

  labels = {
    environment = var.environment
  }
}

resource "google_pubsub_subscription" "events_subscription" {
  name  = "jarvis-events-sub"
  topic = google_pubsub_topic.events.name

  ack_deadline_seconds = 20

  labels = {
    environment = var.environment
  }
}
