package dev.arunkumar.jarvis.data.ticktick

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
  @Query("SELECT * FROM ticktick_projects")
  suspend fun getAllProjects(): List<ProjectEntity>

  @Query("SELECT * FROM ticktick_projects")
  fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(projects: List<ProjectEntity>)

  @Query("DELETE FROM ticktick_projects")
  suspend fun deleteAll()

  @Transaction
  suspend fun replaceAll(projects: List<ProjectEntity>) {
    deleteAll()
    insertAll(projects)
  }
}
