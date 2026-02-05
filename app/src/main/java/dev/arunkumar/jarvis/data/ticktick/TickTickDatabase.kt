package dev.arunkumar.jarvis.data.ticktick

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Transaction

@Database(entities = [TaskEntity::class, ProjectEntity::class], version = 1, exportSchema = false)
abstract class TickTickDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao
  abstract fun projectDao(): ProjectDao

  @Transaction
  open suspend fun replaceAllData(tasks: List<TaskEntity>, projects: List<ProjectEntity>) {
    taskDao().deleteAll()
    projectDao().deleteAll()
    taskDao().insertAll(tasks)
    projectDao().insertAll(projects)
  }
}
