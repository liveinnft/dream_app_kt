package com.lionido.dream_app.analyzer

import com.lionido.dream_app.model.DreamSymbol
import com.lionido.dream_app.model.SymbolCategory

/**
 * Словарь символов сновидений с их интерпретациями
 */
class DreamSymbolDictionary {

    private val symbolMap = buildSymbolMap()

    fun findSymbol(word: String): DreamSymbol? {
        return symbolMap[word.lowercase()]
    }

    private fun buildSymbolMap(): Map<String, DreamSymbol> {
        return mapOf(
            // Люди
            "мама" to DreamSymbol("Мать", "Символ защиты, заботы и материнской любви", SymbolCategory.PEOPLE),
            "папа" to DreamSymbol("Отец", "Символ авторитета, силы и руководства", SymbolCategory.PEOPLE),
            "дети" to DreamSymbol("Дети", "Символ невинности, новых начинаний и потенциала", SymbolCategory.PEOPLE),
            "ребенок" to DreamSymbol("Ребенок", "Внутренний ребенок, творческий потенциал", SymbolCategory.PEOPLE),
            "друг" to DreamSymbol("Друг", "Поддержка, товарищество, аспекты личности", SymbolCategory.PEOPLE),
            "враг" to DreamSymbol("Враг", "Внутренние конфликты, подавленные эмоции", SymbolCategory.PEOPLE),

            // Животные
            "собака" to DreamSymbol("Собака", "Верность, дружба, защита и преданность", SymbolCategory.ANIMALS),
            "кот" to DreamSymbol("Кошка", "Независимость, интуиция, женская энергия", SymbolCategory.ANIMALS),
            "кошка" to DreamSymbol("Кошка", "Независимость, интуиция, женская энергия", SymbolCategory.ANIMALS),
            "лошадь" to DreamSymbol("Лошадь", "Сила, свобода, страсть и энергия", SymbolCategory.ANIMALS),
            "птица" to DreamSymbol("Птица", "Свобода, духовность, стремление к высшему", SymbolCategory.ANIMALS),
            "змея" to DreamSymbol("Змея", "Трансформация, мудрость или скрытые опасности", SymbolCategory.ANIMALS),
            "медведь" to DreamSymbol("Медведь", "Сила, защита материнства, внутренняя мощь", SymbolCategory.ANIMALS),
            "волк" to DreamSymbol("Волк", "Инстинкты, дикая природа, лидерство", SymbolCategory.ANIMALS),

            // Природа
            "вода" to DreamSymbol("Вода", "Эмоции, подсознание, очищение и обновление", SymbolCategory.NATURE),
            "море" to DreamSymbol("Море", "Безграничность эмоций, подсознание", SymbolCategory.NATURE),
            "река" to DreamSymbol("Река", "Течение жизни, перемены, движение", SymbolCategory.NATURE),
            "огонь" to DreamSymbol("Огонь", "Страсть, трансформация, очищение", SymbolCategory.NATURE),
            "дерево" to DreamSymbol("Дерево", "Рост, стабильность, жизненная сила", SymbolCategory.NATURE),
            "лес" to DreamSymbol("Лес", "Неизвестность, подсознание, природные инстинкты", SymbolCategory.NATURE),
            "гора" to DreamSymbol("Гора", "Препятствия, достижения, духовное восхождение", SymbolCategory.NATURE),
            "солнце" to DreamSymbol("Солнце", "Сознание, энергия, жизненная сила", SymbolCategory.NATURE),
            "луна" to DreamSymbol("Луна", "Подсознание, интуиция, женская энергия", SymbolCategory.NATURE),
            "звезды" to DreamSymbol("Звезды", "Надежда, мечты, духовное руководство", SymbolCategory.NATURE),

            // Места
            "дом" to DreamSymbol("Дом", "Безопасность, семья, внутренний мир", SymbolCategory.PLACES),
            "школа" to DreamSymbol("Школа", "Обучение, прошлый опыт, социальные навыки", SymbolCategory.PLACES),
            "больница" to DreamSymbol("Больница", "Исцеление, забота о здоровье, уязвимость", SymbolCategory.PLACES),
            "церковь" to DreamSymbol("Церковь", "Духовность, поиск смысла, святость", SymbolCategory.PLACES),
            "магазин" to DreamSymbol("Магазин", "Выбор, материальные потребности", SymbolCategory.PLACES),
            "кладбище" to DreamSymbol("Кладбище", "Окончание, трансформация, память", SymbolCategory.PLACES),

            // Транспорт
            "машина" to DreamSymbol("Автомобиль", "Контроль над жизнью, направление движения", SymbolCategory.VEHICLES),
            "самолет" to DreamSymbol("Самолёт", "Стремление к высшему, быстрые перемены", SymbolCategory.VEHICLES),
            "поезд" to DreamSymbol("Поезд", "Жизненный путь, предназначение", SymbolCategory.VEHICLES),
            "корабль" to DreamSymbol("Корабль", "Эмоциональное путешествие, переход", SymbolCategory.VEHICLES),

            // Предметы
            "ключ" to DreamSymbol("Ключ", "Решение проблем, доступ к тайнам", SymbolCategory.OBJECTS),
            "дверь" to DreamSymbol("Дверь", "Новые возможности, переходы", SymbolCategory.OBJECTS),
            "зеркало" to DreamSymbol("Зеркало", "Самопознание, отражение души", SymbolCategory.OBJECTS),
            "книга" to DreamSymbol("Книга", "Знания, мудрость, обучение", SymbolCategory.OBJECTS),
            "телефон" to DreamSymbol("Телефон", "Коммуникация, связь с другими", SymbolCategory.OBJECTS),
            "деньги" to DreamSymbol("Деньги", "Ценности, самооценка, власть", SymbolCategory.OBJECTS),

            // Еда
            "хлеб" to DreamSymbol("Хлеб", "Основные потребности, питание души", SymbolCategory.FOOD),
            "фрукты" to DreamSymbol("Фрукты", "Плоды труда, награда, изобилие", SymbolCategory.FOOD),
            "мясо" to DreamSymbol("Мясо", "Силы, животные инстинкты", SymbolCategory.FOOD),
            "сладости" to DreamSymbol("Сладости", "Удовольствие, награда себе", SymbolCategory.FOOD),

            // Цвета
            "белый" to DreamSymbol("Белый цвет", "Чистота, невинность, новые начинания", SymbolCategory.COLORS),
            "черный" to DreamSymbol("Черный цвет", "Неизвестность, тайна, депрессия", SymbolCategory.COLORS),
            "красный" to DreamSymbol("Красный цвет", "Страсть, энергия, агрессия", SymbolCategory.COLORS),
            "синий" to DreamSymbol("Синий цвет", "Спокойствие, духовность, правда", SymbolCategory.COLORS),
            "зеленый" to DreamSymbol("Зеленый цвет", "Рост, природа, исцеление", SymbolCategory.COLORS),
            "желтый" to DreamSymbol("Желтый цвет", "Радость, интеллект, энергия", SymbolCategory.COLORS),

            // Действия
            "летать" to DreamSymbol("Полёт", "Свобода, выход за пределы ограничений", SymbolCategory.ACTIONS),
            "падать" to DreamSymbol("Падение", "Потеря контроля, страхи", SymbolCategory.ACTIONS),
            "бежать" to DreamSymbol("Бег", "Избегание проблем или стремление к цели", SymbolCategory.ACTIONS),
            "плавать" to DreamSymbol("Плавание", "Навигация через эмоции", SymbolCategory.ACTIONS),
            "танцевать" to DreamSymbol("Танец", "Гармония, выражение эмоций", SymbolCategory.ACTIONS),

            // Погода
            "дождь" to DreamSymbol("Дождь", "Очищение, эмоциональное освобождение", SymbolCategory.WEATHER),
            "снег" to DreamSymbol("Снег", "Чистота, покой, замирание", SymbolCategory.WEATHER),
            "буря" to DreamSymbol("Буря", "Эмоциональные потрясения, хаос", SymbolCategory.WEATHER),
            "туман" to DreamSymbol("Туман", "Неясность, запутанность в жизни", SymbolCategory.WEATHER)
        )
    }
}