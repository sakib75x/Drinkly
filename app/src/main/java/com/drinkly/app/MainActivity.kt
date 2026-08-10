package com.drinkly.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.drinkly.app.data.PreferencesManager
import com.drinkly.app.ui.screens.HomeScreenModern
import com.drinkly.app.ui.theme.ModernTheme
import com.drinkly.app.worker.WaterReminderWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                WaterReminderWorker.schedule(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupNotifications()
        setContent {
            ModernTheme {
                HomeScreenModern()
            }
        }
    }

    private fun setupNotifications() {
        requestNotificationPermissionIfNeeded()

        // Re-schedule the reminder worker on every app start. This is safe:
        // WorkManager replaces the existing periodic work instead of
        // creating duplicates, and the worker only runs while the
        // "reminders enabled" preference is turned on.
        val preferencesManager = PreferencesManager(this)
        lifecycleScope.launch {
            val remindersEnabled = preferencesManager.remindersEnabled.first()
            if (remindersEnabled) {
                WaterReminderWorker.schedule(this@MainActivity)
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
