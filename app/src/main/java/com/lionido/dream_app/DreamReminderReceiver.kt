package com.lionido.dream_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Получатель напоминаний о записи снов
 */
class DreamReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // Показываем уведомление
            NotificationHelper.showDreamReminderNotification(it)
            
            // Планируем следующее напоминание (для Android 12+)
            if (ReminderManager.isReminderEnabled(it)) {
                ReminderManager.scheduleReminder(it)
            }
        }
    }
}