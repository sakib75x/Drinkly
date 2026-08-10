package com.drinkly.app.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drinkly.app.R
import com.drinkly.app.data.PreferencesManager
import com.drinkly.app.worker.WaterReminderWorker
import kotlinx.coroutines.launch

/**
 * The single screen of Drinkly:
 * - a progress ring with today's glasses vs the goal
 * - buttons to add or remove a glass
 * - a slider to change the daily goal
 * - a switch to turn water reminders on or off
 */
@Composable
fun HomeScreenModern() {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val scope = rememberCoroutineScope()

    val glasses by preferencesManager.glassesCount
        .collectAsStateWithLifecycle(initialValue = 0)
    val goal by preferencesManager.dailyGoal
        .collectAsStateWithLifecycle(initialValue = PreferencesManager.DEFAULT_GOAL)
    val remindersEnabled by preferencesManager.remindersEnabled
        .collectAsStateWithLifecycle(initialValue = true)

    // Roll the counter over to zero if it belongs to a previous day.
    LaunchedEffect(Unit) {
        preferencesManager.resetIfNewDay()
    }

    val progress = if (goal > 0) {
        (glasses.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Header()

        Spacer(modifier = Modifier.height(20.dp))

        ProgressCard(
            glasses = glasses,
            goal = goal,
            progress = progress
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(motivationalMessage(glasses, goal)),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { scope.launch { preferencesManager.removeGlass() } }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remove_glass)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.remove_glass))
            }

            FilledTonalButton(
                onClick = { scope.launch { preferencesManager.addGlass() } }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_glass)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.add_glass))
            }
        }

        TextButton(
            onClick = { scope.launch { preferencesManager.resetToday() } }
        ) {
            Text(text = stringResource(R.string.reset_today))
        }

        Spacer(modifier = Modifier.height(8.dp))

        GoalCard(
            goal = goal,
            onGoalChange = { newGoal ->
                scope.launch { preferencesManager.setGoal(newGoal) }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReminderCard(
            enabled = remindersEnabled,
            onToggle = { enabled ->
                scope.launch {
                    preferencesManager.setRemindersEnabled(enabled)
                    if (enabled) {
                        WaterReminderWorker.schedule(context)
                    } else {
                        WaterReminderWorker.cancel(context)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProgressCard(
    glasses: Int,
    goal: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            ProgressRing(
                glasses = glasses,
                goal = goal,
                progress = progress,
                modifier = Modifier.size(240.dp)
            )
        }
    }
}

@Composable
private fun ProgressRing(
    glasses: Int,
    goal: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val dropTint = MaterialTheme.colorScheme.primary
    val countColor = MaterialTheme.colorScheme.onSurface
    val goalColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.09f
            val diameter = size.minDimension - strokeWidth * 2
            val arcTopLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            // Full background ring.
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress ring on top.
            if (progress > 0f) {
                drawArc(
                    color = progressColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_drop),
                contentDescription = stringResource(R.string.water_drop),
                modifier = Modifier.size(56.dp),
                colorFilter = ColorFilter.tint(dropTint)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = glasses.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = countColor
            )

            Text(
                text = stringResource(R.string.of_goal, goal),
                style = MaterialTheme.typography.titleMedium,
                color = goalColor
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: Int,
    onGoalChange: (Int) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(goal.toFloat()) }

    // Keep the slider in sync if the goal changes somewhere else.
    LaunchedEffect(goal) {
        sliderValue = goal.toFloat()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.daily_goal),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.goal_glasses, goal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = sliderValue,
                onValueChange = { newValue -> sliderValue = newValue },
                onValueChangeFinished = { onGoalChange(sliderValue.toInt()) },
                valueRange = PreferencesManager.MIN_GOAL.toFloat()..
                    PreferencesManager.MAX_GOAL.toFloat(),
                steps = PreferencesManager.MAX_GOAL - PreferencesManager.MIN_GOAL - 1
            )

            Text(
                text = stringResource(R.string.goal_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReminderCard(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.reminders_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        if (enabled) R.string.reminders_on else R.string.reminders_off
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

/** Picks an encouraging message based on how far along the user is. */
@StringRes
private fun motivationalMessage(glasses: Int, goal: Int): Int {
    val fraction = if (goal > 0) glasses.toFloat() / goal.toFloat() else 0f
    return when {
        fraction >= 1f -> R.string.msg_goal_reached
        fraction >= 0.75f -> R.string.msg_almost_there
        fraction >= 0.5f -> R.string.msg_halfway
        fraction >= 0.25f -> R.string.msg_good_start
        glasses > 0 -> R.string.msg_every_glass
        else -> R.string.msg_first_glass
    }
}
