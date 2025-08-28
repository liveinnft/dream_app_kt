package com.lionido.dream_app.storage

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object для работы с базой данных снов
 */
@Dao
interface DreamDao {

    @Query("SELECT * FROM dreams ORDER BY dateCreated DESC")
    fun getAllDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams ORDER BY dateCreated DESC")
    suspend fun getAllDreamsSync(): List<DreamEntity>

    @Query("SELECT * FROM dreams WHERE id = :dreamId")
    suspend fun getDreamById(dreamId: String): DreamEntity?

    @Query("SELECT * FROM dreams ORDER BY dateCreated DESC LIMIT :limit")
    suspend fun getRecentDreams(limit: Int): List<DreamEntity>

    @Query("""
        SELECT * FROM dreams 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%' 
        OR interpretation LIKE '%' || :query || '%'
        ORDER BY dateCreated DESC
    """)
    suspend fun searchDreams(query: String): List<DreamEntity>

    @Query("SELECT * FROM dreams WHERE dateCreated BETWEEN :startDate AND :endDate ORDER BY dateCreated DESC")
    suspend fun getDreamsByDateRange(startDate: Long, endDate: Long): List<DreamEntity>

    @Query("SELECT COUNT(*) FROM dreams")
    suspend fun getDreamsCount(): Int

    @Query("SELECT COUNT(*) FROM dreams WHERE lucidDream = 1")
    suspend fun getLucidDreamsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: DreamEntity): Long

    @Update
    suspend fun updateDream(dream: DreamEntity): Int

    @Delete
    suspend fun deleteDream(dream: DreamEntity): Int

    @Query("DELETE FROM dreams WHERE id = :dreamId")
    suspend fun deleteDreamById(dreamId: String): Int

    @Query("DELETE FROM dreams")
    suspend fun clearAllDreams()
}