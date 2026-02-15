package dev.arunkumar.jarvis.data.ticktick

import retrofit2.http.GET

interface TickTickApi {
  @GET("tasks")
  suspend fun getTasks(): TasksResponse
}
