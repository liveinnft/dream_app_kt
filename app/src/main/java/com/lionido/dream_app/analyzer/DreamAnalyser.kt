package com.lionido.dream_app.analyzer

import com.lionido.dream_app.model.*
import java.util.*

import kotlin.text.Regex
import kotlin.text.lowercase
import kotlin.collections.mutableListOf
import kotlin.collections.listOf
import kotlin.collections.mutableSetOf
import kotlin.collections.groupBy
import kotlin.collections.count

/**
 * Анализатор снов - основной класс для интерпретации сновидений
 */
class DreamAnalyzer {

    private val symbolDictionary = DreamSymbolDictionary()
    private val emotionAnalyzer = EmotionAnalyzer()

    /**
     * Анализирует текст сна и возвращает интерпретацию
     */
    fun analyzeDream(dreamText: String): DreamAnalysis {
        val cleanText = cleanText(dreamText)

        // Извлекаем символы
        val symbols = extractSymbols(cleanText)

        // Анализируем эмоции
        val emotions = emotionAnalyzer.analyzeEmotions(cleanText)
        val mood = determineMood(emotions, cleanText)

        // Определяем тип сна
        val dreamType = determineDreamType(cleanText, emotions)

        // Создаем интерпретацию
        val interpretation = generateInterpretation(symbols, emotions, mood)

        // Извлекаем теги
        val tags = extractTags(symbols, emotions)

        return DreamAnalysis(
            symbols = symbols,
            emotions = emotions,
            mood = mood,
            dreamType = dreamType,
            interpretation = interpretation,
            tags = tags,
            keyThemes = extractKeyThemes(symbols)
        )
    }

    private fun cleanText(text: String): String {
        return text.lowercase(Locale.getDefault())
            .replace(Regex("[^а-яё\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractSymbols(text: String): List<DreamSymbol> {
        val symbols = mutableListOf<DreamSymbol>()
        val words = text.split(" ")

        for (word in words) {
            val symbol = symbolDictionary.findSymbol(word)
            if (symbol != null) {
                // Проверяем, есть ли уже такой символ
                val existingSymbol = symbols.find { it.name == symbol.name }
                if (existingSymbol != null) {
                    // Увеличиваем частоту
                    symbols[symbols.indexOf(existingSymbol)] =
                        existingSymbol.copy(frequency = existingSymbol.frequency + 1)
                } else {
                    symbols.add(symbol)
                }
            }
        }

        return symbols.sortedByDescending { it.frequency }
    }

    private fun determineMood(emotions: List<String>, text: String): DreamMood {
        val positiveWords = listOf(
            "счастье", "радость", "любовь", "смех", "улыбка", "свет", "красота",
            "успех", "победа", "друзья", "семья", "праздник", "подарок"
        )

        val negativeWords = listOf(
            "страх", "тревога", "ужас", "боль", "грусть", "темнота", "смерть",
            "война", "кровь", "плач", "одиночество", "потеря", "провал"
        )

        val positiveScore = positiveWords.count { text.contains(it) } +
                emotions.count { it in listOf("радость", "счастье", "любовь", "восторг") }

        val negativeScore = negativeWords.count { text.contains(it) } +
                emotions.count { it in listOf("страх", "тревога", "грусть", "злость") }

        return when {
            positiveScore >= 3 && negativeScore == 0 -> DreamMood.VERY_POSITIVE
            positiveScore > negativeScore && positiveScore >= 2 -> DreamMood.POSITIVE
            negativeScore > positiveScore && negativeScore >= 2 -> DreamMood.NEGATIVE
            negativeScore >= 3 && positiveScore == 0 -> DreamMood.VERY_NEGATIVE
            positiveScore > 0 && negativeScore > 0 -> DreamMood.MIXED
            else -> DreamMood.NEUTRAL
        }
    }

    private fun determineDreamType(text: String, emotions: List<String>): DreamType {
        return when {
            text.contains("понял что сплю") ||
                    text.contains("осознал что во сне") ||
                    text.contains("управлял сном") -> DreamType.LUCID

            emotions.contains("страх") &&
                    (text.contains("преследовал") || text.contains("убегал") ||
                            text.contains("кошмар")) -> DreamType.NIGHTMARE

            text.contains("опять снилось") ||
                    text.contains("снится часто") ||
                    text.contains("повторяется") -> DreamType.RECURRING

            else -> DreamType.ORDINARY
        }
    }

    private fun generateInterpretation(
        symbols: List<DreamSymbol>,
        emotions: List<String>,
        mood: DreamMood
    ): String {
        val interpretation = StringBuilder()

        // Общая интерпретация настроения
        interpretation.append(getMoodInterpretation(mood))
        interpretation.append("\n\n")

        // Интерпретация основных символов
        if (symbols.isNotEmpty()) {
            interpretation.append("Ключевые символы в вашем сне:\n\n")
            symbols.take(5).forEach { symbol ->
                interpretation.append("• ${symbol.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}: ${symbol.meaning}")
                if (symbol.frequency > 1) {
                    interpretation.append(" (встречается ${symbol.frequency} раз)")
                }
                interpretation.append("\n")
            }
        }

        // Эмоциональная составляющая
        if (emotions.isNotEmpty()) {
            interpretation.append("\nЭмоциональная окраска сна указывает на ")
            interpretation.append(getEmotionalInterpretation(emotions))
        }

        return interpretation.toString()
    }

    private fun getMoodInterpretation(mood: DreamMood): String {
        return when (mood) {
            DreamMood.VERY_POSITIVE ->
                "Ваш сон наполнен очень позитивной энергией. Это может указывать на внутреннюю гармонию и оптимистичный взгляд на жизнь."

            DreamMood.POSITIVE ->
                "Позитивная энергия сна говорит о том, что вы находитесь в хорошем эмоциональном состоянии."

            DreamMood.NEUTRAL ->
                "Ваш сон имеет нейтральную эмоциональную окраску, что может отражать спокойное состояние ума."

            DreamMood.NEGATIVE ->
                "Негативные эмоции в сне могут отражать внутренние переживания или стресс в реальной жизни."

            DreamMood.VERY_NEGATIVE ->
                "Сильные негативные эмоции в сне требуют внимания. Возможно, стоит обратиться к специалисту."

            DreamMood.MIXED ->
                "Смешанные эмоции в сне отражают сложность ваших внутренних переживаний."
        }
    }

    private fun getEmotionalInterpretation(emotions: List<String>): String {
        return when {
            emotions.contains("страх") ->
                "необходимость преодоления внутренних барьеров или опасений."

            emotions.contains("радость") ->
                "гармонию с собой и окружающим миром."

            emotions.contains("грусть") ->
                "потребность в эмоциональном исцелении или принятии потерь."

            emotions.contains("любовь") ->
                "открытость к близости и глубоким эмоциональным связям."

            else -> "ваше текущее эмоциональное состояние."
        }
    }

    private fun extractTags(symbols: List<DreamSymbol>, emotions: List<String>): List<String> {
        val tags = mutableSetOf<String>()

        // Теги на основе категорий символов
        symbols.forEach { symbol ->
            when (symbol.category) {
                SymbolCategory.PEOPLE -> tags.add("люди")
                SymbolCategory.ANIMALS -> tags.add("животные")
                SymbolCategory.NATURE -> tags.add("природа")
                SymbolCategory.PLACES -> tags.add("места")
                SymbolCategory.OBJECTS -> tags.add("предметы")
                SymbolCategory.ACTIONS -> tags.add("действия")
                else -> {}
            }
        }

        // Теги на основе эмоций
        emotions.forEach { emotion ->
            tags.add(emotion)
        }

        return tags.toList()
    }

    private fun extractKeyThemes(symbols: List<DreamSymbol>): List<String> {
        return symbols.groupBy { it.category }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .map { it.key.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }
}

/**
 * Результат анализа сна
 */
data class DreamAnalysis(
    val symbols: List<DreamSymbol>,
    val emotions: List<String>,
    val mood: DreamMood,
    val dreamType: DreamType,
    val interpretation: String,
    val tags: List<String>,
    val keyThemes: List<String>
)

/**
 * ML-анализатор для будущего расширения
 */
// Класс DreamMLAnalyzer удалён во избежание дублирования