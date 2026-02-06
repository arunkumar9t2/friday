package dev.arunkumar.jarvis.shared.models

import dev.arunkumar.jarvis.shared.models.user.UserProfile
import dev.arunkumar.jarvis.shared.models.task.TaskItem
import dev.arunkumar.jarvis.shared.models.task.TaskPriority
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {
    @Test
    fun testUserProfileSerialization() {
        val profile = UserProfile(id = "1", name = "Test", email = "test@example.com")
        val json = Json.encodeToString(UserProfile.serializer(), profile)
        val decoded = Json.decodeFromString(UserProfile.serializer(), json)
        assertEquals(profile, decoded)
    }

    @Test
    fun testTaskItemSerialization() {
        val task = TaskItem(
            id = "t1",
            projectId = "p1",
            title = "Test Task",
            priority = TaskPriority.HIGH,
            status = "open"
        )
        val json = Json.encodeToString(TaskItem.serializer(), task)
        val decoded = Json.decodeFromString(TaskItem.serializer(), json)
        assertEquals(task, decoded)
    }
}
