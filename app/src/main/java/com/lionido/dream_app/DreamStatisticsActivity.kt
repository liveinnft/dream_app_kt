package com.lionido.dream_app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lionido.dream_app.adapter.StatisticsAdapter
import com.lionido.dream_app.model.DreamMood
import com.lionido.dream_app.storage.DreamStorage
import com.lionido.dream_app.storage.DreamStatistics
import kotlin.math.roundToInt

class DreamStatisticsActivity : AppCompatActivity() {

    private lateinit var dreamStorage: DreamStorage

    private lateinit var totalDreamsText: TextView
    private lateinit var averageDreamsText: TextView
    private lateinit var lucidDreamsText: TextView
    private lateinit var moodDistributionText: TextView

    private lateinit var emotionsRecycler: RecyclerView
    private lateinit var symbolsRecycler: RecyclerView
    private lateinit var tagsRecycler: RecyclerView

    private lateinit var emotionsAdapter: StatisticsAdapter
    private lateinit var symbolsAdapter: StatisticsAdapter
    private lateinit var tagsAdapter: StatisticsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dream_statistics)

        dreamStorage = DreamStorage(this)

        initViews()
        setupRecyclerViews()
        setupClickListeners()
        loadStatistics()
    }

    private fun initViews() {
        totalDreamsText = findViewById(R.id.text_total_dreams)
        averageDreamsText = findViewById(R.id.text_average_dreams)
        lucidDreamsText = findViewById(R.id.text_lucid_dreams)
        moodDistributionText = findViewById(R.id.text_mood_distribution)

        emotionsRecycler = findViewById(R.id.emotions_recycler)
        symbolsRecycler = findViewById(R.id.symbols_recycler)
        tagsRecycler = findViewById(R.id.tags_recycler)
    }

    private fun setupRecyclerViews() {
        emotionsAdapter = StatisticsAdapter()
        symbolsAdapter = StatisticsAdapter()
        tagsAdapter = StatisticsAdapter()

        emotionsRecycler.apply {
            adapter = emotionsAdapter
            layoutManager = LinearLayoutManager(this@DreamStatisticsActivity)
        }

        symbolsRecycler.apply {
            adapter = symbolsAdapter
            layoutManager = LinearLayoutManager(this@DreamStatisticsActivity)
        }

        tagsRecycler.apply {
            adapter = tagsAdapter
            layoutManager = LinearLayoutManager(this@DreamStatisticsActivity)
        }
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    private fun loadStatistics() {
        val statistics = dreamStorage.getDreamStatistics()
        displayStatistics(statistics)
    }

    private fun displayStatistics(stats: DreamStatistics) {
        // Основные показатели
        totalDreamsText.text = "Всего снов: ${stats.totalDreams}"

        averageDreamsText.text = "Снов в неделю: ${(stats.averageDreamsPerWeek * 10).roundToInt() / 10.0}"

        lucidDreamsText.text = "Осознанных снов: ${stats.lucidDreamsCount}"

        // Распределение настроений
        val moodText = buildMoodDistributionText(stats.moodDistribution)
        moodDistributionText.text = moodText

        // Топ эмоции
        emotionsAdapter.updateItems(
            stats.mostCommonEmotions.map {
                StatisticsAdapter.StatItem(
                    it.first.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    it.second
                )
            }
        )

        // Топ символы
        symbolsAdapter.updateItems(
            stats.mostCommonSymbols.map {
                StatisticsAdapter.StatItem(
                    it.first.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    it.second
                )
            }
        )

        // Топ теги
        tagsAdapter.updateItems(
            stats.mostCommonTags.map {
                StatisticsAdapter.StatItem(
                    it.first.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase() else char.toString()
                    },
                    it.second
                )
            }
        )
    }

    private fun buildMoodDistributionText(moodDistribution: Map<DreamMood, Int>): String {
        if (moodDistribution.isEmpty()) return "Нет данных о настроении"

        val total = moodDistribution.values.sum()
        if (total == 0) return "Нет данных о настроении"

        val sortedMoods = moodDistribution.entries.sortedByDescending { it.value }
        val result = StringBuilder("Распределение настроений:\n")

        sortedMoods.forEach { (mood, count) ->
            val percentage = (count * 100.0 / total).roundToInt()
            val moodEmoji = getMoodEmoji(mood)
            val moodText = getMoodText(mood)
            result.append("$moodEmoji $moodText: $count ($percentage%)\n")
        }

        return result.toString().trim()
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
}