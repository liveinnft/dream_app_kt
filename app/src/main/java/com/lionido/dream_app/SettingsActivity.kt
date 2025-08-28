package com.lionido.dream_app

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var themeCard: MaterialCardView
    private lateinit var themeTitle: TextView
    private lateinit var themeSubtitle: TextView
    private lateinit var apiKeyCard: MaterialCardView
    private lateinit var apiKeyTitle: TextView
    private lateinit var apiKeySubtitle: TextView
    private lateinit var reminderCard: MaterialCardView
    private lateinit var reminderSwitch: SwitchMaterial
    private lateinit var reminderTimeCard: MaterialCardView
    private lateinit var reminderTimeSubtitle: TextView
    private lateinit var aboutCard: MaterialCardView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        setupToolbar()
        initializeViews()
        setupThemeSelector()
        setupApiKeySection()
        setupReminderSection()
        setupAboutSection()
        
        updateThemeDisplay()
        updateApiKeyDisplay()
        updateReminderDisplay()
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Настройки"
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun initializeViews() {
        themeCard = findViewById(R.id.themeCard)
        themeTitle = findViewById(R.id.themeTitle)
        themeSubtitle = findViewById(R.id.themeSubtitle)
        
        apiKeyCard = findViewById(R.id.apiKeyCard)
        apiKeyTitle = findViewById(R.id.apiKeyTitle)
        apiKeySubtitle = findViewById(R.id.apiKeySubtitle)
        
        reminderCard = findViewById(R.id.reminderCard)
        reminderSwitch = findViewById(R.id.reminderSwitch)
        reminderTimeCard = findViewById(R.id.reminderTimeCard)
        reminderTimeSubtitle = findViewById(R.id.reminderTimeSubtitle)
        
        aboutCard = findViewById(R.id.aboutCard)
    }
    
    private fun setupThemeSelector() {
        themeCard.setOnClickListener {
            showThemeDialog()
        }
    }
    
    private fun showThemeDialog() {
        val themes = arrayOf("Светлая", "Темная", "Системная")
        val currentTheme = ThemeManager.getCurrentTheme(this)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Выберите тему")
            .setSingleChoiceItems(themes, currentTheme) { dialog, which ->
                ThemeManager.setTheme(this, which)
                updateThemeDisplay()
                dialog.dismiss()
                
                // Показываем уведомление о применении темы
                Toast.makeText(this, "Тема изменена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun setupApiKeySection() {
        apiKeyCard.setOnClickListener {
            showApiKeyDialog()
        }
    }
    
    private fun showApiKeyDialog() {
        val currentKey = getString(R.string.openrouter_api_key)
        val isDefault = currentKey == "YOUR_API_KEY_HERE"
        
        MaterialAlertDialogBuilder(this)
            .setTitle("API ключ OpenRouter")
            .setMessage(if (isDefault) {
                "API ключ не настроен. Для получения расширенных интерпретаций снов необходимо добавить ключ OpenRouter API."
            } else {
                "API ключ настроен. Расширенные интерпретации снов доступны."
            })
            .setPositiveButton("Понятно", null)
            .setNeutralButton("Получить ключ") { _, _ ->
                // Здесь можно открыть браузер с сайтом OpenRouter
                Toast.makeText(this, "Посетите openrouter.ai для получения ключа", Toast.LENGTH_LONG).show()
            }
            .show()
    }
    
    private fun setupAboutSection() {
        aboutCard.setOnClickListener {
            showAboutDialog()
        }
    }
    
    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("О приложении")
            .setMessage("Сонный переводчик v1.0\n\n" +
                    "Приложение для анализа и интерпретации снов на базе AI.\n\n" +
                    "Особенности:\n" +
                    "• Голосовая запись снов\n" +
                    "• Анализ символов и эмоций\n" +
                    "• AI интерпретация\n" +
                    "• Статистика снов\n" +
                    "• Полная приватность данных\n\n" +
                    "Создано с ❤️ для тех, кто ценит свои сны")
            .setPositiveButton("Закрыть", null)
            .show()
    }
    
    private fun updateThemeDisplay() {
        val currentTheme = ThemeManager.getCurrentTheme(this)
        val themeName = ThemeManager.getThemeName(this, currentTheme)
        themeSubtitle.text = "Текущая тема: $themeName"
    }
    
    private fun updateApiKeyDisplay() {
        val currentKey = getString(R.string.openrouter_api_key)
        val isConfigured = currentKey != "YOUR_API_KEY_HERE"
        
        apiKeySubtitle.text = if (isConfigured) {
            "API ключ настроен"
        } else {
            "API ключ не настроен"
        }
    }
    
    private fun setupReminderSection() {
        // Настройка переключателя напоминаний
        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            ReminderManager.setReminderEnabled(this, isChecked)
            updateReminderTimeVisibility()
            
            if (isChecked) {
                // Создаем канал уведомлений при включении напоминаний
                NotificationHelper.createNotificationChannel(this)
                Toast.makeText(this, "Напоминания включены", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Напоминания выключены", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Настройка времени напоминания
        reminderTimeCard.setOnClickListener {
            showTimePickerDialog()
        }
    }
    
    private fun showTimePickerDialog() {
        val (currentHour, currentMinute) = ReminderManager.getReminderTime(this)
        
        TimePickerDialog(this, { _, hour, minute ->
            ReminderManager.setReminderTime(this, hour, minute)
            updateReminderDisplay()
            Toast.makeText(this, "Время напоминания изменено", Toast.LENGTH_SHORT).show()
        }, currentHour, currentMinute, true).show()
    }
    
    private fun updateReminderDisplay() {
        val isEnabled = ReminderManager.isReminderEnabled(this)
        val (hour, minute) = ReminderManager.getReminderTime(this)
        
        reminderSwitch.isChecked = isEnabled
        reminderTimeSubtitle.text = "Время: ${ReminderManager.formatTime(hour, minute)}"
        
        updateReminderTimeVisibility()
    }
    
    private fun updateReminderTimeVisibility() {
        reminderTimeCard.visibility = if (reminderSwitch.isChecked) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
    }
}