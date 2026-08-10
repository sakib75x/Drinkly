package com.drinkly.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.drinkly.app.R
import com.drinkly.app.data.PreferencesManager
import com.drinkly.app.notification.NotificationHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

/**
 * Periodic background task that posts a "drink water" notification.
 *
 * It runs every few hours (see REPEAT_INTERVAL_HOURS), only between
 * ACTIVE_START_HOUR and ACTIVE_END_HOUR, and only while the reminders
 * preference is enabled.
 */
class WaterReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val preferencesManager = PreferencesManager(applicationContext)

        val remindersEnabled = preferencesManager.remindersEnabled.first()
        if (!remindersEnabled) {
            return Result.success()
        }

        if (!isWithinActiveHours()) {
            return Result.success()
        }

        preferencesManager.resetIfNewDay()

        val glasses = preferencesManager.glassesCount.first()
        val goal = preferencesManager.dailyGoal.first()
        val remaining = (goal - glasses).coerceAtLeast(0)

        val title = applicationContext.getString(R.string.notif_title)
        val message = if (remaining > 0) {
            applicationContext.getString(R.string.notif_message_remaining, remaining)
        } else {
            applicationContext.getString(R.string.notif_message_done)
        }

        NotificationHelper.showReminder(applicationContext, title, message)

        return Result.success()
    }

    private fun isWithinActiveHours(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour in ACTIVE_START_HOUR..ACTIVE_END_HOUR
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "drinkly_water_reminder"
        private const val REPEAT_INTERVAL_HOURS = 3L
        private const val INITIAL_DELAY_HOURS = 1L
        private const val ACTIVE_START_HOUR = 8
        private const val ACTIVE_END_HOUR = 21

        /**
         * Schedules the periodic worker. Calling this again simply updates
         * the existing schedule, so it is safe to call on every app start.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                REPEAT_INTERVAL_HOURS,
                TimeUnit.HOURS
            )
                .setInitialDelay(INITIAL_DELAY_HOURS, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
        }
    }
}
