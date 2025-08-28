package com.lionido.dream_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lionido.dream_app.R
import com.lionido.dream_app.model.Dream
import com.lionido.dream_app.model.DreamMood
import java.text.SimpleDateFormat
import java.util.*

class DreamsListAdapter(
    private val onDreamClick: (Dream) -> Unit
) : RecyclerView.Adapter<DreamsListAdapter.DreamViewHolder>() {

    private var dreams: List<Dream> = emptyList()
    private val dateFormat = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())

    fun updateDreams(newDreams: List<Dream>) {
        dreams = newDreams
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dream, parent, false)
        return DreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        holder.bind(dreams[position])
    }

    override fun getItemCount(): Int = dreams.size

    inner class DreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dreamTitle: TextView = itemView.findViewById(R.id.dream_title)
        private val dreamDate: TextView = itemView.findViewById(R.id.dream_date)
        private val dreamMood: TextView = itemView.findViewById(R.id.dream_mood)
        private val dreamPreview: TextView = itemView.findViewById(R.id.dream_preview)
        private val tagsChipGroup: ChipGroup = itemView.findViewById(R.id.tags_chip_group)
        private val symbolsCount: TextView = itemView.findViewById(R.id.symbols_count)

        fun bind(dream: Dream) {
            dreamTitle.text = if (dream.title.isNotEmpty()) dream.title else "Сон без названия"
            dreamDate.text = dateFormat.format(dream.dateCreated)
            dreamMood.text = "${getMoodEmoji(dream.mood)} ${getMoodText(dream.mood)}"

            // Превью содержимого (первые 100 символов)
            dreamPreview.text = if (dream.content.isNotEmpty()) {
                if (dream.content.length > 100) {
                    "${dream.content.take(100)}..."
                } else {
                    dream.content
                }
            } else {
                "Нет описания"
            }

            // Отображаем количество символов
            symbolsCount.text = "${dream.symbols.size} символов"

            // Отображаем теги
            setupTags(dream.tags)

            itemView.setOnClickListener {
                onDreamClick(dream)
            }
        }

        private fun setupTags(tags: List<String>) {
            tagsChipGroup.removeAllViews()

            tags.take(3).forEach { tag ->
                val chip = Chip(itemView.context).apply {
                    text = tag.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                        else it.toString()
                    }
                    isClickable = false
                    setChipBackgroundColorResource(R.color.primary_color)
                    setTextColor(itemView.context.resources.getColor(R.color.white, itemView.context.theme))
                    textSize = 12f
                }
                tagsChipGroup.addView(chip)
            }

            if (tags.size > 3) {
                val moreChip = Chip(itemView.context).apply {
                    text = "+${tags.size - 3}"
                    isClickable = false
                    setChipBackgroundColorResource(R.color.surface_color)
                    setTextColor(itemView.context.resources.getColor(R.color.secondary_text, itemView.context.theme))
                    textSize = 12f
                }
                tagsChipGroup.addView(moreChip)
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
    }
}