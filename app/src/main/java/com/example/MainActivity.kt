package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AlarmRingingDialog
import com.example.ui.components.CelebrationOverlay
import com.example.ui.components.ShareProgressDialog
import com.example.ui.components.StudyFlowTopBar
import com.example.ui.screens.ClockAlarmScreen
import com.example.ui.screens.HabitsRoutineScreen
import com.example.ui.screens.ProgressHubScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.TimerStopwatchScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.StudyFlowTheme
import com.example.ui.viewmodel.StudyFlowViewModel

enum class NavigationTab(val title: String, val icon: ImageVector, val tag: String) {
    TASKS("Tasks", Icons.Default.CheckCircle, "nav_tasks"),
    TIMER("Focus", Icons.Default.Timer, "nav_timer"),
    HABITS("Routines", Icons.Default.LocalFireDepartment, "nav_habits"),
    ALARMS("Clock", Icons.Default.Alarm, "nav_alarms"),
    PROGRESS("Hub", Icons.Default.Insights, "nav_progress")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyFlowTheme(darkTheme = true) {
                StudyFlowApp()
            }
        }
    }
}

@Composable
fun StudyFlowApp(viewModel: StudyFlowViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showShareModal by remember { mutableStateOf(false) }

    // Database / ViewModel state
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    val habits by viewModel.habits.collectAsStateWithLifecycle()
    val todayWaterLogs by viewModel.todayWaterLogs.collectAsStateWithLifecycle()
    val allWaterLogs by viewModel.allWaterLogs.collectAsStateWithLifecycle()
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val buddies by viewModel.buddies.collectAsStateWithLifecycle()

    // Clock, timer, celebration, ringing alarm
    val currentTimeMillis by viewModel.currentTimeMillis.collectAsStateWithLifecycle()
    val celebrationEvent by viewModel.celebrationEvent.collectAsStateWithLifecycle()
    val ringingAlarm by viewModel.ringingAlarm.collectAsStateWithLifecycle()

    // Timer states
    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val totalTimerSeconds by viewModel.totalTimerSeconds.collectAsStateWithLifecycle()
    val remainingTimerSeconds by viewModel.remainingTimerSeconds.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val selectedTimerSubject by viewModel.selectedTimerSubject.collectAsStateWithLifecycle()
    val completedSessionsCount by viewModel.completedSessionsCount.collectAsStateWithLifecycle()

    // Stopwatch states
    val stopwatchElapsedMs by viewModel.stopwatchElapsedMs.collectAsStateWithLifecycle()
    val isStopwatchRunning by viewModel.isStopwatchRunning.collectAsStateWithLifecycle()
    val laps by viewModel.laps.collectAsStateWithLifecycle()

    val totalWaterMlToday = todayWaterLogs.sumOf { it.amountMl }

    // BackHandler to return to Tasks screen if in secondary tab
    BackHandler(enabled = selectedTab != 0) {
        selectedTab = 0
    }

    Scaffold(
        topBar = {
            StudyFlowTopBar(
                streakDays = profile.dailyStreak,
                totalXp = profile.totalXp,
                todayWaterMl = totalWaterMlToday,
                onWaterClick = { selectedTab = 2 }, // Navigate to Habits / Water tab
                onShareClick = { showShareModal = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 6.dp
            ) {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DarkBackground,
                            selectedTextColor = FocusCyan,
                            indicatorColor = FocusCyan,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetScreenIndex ->
                when (targetScreenIndex) {
                    0 -> TasksScreen(
                        tasks = tasks,
                        onToggleTask = { viewModel.toggleTaskCompleted(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onAddTask = { title, desc, cat, prio, due ->
                            viewModel.addTask(title, desc, cat, prio, due)
                        }
                    )
                    1 -> TimerStopwatchScreen(
                        timerMode = timerMode,
                        totalTimerSeconds = totalTimerSeconds,
                        remainingTimerSeconds = remainingTimerSeconds,
                        isTimerRunning = isTimerRunning,
                        selectedSubject = selectedTimerSubject,
                        completedSessionsCount = completedSessionsCount,
                        onSetTimerPreset = { viewModel.setTimerPreset(it) },
                        onSetCustomMinutes = { viewModel.setCustomTimerMinutes(it) },
                        onSelectSubject = { viewModel.setSelectedSubject(it) },
                        onStartTimer = { viewModel.startTimer() },
                        onPauseTimer = { viewModel.pauseTimer() },
                        onResetTimer = { viewModel.resetTimer() },
                        stopwatchElapsedMs = stopwatchElapsedMs,
                        isStopwatchRunning = isStopwatchRunning,
                        laps = laps,
                        onStartStopwatch = { viewModel.startStopwatch() },
                        onPauseStopwatch = { viewModel.pauseStopwatch() },
                        onResetStopwatch = { viewModel.resetStopwatch() },
                        onRecordLap = { viewModel.recordLap() },
                        onSaveStopwatchToTracker = { subj, notes ->
                            viewModel.saveStopwatchAsStudySession(subj, notes)
                        }
                    )
                    2 -> HabitsRoutineScreen(
                        habits = habits,
                        todayWaterLogs = todayWaterLogs,
                        dailyWaterGoalMl = profile.dailyWaterGoalMl,
                        onToggleHabit = { viewModel.toggleHabit(it) },
                        onDeleteHabit = { viewModel.deleteHabit(it) },
                        onAddHabit = { title, cat, icon ->
                            viewModel.addHabit(title, cat, icon)
                        },
                        onAddWater = { viewModel.logWater(it) },
                        onSetWaterGoal = { viewModel.setWaterGoal(it) },
                        onTriggerWaterAlarm = { viewModel.triggerWaterAlarmTest() }
                    )
                    3 -> ClockAlarmScreen(
                        currentTimeMillis = currentTimeMillis,
                        quote = viewModel.currentQuote,
                        alarms = alarms,
                        onToggleAlarm = { viewModel.toggleAlarm(it) },
                        onDeleteAlarm = { viewModel.deleteAlarm(it) },
                        onTriggerAlarmTest = { viewModel.triggerAlarmRinging(it) },
                        onAddAlarm = { h, m, label, repeat, isWater ->
                            viewModel.addAlarm(h, m, label, repeat, isWater)
                        }
                    )
                    4 -> ProgressHubScreen(
                        tasks = tasks,
                        sessions = sessions,
                        habits = habits,
                        waterLogs = allWaterLogs,
                        dailyWaterGoalMl = profile.dailyWaterGoalMl,
                        profile = profile,
                        badges = viewModel.getBadges(),
                        rewards = rewards,
                        buddies = buddies,
                        onRedeemReward = { viewModel.redeemReward(it) },
                        onAddReward = { title, cost ->
                            viewModel.addReward(title, cost)
                        },
                        onSendCheer = { buddy, cheer ->
                            viewModel.sendCheerToBuddy(buddy, cheer)
                        },
                        onAddBuddy = { name, subj ->
                            viewModel.addStudyBuddy(name, subj)
                        }
                    )
                }
            }

            // Top Floating Celebration Notification
            CelebrationOverlay(
                message = celebrationEvent,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }

    // Active Ringing Alarm simulation dialog
    ringingAlarm?.let { alarm ->
        AlarmRingingDialog(
            alarm = alarm,
            onDismiss = { viewModel.dismissRingingAlarm() },
            onSnooze = { viewModel.snoozeRingingAlarm() }
        )
    }

    // Share habit progress card dialog
    if (showShareModal) {
        ShareProgressDialog(
            streakDays = profile.dailyStreak,
            studyMinutesToday = sessions.sumOf { it.durationMinutes },
            tasksCompletedToday = tasks.count { it.isCompleted },
            waterMlToday = totalWaterMlToday,
            level = profile.level,
            onDismiss = { showShareModal = false }
        )
    }
}
