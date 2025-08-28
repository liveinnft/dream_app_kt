package com.lionido.dream_app.model

import java.util.*

/**
 * Модель для представления сна
 */
data class Dream(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val audioPath: String? = null,
    val dateCreated: Date = Date(),
    val emotions: List<String> = emptyList(),
    val symbols: List<DreamSymbol> = emptyList(),
    val interpretation: String = "",
    val tags: List<String> = emptyList(),
    val mood: DreamMood = DreamMood.NEUTRAL,
    val lucidDream: Boolean = false
)

/**
 * Символы в сне
 */
data class DreamSymbol(
    val name: String,
    val meaning: String,
    val category: SymbolCategory,
    val frequency: Int = 1
)

/**
 * Категории символов
 */
enum class SymbolCategory {
    PEOPLE,      // Люди
    ANIMALS,     // Животные
    NATURE,      // Природа
    OBJECTS,     // Предметы
    PLACES,      // Места
    ACTIONS,     // Действия
    EMOTIONS,    // Эмоции
    COLORS,      // Цвета
    NUMBERS,     // Числа
    WEATHER,     // Погода
    BUILDINGS,   // Здания
    VEHICLES,    // Транспорт
    FOOD,        // Еда
    CLOTHING,    // Одежда
    OTHER        // Другое
}

/**
 * Настроение сна
 */
enum class DreamMood {
    VERY_POSITIVE,  // Очень позитивное
    POSITIVE,       // Позитивное
    NEUTRAL,        // Нейтральное
    NEGATIVE,       // Негативное
    VERY_NEGATIVE,  // Очень негативное
    MIXED          // Смешанное
}

/**
 * Типы снов
 */
enum class DreamType {
    ORDINARY,      // Обычный сон
    LUCID,         // Осознанный сон
    NIGHTMARE,     // Кошмар
    RECURRING,     // Повторяющийся сон
    PROPHETIC,     // Вещий сон
    HEALING        // Исцеляющий сон
}