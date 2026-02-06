package dev.arunkumar.jarvis.data.ticktick

import retrofit2.http.GET

interface TickTickApi {
  @GET("task")
  suspend fun getTasks(): List<ApiTickTickTask>

  @GET("project")
  suspend fun getProjects(): List<ApiTickTickProject>
}
