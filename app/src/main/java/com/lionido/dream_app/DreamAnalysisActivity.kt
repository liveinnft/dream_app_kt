package com.lionido.dream_app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lionido.dream_app.adapter.DreamSymbolAdapter
import com.lionido.dream_app.analyzer.DreamAnalysis
import com.lionido.dream_app.model.Dream
import com.lionido.dream_app.model.DreamMood
import com.lionido.dream_app.model.DreamSymbol
import com.lionido.dream_app.model.DreamType
import com.lionido.dream_app.model.SymbolCategory
import com.lionido.dream_app.storage.DreamStorage
import java.text.SimpleDateFormat
import java.util.*

class DreamAnalysisActivity : AppCompatActivity() {

    private lateinit var dreamTitleText: TextView
    private lateinit var dreamDateText: TextView
    private lateinit var dreamMoodText: TextView
    private lateinit var dreamTypeText: TextView
    private lateinit var dreamContentText: TextView
    private lateinit var dreamInterpretationText: TextView
    private lateinit var emotionsChipGroup: ChipGroup
    private lateinit var themesChipGroup: ChipGroup
    private lateinit var tagsChipGroup: ChipGroup
    private lateinit var symbolsRecycler: RecyclerView

    private lateinit var symbolsAdapter: DreamSymbolAdapter
    private lateinit var dreamStorage: DreamStorage
    private var currentDream: Dream? = null
    private var currentAnalysis: DreamAnalysis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dream_analysis)

        dreamStorage = DreamStorage(this)

        initViews()
        setupRecyclerView()
        setupClickListeners()

        // Получаем данные из intent или создаем пример
        loadDreamData()
    }

    private fun initViews() {
        dreamTitleText = findViewById(R.id.dream_title)
        dreamDateText = findViewById(R.id.dream_date)
        dreamMoodText = findViewById(R.id.dream_mood)
        dreamTypeText = findViewById(R.id.dream_type)
        dreamContentText = findViewById(R.id.dream_content)
        dreamInterpretationText = findViewById(R.id.dream_interpretation)
        emotionsChipGroup = findViewById(R.id.emotions_chip_group)
        themesChipGroup = findViewById(R.id.themes_chip_group)
        tagsChipGroup = findViewById(R.id.tags_chip_group)
        symbolsRecycler = findViewById(R.id.symbols_recycler)
    }

    private fun setupRecyclerView() {
        symbolsAdapter = DreamSymbolAdapter()
        symbolsRecycler.apply {
            adapter = symbolsAdapter
            layoutManager = LinearLayoutManager(this@DreamAnalysisActivity)
        }
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_save).setOnClickListener {
            currentDream?.let { dream ->
                val success = dreamStorage.saveDream(dream)
                if (success) {
                    Toast.makeText(this, "Сон сохранен!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Ошибка при сохранении", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<View>(R.id.btn_edit_tags).setOnClickListener {
            showTagSelectionDialog()
        }
    }

    private fun loadDreamData() {
        // Получаем dreamId из intent, если он был передан
        val dreamId = intent.getStringExtra("dreamId")

        if (dreamId != null) {
            // Загружаем существующий сон
            currentDream = dreamStorage.getDreamById(dreamId)
            currentAnalysis = null // Можно добавить сохранение анализа позже

            currentDream?.let { dream ->
                displayDreamAnalysis(dream, createAnalysisFromDream(dream))
            }
        } else {
            // Создаем пример данных для тестирования
            val sampleDream = createSampleDream()
            val sampleAnalysis = createSampleAnalysis()

            currentDream = sampleDream
            currentAnalysis = sampleAnalysis

            displayDreamAnalysis(sampleDream, sampleAnalysis)
        }
    }

    private fun createAnalysisFromDream(dream: Dream): DreamAnalysis {
        return DreamAnalysis(
            symbols = dream.symbols,
            emotions = dream.emotions,
            mood = dream.mood,
            dreamType = if (dream.lucidDream) DreamType.LUCID else DreamType.ORDINARY,
            interpretation = dream.interpretation,
            tags = dream.tags,
            keyThemes = extractKeyThemesFromSymbols(dream.symbols)
        )
    }

    private fun extractKeyThemesFromSymbols(symbols: List<DreamSymbol>): List<String> {
        return symbols.groupBy { it.category }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .map { getCategoryDisplayName(it.key) }
    }

    private fun getCategoryDisplayName(category: SymbolCategory): String {
        return when (category) {
            SymbolCategory.PEOPLE -> "Люди"
            SymbolCategory.ANIMALS -> "Животные"
            SymbolCategory.NATURE -> "Природа"
            SymbolCategory.OBJECTS -> "Предметы"
            SymbolCategory.PLACES -> "Места"
            SymbolCategory.ACTIONS -> "Действия"
            SymbolCategory.EMOTIONS -> "Эмоции"
            SymbolCategory.COLORS -> "Цвета"
            SymbolCategory.NUMBERS -> "Числа"
            SymbolCategory.WEATHER -> "Погода"
            SymbolCategory.BUILDINGS -> "Здания"
            SymbolCategory.VEHICLES -> "Транспорт"
            SymbolCategory.FOOD -> "Еда"
            SymbolCategory.CLOTHING -> "Одежда"
            SymbolCategory.OTHER -> "Другое"
        }
    }

    private fun displayDreamAnalysis(dream: Dream, analysis: DreamAnalysis) {
        // Основная информация
        dreamTitleText.text = dream.title.ifEmpty { "Мой сон" }

        val dateFormat = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale.getDefault())
        dreamDateText.text = dateFormat.format(dream.dateCreated)

        dreamMoodText.text = "${getMoodEmoji(dream.mood)} ${getMoodText(dream.mood)}"
        dreamTypeText.text = "Тип: ${getDreamTypeText(analysis.dreamType)}"

        // Содержание сна
        dreamContentText.text = dream.content.ifEmpty { "Описание сна отсутствует" }

        // Интерпретация
        dreamInterpretationText.text = dream.interpretation.ifEmpty { "Интерпретация не найдена" }

        // Эмоции
        displayEmotions(analysis.emotions)

        // Ключевые темы
        displayKeyThemes(analysis.keyThemes)

        // Пользовательские теги
        displayTags(dream.tags)

        // Символы
        symbolsAdapter.updateSymbols(dream.symbols)

        // Скрываем карточку символов, если их нет
        if (dream.symbols.isEmpty()) {
            findViewById<View>(R.id.symbols_card).visibility = View.GONE
        }
    }

    private fun displayEmotions(emotions: List<String>) {
        emotionsChipGroup.removeAllViews()

        emotions.forEach { emotion ->
            val chip = Chip(this).apply {
                text = emotion.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                isClickable = false
                setChipBackgroundColorResource(R.color.accent_color)
                setTextColor(resources.getColor(R.color.white, this@DreamAnalysisActivity.theme))
            }
            emotionsChipGroup.addView(chip)
        }

        if (emotions.isEmpty()) {
            val chip = Chip(this).apply {
                text = "Нет эмоций"
                isClickable = false
                setChipBackgroundColorResource(R.color.surface_color)
                setTextColor(
                    resources.getColor(
                        R.color.secondary_text,
                        this@DreamAnalysisActivity.theme
                    )
                )
            }
            emotionsChipGroup.addView(chip)
        }
    }

    private fun displayKeyThemes(themes: List<String>) {
        themesChipGroup.removeAllViews()

        themes.forEach { theme ->
            val chip = Chip(this).apply {
                text = theme
                isClickable = false
                setChipBackgroundColorResource(R.color.primary_color)
                setTextColor(resources.getColor(R.color.white, this@DreamAnalysisActivity.theme))
            }
            themesChipGroup.addView(chip)
        }

        if (themes.isEmpty()) {
            val chip = Chip(this).apply {
                text = "Нет ключевых тем"
                isClickable = false
                setChipBackgroundColorResource(R.color.surface_color)
                setTextColor(
                    resources.getColor(
                        R.color.secondary_text,
                        this@DreamAnalysisActivity.theme
                    )
                )
            }
            themesChipGroup.addView(chip)
        }
    }

    private fun getMoodEmoji(mood: DreamMood): String {
        return when (mood) {
            DreamMood.VERY_POSITIVE -> "😄"
            DreamMood.POSITIVE -> "😊"
            DreamMood.NEUTRAL -> "😐"
            DreamMood.NEGATIVE -> "😔"
            DreamMood.VERY_NEGATIVE -> "😰"
            DreamMood.MIXED -> "🤔"
        }
    }

    private fun getMoodText(mood: DreamMood): String {
        return when (mood) {
            DreamMood.VERY_POSITIVE -> "Очень позитивное"
            DreamMood.POSITIVE -> "Позитивное"
            DreamMood.NEUTRAL -> "Нейтральное"
            DreamMood.NEGATIVE -> "Негативное"
            DreamMood.VERY_NEGATIVE -> "Очень негативное"
            DreamMood.MIXED -> "Смешанное"
        }
    }

    private fun getDreamTypeText(type: DreamType): String {
        return when (type) {
            DreamType.ORDINARY -> "Обычный сон"
            DreamType.LUCID -> "Осознанный сон"
            DreamType.NIGHTMARE -> "Кошмар"
            DreamType.RECURRING -> "Повторяющийся сон"
            DreamType.PROPHETIC -> "Вещий сон"
            DreamType.HEALING -> "Исцеляющий сон"
        }
    }

    // Временные функции для создания примера данных
    private fun createSampleDream(): Dream {
        val sampleSymbols = listOf(
            DreamSymbol("Полет", "Свобода, выход за пределы ограничений", SymbolCategory.ACTIONS, 1),
            DreamSymbol("Птица", "Свобода, духовность, стремление к высшему", SymbolCategory.ANIMALS, 1),
            DreamSymbol("Солнце", "Сознание, энергия, жизненная сила", SymbolCategory.NATURE, 1),
            DreamSymbol("Город", "Общество, цивилизация, сложность жизни", SymbolCategory.PLACES, 1)
        )

        return Dream(
            title = "Полет над городом",
            content = "Мне снилось, что я летаю над большим городом. Внизу были высокие здания, светились окна. Я чувствовал свободу и радость. Рядом со мной летела белая птица, которая показывала мне дорогу к яркому солнцу.",
            dateCreated = Date(),
            emotions = listOf("радость", "свобода", "восторг"),
            symbols = sampleSymbols,
            interpretation = "Полет во сне символизирует стремление к свободе и преодоление ограничений. Белая птица представляет духовное руководство, а солнце - цель или просветление, к которому вы стремитесь. Город внизу может отражать повседневные заботы, от которых вы хотите освободиться.",
            tags = listOf("полет", "город", "свобода", "птица"),
            mood = DreamMood.POSITIVE,
            lucidDream = false
        )
    }

    private fun createSampleAnalysis(): DreamAnalysis {
        return DreamAnalysis(
            symbols = listOf(
                DreamSymbol("Полет", "Свобода, выход за пределы ограничений", SymbolCategory.ACTIONS, 1),
                DreamSymbol("Птица", "Свобода, духовность, стремление к высшему", SymbolCategory.ANIMALS, 1),
                DreamSymbol("Солнце", "Сознание, энергия, жизненная сила", SymbolCategory.NATURE, 1),
                DreamSymbol("Город", "Общество, цивилизация, сложность жизни", SymbolCategory.PLACES, 1)
            ),
            emotions = listOf("радость", "свобода", "восторг"),
            mood = DreamMood.POSITIVE,
            dreamType = DreamType.ORDINARY,
            interpretation = "Ваш сон наполнен позитивной энергией и символизирует стремление к достижению высших целей.",
            tags = listOf("полет", "город", "свобода", "птица"),
            keyThemes = listOf("Свобода", "Духовность", "Достижения")
        )
    }

    private fun displayTags(tags: List<String>) {
        tagsChipGroup.removeAllViews()

        if (tags.isEmpty()) {
            val emptyChip = Chip(this)
            emptyChip.text = "Теги не добавлены"
            emptyChip.isEnabled = false
            emptyChip.setTextColor(getColor(android.R.color.darker_gray))
            tagsChipGroup.addView(emptyChip)
            return
        }

        tags.forEach { tag ->
            val chip = Chip(this)
            chip.text = tag
            chip.isClickable = false
            chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(TagManager.getTagColor(this, tag))
            )
            chip.setTextColor(android.graphics.Color.WHITE)
            tagsChipGroup.addView(chip)
        }
    }

    private fun showTagSelectionDialog() {
        val currentDream = currentDream ?: return
        
        val dialog = TagSelectionDialog.newInstance(
            currentTags = currentDream.tags,
            dreamContent = currentDream.content,
            listener = object : TagSelectionDialog.TagSelectionListener {
                override fun onTagsSelected(selectedTags: List<String>) {
                    updateDreamTags(selectedTags)
                }
            }
        )
        
        dialog.show(supportFragmentManager, "TagSelectionDialog")
    }

    private fun updateDreamTags(newTags: List<String>) {
        currentDream = currentDream?.copy(tags = newTags)
        displayTags(newTags)
    }
}