package com.lionido.dream_app

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Менеджер пользовательских тегов и категорий
 */
object TagManager {
    
    private const val PREFS_NAME = "tag_prefs"
    private const val KEY_CUSTOM_TAGS = "custom_tags"
    private const val KEY_TAG_COLORS = "tag_colors"
    private const val KEY_PREDEFINED_TAGS = "predefined_tags"
    
    private val gson = Gson()
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Предопределенные теги
     */
    fun getPredefinedTags(): List<String> {
        return listOf(
            "Кошмар", "Осознанный сон", "Повторяющийся", 
            "Яркий", "Эмоциональный", "Странный",
            "Семья", "Работа", "Путешествие",
            "Детство", "Будущее", "Страхи",
            "Любовь", "Успех", "Природа",
            "Животные", "Полет", "Падение",
            "Вода", "Огонь", "Дом",
            "Учеба", "Друзья", "Незнакомцы"
        )
    }
    
    /**
     * Получает все пользовательские теги
     */
    fun getCustomTags(context: Context): List<String> {
        val prefs = getPrefs(context)
        val tagsJson = prefs.getString(KEY_CUSTOM_TAGS, "[]")
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(tagsJson, type) ?: emptyList()
    }
    
    /**
     * Добавляет новый пользовательский тег
     */
    fun addCustomTag(context: Context, tag: String): Boolean {
        val normalizedTag = tag.trim().lowercase()
        if (normalizedTag.isEmpty()) return false
        
        val existingTags = getCustomTags(context).map { it.lowercase() }
        val predefinedTags = getPredefinedTags().map { it.lowercase() }
        
        // Проверяем, что тег не существует
        if (normalizedTag in existingTags || normalizedTag in predefinedTags) {
            return false
        }
        
        val newTags = getCustomTags(context).toMutableList()
        newTags.add(tag.trim())
        
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_CUSTOM_TAGS, gson.toJson(newTags)).apply()
        
        return true
    }
    
    /**
     * Удаляет пользовательский тег
     */
    fun removeCustomTag(context: Context, tag: String) {
        val tags = getCustomTags(context).toMutableList()
        tags.remove(tag)
        
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_CUSTOM_TAGS, gson.toJson(tags)).apply()
    }
    
    /**
     * Получает все доступные теги (предопределенные + пользовательские)
     */
    fun getAllTags(context: Context): List<String> {
        val predefined = getPredefinedTags()
        val custom = getCustomTags(context)
        return (predefined + custom).distinct().sorted()
    }
    
    /**
     * Получает цвет для тега
     */
    fun getTagColor(context: Context, tag: String): String {
        val prefs = getPrefs(context)
        val colorsJson = prefs.getString(KEY_TAG_COLORS, "{}")
        val type = object : TypeToken<Map<String, String>>() {}.type
        val colors: Map<String, String> = gson.fromJson(colorsJson, type) ?: emptyMap()
        
        return colors[tag] ?: getDefaultTagColor(tag)
    }
    
    /**
     * Устанавливает цвет для тега
     */
    fun setTagColor(context: Context, tag: String, color: String) {
        val prefs = getPrefs(context)
        val colorsJson = prefs.getString(KEY_TAG_COLORS, "{}")
        val type = object : TypeToken<MutableMap<String, String>>() {}.type
        val colors: MutableMap<String, String> = gson.fromJson(colorsJson, type) ?: mutableMapOf()
        
        colors[tag] = color
        prefs.edit().putString(KEY_TAG_COLORS, gson.toJson(colors)).apply()
    }
    
    /**
     * Получает цвет по умолчанию для тега
     */
    private fun getDefaultTagColor(tag: String): String {
        val colors = listOf(
            "#667eea", "#764ba2", "#f093fb", "#4facfe", "#00f2fe",
            "#43e97b", "#38f9d7", "#ffecd2", "#fcb69f", "#a8edea",
            "#fed6e3", "#d299c2", "#ffd89b", "#19547b", "#667292"
        )
        
        val hash = tag.hashCode()
        val index = kotlin.math.abs(hash) % colors.size
        return colors[index]
    }
    
    /**
     * Получает популярные теги на основе использования
     */
    fun getPopularTags(@Suppress("UNUSED_PARAMETER") context: Context, dreams: List<com.lionido.dream_app.model.Dream>): List<Pair<String, Int>> {
        val tagCount = mutableMapOf<String, Int>()
        
        dreams.forEach { dream ->
            dream.tags.forEach { tag ->
                tagCount[tag] = tagCount.getOrDefault(tag, 0) + 1
            }
        }
        
        return tagCount.toList().sortedByDescending { it.second }
    }
    
    /**
     * Предлагает теги на основе содержимого сна
     */
    fun suggestTags(content: String): List<String> {
        val suggestions = mutableListOf<String>()
        val lowerContent = content.lowercase()
        
        // Простые правила для предложения тегов
        val tagRules = mapOf(
            "кошмар" to listOf("кошмар", "страх", "темный"),
            "летать" to listOf("полет", "свобода"),
            "падать" to listOf("падение", "страх"),
            "вода" to listOf("вода", "океан", "река"),
            "огонь" to listOf("огонь", "тепло"),
            "семья" to listOf("семья", "родители", "дети"),
            "работа" to listOf("работа", "офис", "коллеги"),
            "школа" to listOf("учеба", "школа", "экзамен"),
            "дом" to listOf("дом", "детство"),
            "животное" to listOf("животные", "природа"),
            "машина" to listOf("транспорт", "путешествие"),
            "смерть" to listOf("смерть", "грусть", "потеря"),
            "свадьба" to listOf("любовь", "праздник"),
            "деньги" to listOf("деньги", "богатство", "успех")
        )
        
        tagRules.forEach { (keyword, tags) ->
            if (lowerContent.contains(keyword)) {
                suggestions.addAll(tags)
            }
        }
        
        return suggestions.distinct().take(5)
    }
}