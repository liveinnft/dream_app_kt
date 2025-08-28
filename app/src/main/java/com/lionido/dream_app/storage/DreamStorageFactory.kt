package com.lionido.dream_app.storage

import android.content.Context

/**
 * Фабрика для создания подходящего экземпляра хранилища
 */
object DreamStorageFactory {
    
    /**
     * Создает экземпляр хранилища, выбирая между Room и SharedPreferences
     * в зависимости от доступности Room
     */
    fun createStorage(context: Context): IDreamStorage {
        return try {
            // Пытаемся создать Room хранилище
            DreamStorage(context)
        } catch (e: Exception) {
            // Если Room недоступен, используем SharedPreferences
            e.printStackTrace()
            DreamStorageAdapter(DreamStorageCompat(context))
        }
    }
}

/**
 * Интерфейс для унификации работы с разными типами хранилищ
 */
interface IDreamStorage {
    fun saveDream(dream: com.lionido.dream_app.model.Dream): Boolean
    fun getAllDreams(): List<com.lionido.dream_app.model.Dream>
    fun getDreamById(dreamId: String): com.lionido.dream_app.model.Dream?
    fun updateDream(updatedDream: com.lionido.dream_app.model.Dream): Boolean
    fun deleteDream(dreamId: String): Boolean
    fun getDreamsCount(): Int
    fun getRecentDreams(limit: Int = 10): List<com.lionido.dream_app.model.Dream>
    fun searchDreams(query: String): List<com.lionido.dream_app.model.Dream>
    fun getDreamsByDateRange(startDate: Long, endDate: Long): List<com.lionido.dream_app.model.Dream>
    fun getDreamStatistics(): DreamStatistics
    fun clearAllData()
    fun close()
}

/**
 * Адаптер для DreamStorage (Room)
 */
class DreamStorageAdapter(private val storage: DreamStorage) : IDreamStorage {
    override fun saveDream(dream: com.lionido.dream_app.model.Dream) = storage.saveDream(dream)
    override fun getAllDreams() = storage.getAllDreams()
    override fun getDreamById(dreamId: String) = storage.getDreamById(dreamId)
    override fun updateDream(updatedDream: com.lionido.dream_app.model.Dream) = storage.updateDream(updatedDream)
    override fun deleteDream(dreamId: String) = storage.deleteDream(dreamId)
    override fun getDreamsCount() = storage.getDreamsCount()
    override fun getRecentDreams(limit: Int) = storage.getRecentDreams(limit)
    override fun searchDreams(query: String) = storage.searchDreams(query)
    override fun getDreamsByDateRange(startDate: Long, endDate: Long) = storage.getDreamsByDateRange(startDate, endDate)
    override fun getDreamStatistics() = storage.getDreamStatistics()
    override fun clearAllData() = storage.clearAllData()
    override fun close() = storage.close()
}

/**
 * Адаптер для DreamStorageCompat (SharedPreferences)
 */
class DreamStorageCompatAdapter(private val storage: DreamStorageCompat) : IDreamStorage {
    override fun saveDream(dream: com.lionido.dream_app.model.Dream) = storage.saveDream(dream)
    override fun getAllDreams() = storage.getAllDreams()
    override fun getDreamById(dreamId: String) = storage.getDreamById(dreamId)
    override fun updateDream(updatedDream: com.lionido.dream_app.model.Dream) = storage.updateDream(updatedDream)
    override fun deleteDream(dreamId: String) = storage.deleteDream(dreamId)
    override fun getDreamsCount() = storage.getDreamsCount()
    override fun getRecentDreams(limit: Int) = storage.getRecentDreams(limit)
    override fun searchDreams(query: String) = storage.searchDreams(query)
    override fun getDreamsByDateRange(startDate: Long, endDate: Long) = storage.getDreamsByDateRange(startDate, endDate)
    override fun getDreamStatistics() = storage.getDreamStatistics()
    override fun clearAllData() = storage.clearAllData()
    override fun close() = storage.close()
}