package com.lionido.dream_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * Помощник для работы с уведомлениями
 */
object NotificationHelper {
    
    private const val CHANNEL_ID = "dream_reminders"
    private const val CHANNEL_NAME = "Напоминания о записи снов"
    private const val CHANNEL_DESCRIPTION = "Уведомления с напоминанием записать свой сон"
    
    private const val NOTIFICATION_ID = 1001
    
    /**
     * Создает канал уведомлений (для Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Показывает уведомление с напоминанием записать сон
     */
    fun showDreamReminderNotification(context: Context) {
        createNotificationChannel(context)
        
        // Intent для открытия главной активности при нажатии на уведомление
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dream_notification)
            .setContentTitle("Время записать сон! 🌙")
            .setContentText("Помните ли вы, что вам снилось сегодня ночью?")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Запишите свой сон пока не забыли детали. Каждый сон может содержать важные послания!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(NOTIFICATION_ID, notification)
            } catch (e: SecurityException) {
                // Уведомления заблокированы пользователем
            }
        }
    }
    
    /**
     * Отменяет все активные уведомления
     */
    fun cancelAllNotifications(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            cancelAll()
        }
    }
    
    /**
     * Проверяет, разрешены ли уведомления
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}