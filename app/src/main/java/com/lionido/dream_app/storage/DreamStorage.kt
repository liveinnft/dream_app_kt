package com.lionido.dream_app.storage

import android.content.Context
import com.lionido.dream_app.model.Dream
import kotlinx.coroutines.*

/**
 * Локальное хранилище снов с использованием Room Database
 */
class DreamStorage(context: Context) {

    private val database = DreamDatabase.getDatabase(context)
    private val dreamDao = database.dreamDao()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Сохраняет сон в локальное хранилище
     */
    fun saveDream(dream: Dream): Boolean {
        return try {
            runBlocking {
                dreamDao.insertDream(DreamEntity.fromDream(dream))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Получает все сны из хранилища
     */
    fun getAllDreams(): List<Dream> {
        return try {
            runBlocking {
                dreamDao.getAllDreamsSync().map { it.toDream() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Получает сон по ID
     */
    fun getDreamById(dreamId: String): Dream? {
        return try {
            runBlocking {
                dreamDao.getDreamById(dreamId)?.toDream()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Обновляет существующий сон
     */
    fun updateDream(updatedDream: Dream): Boolean {
        return try {
            runBlocking {
                dreamDao.updateDream(DreamEntity.fromDream(updatedDream)) > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Удаляет сон по ID
     */
    fun deleteDream(dreamId: String): Boolean {
        return try {
            runBlocking {
                dreamDao.deleteDreamById(dreamId) > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Получает количество сохраненных снов
     */
    fun getDreamsCount(): Int {
        return try {
            runBlocking {
                dreamDao.getDreamsCount()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Получает последние N снов
     */
    fun getRecentDreams(limit: Int = 10): List<Dream> {
        return try {
            runBlocking {
                dreamDao.getRecentDreams(limit).map { it.toDream() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Поиск снов по тексту
     */
    fun searchDreams(query: String): List<Dream> {
        val searchQuery = query.lowercase().trim()
        if (searchQuery.isEmpty()) return emptyList()

        return try {
            runBlocking {
                dreamDao.searchDreams(searchQuery).map { it.toDream() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Получает сны за определенный период
     */
    fun getDreamsByDateRange(startDate: Long, endDate: Long): List<Dream> {
        return try {
            runBlocking {
                dreamDao.getDreamsByDateRange(startDate, endDate).map { it.toDream() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Получает статистику снов
     */
    fun getDreamStatistics(): DreamStatistics {
        val allDreams = getAllDreams()

        val emotionFrequency = allDreams
            .flatMap { it.emotions }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        val symbolFrequency = allDreams
            .flatMap { it.symbols }
            .groupBy { it.name }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        val tagFrequency = allDreams
            .flatMap { it.tags }
            .groupBy { it }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }

        val moodDistribution = allDreams
            .groupBy { it.mood }
            .mapValues { it.value.size }

        val lucidDreamsCount = try {
            runBlocking {
                dreamDao.getLucidDreamsCount()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            allDreams.count { it.lucidDream }
        }

        return DreamStatistics(
            totalDreams = allDreams.size,
            averageDreamsPerWeek = calculateAverageDreamsPerWeek(allDreams),
            mostCommonEmotions = emotionFrequency.take(5),
            mostCommonSymbols = symbolFrequency.take(5),
            mostCommonTags = tagFrequency.take(5),
            moodDistribution = moodDistribution,
            lucidDreamsCount = lucidDreamsCount
        )
    }

    private fun calculateAverageDreamsPerWeek(dreams: List<Dream>): Double {
        if (dreams.isEmpty()) return 0.0

        val now = System.currentTimeMillis()
        val oldestDream = dreams.minByOrNull { it.dateCreated.time }?.dateCreated?.time ?: now

        val periodInWeeks = (now - oldestDream) / (1000 * 60 * 60 * 24 * 7).toDouble()

        return if (periodInWeeks > 0) dreams.size / periodInWeeks else dreams.size.toDouble()
    }

    /**
     * Очищает все данные
     */
    fun clearAllData() {
        try {
            runBlocking {
                dreamDao.clearAllDreams()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Освобождает ресурсы
     */
    fun close() {
        scope.cancel()
    }
}

/**
 * Статистика снов
 */
data class DreamStatistics(
    val totalDreams: Int,
    val averageDreamsPerWeek: Double,
    val mostCommonEmotions: List<Pair<String, Int>>,
    val mostCommonSymbols: List<Pair<String, Int>>,
    val mostCommonTags: List<Pair<String, Int>>,
    val moodDistribution: Map<com.lionido.dream_app.model.DreamMood, Int>,
    val lucidDreamsCount: Int
)