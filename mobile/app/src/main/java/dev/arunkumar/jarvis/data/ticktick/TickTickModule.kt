package dev.arunkumar.jarvis.data.ticktick

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.arunkumar.jarvis.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TickTickModule {

  // Database
  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): TickTickDatabase =
    Room.databaseBuilder(context, TickTickDatabase::class.java, "ticktick.db")
      .fallbackToDestructiveMigration(dropAllTables = true)
      .build()

  @Provides
  fun provideTaskDao(db: TickTickDatabase): TaskDao = db.taskDao()

  @Provides
  fun provideProjectDao(db: TickTickDatabase): ProjectDao = db.projectDao()

  // Network
  @Provides
  @Singleton
  @TickTickClient
  fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
      chain.proceed(
        chain.request().newBuilder()
          .addHeader("Authorization", "Bearer ${BuildConfig.TICKTICK_API_KEY}")
          .build()
      )
    }
    .addInterceptor(
      HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
          HttpLoggingInterceptor.Level.BODY
        } else {
          HttpLoggingInterceptor.Level.NONE
        }
      }
    )
    .build()

  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
  }

  @Provides
  @Singleton
  fun provideTickTickApi(@TickTickClient client: OkHttpClient, json: Json): TickTickApi = Retrofit.Builder()
    .baseUrl(BuildConfig.TICKTICK_API_BASE_URL)
    .client(client)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()
    .create(TickTickApi::class.java)

  // WorkManager
  @Provides
  @Singleton
  fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
    WorkManager.getInstance(context)

  // NotificationManager (moved from di/TickTickModule.kt)
  @Provides
  @Singleton
  fun provideNotificationManager(
    @ApplicationContext context: Context,
  ): NotificationManager {
    return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  }
}
