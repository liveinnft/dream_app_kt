package com.lionido.dream_app.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lionido.dream_app.model.Dream

/**
 * Локальное хранилище снов с использованием SharedPreferences
 */
class DreamStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("dreams_storage", Context.MODE_PRIVATE)
    private val gson = Gson()

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
            dreams.add(0, dream) // Добавляем в начало списка (новые сверху)

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
                gson.fromJson(dreamsJson, type)
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
        return getAllDreams().find { it.id == dreamId }
    }

    /**
     * Обновляет существующий сон
     */
    fun updateDream(updatedDream: Dream): Boolean {
        return try {
            val dreams = getAllDreams().toMutableList()
            val index = dreams.indexOfFirst { it.id == updatedDream.id }

            if (index != -1) {
                dreams[index] = updatedDream
                val dreamsJson = gson.toJson(dreams)

                prefs.edit()
                    .putString(DREAMS_KEY, dreamsJson)
                    .apply()
                true
            } else {
                false
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
        return prefs.getInt(DREAMS_COUNT_KEY, 0)
    }

    /**
     * Получает последние N снов
     */
    fun getRecentDreams(limit: Int = 10): List<Dream> {
        return getAllDreams().take(limit)
    }

    /**
     * Поиск снов по тексту
     */
    fun searchDreams(query: String): List<Dream> {
        val searchQuery = query.lowercase().trim()
        if (searchQuery.isEmpty()) return emptyList()

        return getAllDreams().filter { dream ->
            dream.title.lowercase().contains(searchQuery) ||
                    dream.content.lowercase().contains(searchQuery) ||
                    dream.interpretation.lowercase().contains(searchQuery) ||
                    dream.tags.any { it.lowercase().contains(searchQuery) } ||
                    dream.emotions.any { it.lowercase().contains(searchQuery) }
        }
    }

    /**
     * Получает сны за определенный период
     */
    fun getDreamsByDateRange(startDate: Long, endDate: Long): List<Dream> {
        return getAllDreams().filter { dream ->
            val dreamTime = dream.dateCreated.time
            dreamTime >= startDate && dreamTime <= endDate
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

        return DreamStatistics(
            totalDreams = allDreams.size,
            averageDreamsPerWeek = calculateAverageDreamsPerWeek(allDreams),
            mostCommonEmotions = emotionFrequency.take(5),
            mostCommonSymbols = symbolFrequency.take(5),
            mostCommonTags = tagFrequency.take(5),
            moodDistribution = moodDistribution,
            lucidDreamsCount = allDreams.count { it.lucidDream }
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
        prefs.edit()
            .remove(DREAMS_KEY)
            .remove(DREAMS_COUNT_KEY)
            .apply()
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