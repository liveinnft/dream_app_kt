package com.lionido.dream_app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Менеджер тем приложения
 */
object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    
    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Устанавливает тему приложения
     */
    fun setTheme(context: Context, themeMode: Int) {
        getPrefs(context).edit().putInt(KEY_THEME_MODE, themeMode).apply()
        applyTheme(themeMode)
    }
    
    /**
     * Получает текущую тему
     */
    fun getCurrentTheme(context: Context): Int {
        return getPrefs(context).getInt(KEY_THEME_MODE, THEME_SYSTEM)
    }
    
    /**
     * Применяет тему
     */
    fun applyTheme(themeMode: Int) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    /**
     * Инициализирует тему при запуске приложения
     */
    fun initTheme(context: Context) {
        val currentTheme = getCurrentTheme(context)
        applyTheme(currentTheme)
    }
    
    /**
     * Получает название темы для отображения
     */
    fun getThemeName(@Suppress("UNUSED_PARAMETER") context: Context, themeMode: Int): String {
        return when (themeMode) {
            THEME_LIGHT -> "Светлая"
            THEME_DARK -> "Темная"
            THEME_SYSTEM -> "Системная"
            else -> "Неизвестная"
        }
    }
}