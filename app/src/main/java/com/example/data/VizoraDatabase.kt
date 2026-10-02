package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VizoraDao {
    @Query("SELECT * FROM video_projects WHERE isTrashed = 0 ORDER BY createdAt DESC")
    fun getActiveProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE isFavorite = 1 AND isTrashed = 0 ORDER BY createdAt DESC")
    fun getFavoriteProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects WHERE isTrashed = 1 ORDER BY createdAt DESC")
    fun getTrashedProjects(): Flow<List<VideoProjectEntity>>

    @Query("SELECT * FROM video_projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity): Long

    @Update
    suspend fun updateProject(project: VideoProjectEntity)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProjectPermanently(id: Int)

    @Query("SELECT * FROM saved_prompts ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllSavedPrompts(): Flow<List<SavedPromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPrompt(prompt: SavedPromptEntity): Long

    @Update
    suspend fun updateSavedPrompt(prompt: SavedPromptEntity)

    @Query("DELETE FROM saved_prompts WHERE id = :id")
    suspend fun deleteSavedPrompt(id: Int)

    @Query("SELECT * FROM character_locks ORDER BY id DESC")
    fun getCharacterLocks(): Flow<List<CharacterLockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterLock(character: CharacterLockEntity): Long

    @Update
    suspend fun updateCharacterLock(character: CharacterLockEntity)

    @Query("DELETE FROM character_locks WHERE id = :id")
    suspend fun deleteCharacterLock(id: Int)

    @Query("SELECT * FROM credit_transactions ORDER BY timestamp DESC")
    fun getCreditTransactions(): Flow<List<CreditTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditTransaction(tx: CreditTransactionEntity)

    @Query("SELECT * FROM moderation_logs ORDER BY timestamp DESC")
    fun getModerationLogs(): Flow<List<ModerationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModerationLog(log: ModerationLogEntity)
}

@Database(
    entities = [
        VideoProjectEntity::class,
        SavedPromptEntity::class,
        CharacterLockEntity::class,
        CreditTransactionEntity::class,
        ModerationLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VizoraDatabase : RoomDatabase() {
    abstract fun vizoraDao(): VizoraDao

    companion object {
        @Volatile
        private var INSTANCE: VizoraDatabase? = null

        fun getDatabase(context: Context): VizoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VizoraDatabase::class.java,
                    "vizora_ai_studio.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
