package com.drinkly.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// A single DataStore instance shared by the whole app.
private val Context.dataStore by preferencesDataStore(name = "drinkly_prefs")

/**
 * Stores and exposes everything Drinkly remembers:
 * - how many glasses were drunk today
 * - the daily goal
 * - whether water reminders are turned on
 * - the last day the counter was reset
 */
class PreferencesManager(private val context: Context) {

    private object Keys {
        val glassesCount = intPreferencesKey("glasses_count")
        val dailyGoal = intPreferencesKey("daily_goal")
        val remindersEnabled = booleanPreferencesKey("reminders_enabled")
        val lastResetDate = stringPreferencesKey("last_reset_date")
    }

    companion object {
        const val DEFAULT_GOAL = 8
        const val MIN_GOAL = 1
        const val MAX_GOAL = 16
    }

    val glassesCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.glassesCount] ?: 0
    }

    val dailyGoal: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.dailyGoal] ?: DEFAULT_GOAL
    }

    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.remindersEnabled] ?: true
    }

    /** Adds one glass, capped at a sane maximum. */
    suspend fun addGlass() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.glassesCount] ?: 0
            prefs[Keys.glassesCount] = (current + 1).coerceAtMost(99)
        }
    }

    /** Removes one glass, never going below zero. */
    suspend fun removeGlass() {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.glassesCount] ?: 0
            prefs[Keys.glassesCount] = (current - 1).coerceAtLeast(0)
        }
    }

    /** Sets the counter back to zero and marks today as the reset day. */
    suspend fun resetToday() {
        context.dataStore.edit { prefs ->
            prefs[Keys.glassesCount] = 0
            prefs[Keys.lastResetDate] = today()
        }
    }

    suspend fun setGoal(goal: Int) {
        val safeGoal = goal.coerceIn(MIN_GOAL, MAX_GOAL)
        context.dataStore.edit { prefs ->
            prefs[Keys.dailyGoal] = safeGoal
        }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.remindersEnabled] = enabled
        }
    }

    /**
     * If the stored reset date is not today, the counter is reset.
     * This is called when the app opens and when a reminder fires, so the
     * count always starts fresh for a new day.
     */
    suspend fun resetIfNewDay() {
        val todayKey = today()
        context.dataStore.edit { prefs ->
            if (prefs[Keys.lastResetDate] != todayKey) {
                prefs[Keys.lastResetDate] = todayKey
                prefs[Keys.glassesCount] = 0
            }
        }
    }

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
