package com.lionido.dream_app.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionido.dream_app.model.Dream
import kotlinx.coroutines.*

/**
 * Совместимая версия хранилища снов с использованием SharedPreferences
 * Используется как fallback если Room недоступен
 */
class DreamStorageCompat(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dreams_storage_compat", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val DREAMS_KEY = "dreams_list"
        private const val DREAMS_COUNT_KEY = "dreams_count"
    }

    /**
     * Сохраняет сон в локальное хранилище
     */
    fun saveDream(dream: Dream): Boolean {
        return try {
            val dreams = getAllDreams().toMutableList()
            
            // Удаляем старый сон с таким же ID, если он существует
            dreams.removeAll { it.id == dream.id }
            
            // Добавляем новый сон в начало списка
            dreams.add(0, dream)

            val dreamsJson = gson.toJson(dreams)

            prefs.edit()
                .putString(DREAMS_KEY, dreamsJson)
                .putInt(DREAMS_COUNT_KEY, dreams.size)
                .apply()

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
            val dreamsJson = prefs.getString(DREAMS_KEY, null)
            if (dreamsJson != null) {
                val type = object : TypeToken<List<Dream>>() {}.type
                gson.fromJson(dreamsJson, type) ?: emptyList()
            } else {
                emptyList()
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
            getAllDreams().find { it.id == dreamId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Обновляет существующий сон
     */
    fun updateDream(updatedDream: Dream): Boolean {
        return saveDream(updatedDream) // В SharedPreferences это то же самое
    }

    /**
     * Удаляет сон по ID
     */
    fun deleteDream(dreamId: String): Boolean {
        return try {
            val dreams = getAllDreams().toMutableList()
            val removed = dreams.removeIf { it.id == dreamId }

            if (removed) {
                val dreamsJson = gson.toJson(dreams)

                prefs.edit()
                    .putString(DREAMS_KEY, dreamsJson)
                    .putInt(DREAMS_COUNT_KEY, dreams.size)
                    .apply()
            }

            removed
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
            prefs.getInt(DREAMS_COUNT_KEY, 0)
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
            getAllDreams().take(limit)
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
            getAllDreams().filter { dream ->
                dream.title.lowercase().contains(searchQuery) ||
                        dream.content.lowercase().contains(searchQuery) ||
                        dream.interpretation.lowercase().contains(searchQuery) ||
                        dream.tags.any { it.lowercase().contains(searchQuery) } ||
                        dream.emotions.any { it.lowercase().contains(searchQuery) }
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
            getAllDreams().filter { dream ->
                val dreamTime = dream.dateCreated.time
                dreamTime >= startDate && dreamTime <= endDate
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

        val lucidDreamsCount = allDreams.count { it.lucidDream }

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
            prefs.edit()
                .remove(DREAMS_KEY)
                .remove(DREAMS_COUNT_KEY)
                .apply()
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