package com.lionido.dream_app

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Диалог для выбора и управления тегами
 */
class TagSelectionDialog : DialogFragment() {
    
    interface TagSelectionListener {
        fun onTagsSelected(selectedTags: List<String>)
    }
    
    private var listener: TagSelectionListener? = null
    private var currentTags: List<String> = emptyList()
    private var dreamContent: String = ""
    
    private lateinit var chipGroup: ChipGroup
    private lateinit var addTagButton: Button
    private lateinit var suggestedChipGroup: ChipGroup
    private lateinit var suggestedLabel: TextView
    
    companion object {
        fun newInstance(
            currentTags: List<String>,
            dreamContent: String = "",
            listener: TagSelectionListener
        ): TagSelectionDialog {
            return TagSelectionDialog().apply {
                this.currentTags = currentTags
                this.dreamContent = dreamContent
                this.listener = listener
            }
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_tag_selection, null)
        
        initializeViews(view)
        setupTagChips()
        setupSuggestedTags()
        setupAddTagButton()
        
        return MaterialAlertDialogBuilder(context)
            .setTitle("Выберите теги")
            .setView(view)
            .setPositiveButton("Готово") { _, _ ->
                val selectedTags = getSelectedTags()
                listener?.onTagsSelected(selectedTags)
            }
            .setNegativeButton("Отмена", null)
            .create()
    }
    
    private fun initializeViews(view: View) {
        chipGroup = view.findViewById(R.id.chipGroup)
        addTagButton = view.findViewById(R.id.addTagButton)
        suggestedChipGroup = view.findViewById(R.id.suggestedChipGroup)
        suggestedLabel = view.findViewById(R.id.suggestedLabel)
    }
    
    private fun setupTagChips() {
        val context = requireContext()
        val allTags = TagManager.getAllTags(context)
        
        chipGroup.removeAllViews()
        
        allTags.forEach { tag ->
            val chip = createTagChip(tag, isSelected = tag in currentTags)
            chipGroup.addView(chip)
        }
    }
    
    private fun setupSuggestedTags() {
        if (dreamContent.isBlank()) {
            suggestedLabel.visibility = View.GONE
            suggestedChipGroup.visibility = View.GONE
            return
        }
        
        val suggestedTags = TagManager.suggestTags(dreamContent)
        if (suggestedTags.isEmpty()) {
            suggestedLabel.visibility = View.GONE
            suggestedChipGroup.visibility = View.GONE
            return
        }
        
        suggestedChipGroup.removeAllViews()
        
        suggestedTags.forEach { tag ->
            val chip = createSuggestedTagChip(tag)
            suggestedChipGroup.addView(chip)
        }
    }
    
    private fun createTagChip(tag: String, isSelected: Boolean = false): Chip {
        val context = requireContext()
        val chip = Chip(context)
        
        chip.text = tag
        chip.isCheckable = true
        chip.isChecked = isSelected
        chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
            Color.parseColor(TagManager.getTagColor(context, tag))
        )
        chip.setTextColor(Color.WHITE)
        
        return chip
    }
    
    private fun createSuggestedTagChip(tag: String): Chip {
        val context = requireContext()
        val chip = Chip(context)
        
        chip.text = tag
        chip.isClickable = true
        chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
            Color.parseColor(TagManager.getTagColor(context, tag))
        )
        chip.setTextColor(Color.WHITE)
        
        chip.setOnClickListener {
            // Добавляем тег в основную группу если его там нет
            val existingTag = findChipByText(chipGroup, tag)
            if (existingTag != null) {
                existingTag.isChecked = true
            } else {
                val newChip = createTagChip(tag, isSelected = true)
                chipGroup.addView(newChip)
            }
            
            // Удаляем из предложенных
            suggestedChipGroup.removeView(chip)
            
            // Скрываем секцию если предложений больше нет
            if (suggestedChipGroup.childCount == 0) {
                suggestedLabel.visibility = View.GONE
                suggestedChipGroup.visibility = View.GONE
            }
        }
        
        return chip
    }
    
    private fun findChipByText(chipGroup: ChipGroup, text: String): Chip? {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.text == text) {
                return chip
            }
        }
        return null
    }
    
    private fun setupAddTagButton() {
        addTagButton.setOnClickListener {
            showAddTagDialog()
        }
    }
    
    private fun showAddTagDialog() {
        val context = requireContext()
        val input = TextInputEditText(context)
        val inputLayout = TextInputLayout(context).apply {
            hint = "Название тега"
            addView(input)
        }
        
        MaterialAlertDialogBuilder(context)
            .setTitle("Добавить новый тег")
            .setView(inputLayout)
            .setPositiveButton("Добавить") { _, _ ->
                val tagName = input.text.toString().trim()
                if (tagName.isNotEmpty()) {
                    if (TagManager.addCustomTag(context, tagName)) {
                        val chip = createTagChip(tagName, isSelected = true)
                        chipGroup.addView(chip)
                        Toast.makeText(context, "Тег добавлен", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Такой тег уже существует", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun getSelectedTags(): List<String> {
        val selectedTags = mutableListOf<String>()
        
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.isChecked == true) {
                selectedTags.add(chip.text.toString())
            }
        }
        
        return selectedTags
    }
}