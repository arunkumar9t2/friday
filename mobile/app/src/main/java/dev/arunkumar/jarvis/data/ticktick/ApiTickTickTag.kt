package dev.arunkumar.jarvis.data.ticktick

import kotlinx.serialization.Serializable

@Serializable
data class ApiTickTickTag(
  val name: String,
  val label: String? = null,
  val sortOrder: Long? = null,
  val sortType: String? = null,
  val color: String? = null,
  val parent: String? = null,
  val rawName: String? = null,
  val etag: String? = null,
  val type: Int? = null,
)
