package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.AlarmEntity
import com.example.data.model.BadgeItem
import com.example.data.model.HabitEntity
import com.example.data.model.RewardEntity
import com.example.data.model.StudyBuddyEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WaterLogEntity
import com.example.data.repository.StudyFlowRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StopwatchLap(
    val lapIndex: Int,
    val lapTimeMs: Long,
    val totalTimeMs: Long
)

enum class PomodoroMode(val title: String, val minutes: Int, val isBreak: Boolean) {
    POMODORO_25("Focus 25", 25, false),
    SHORT_BREAK("Break 5", 5, true),
    DEEP_FOCUS("Deep 50", 50, false),
    LONG_BREAK("Break 15", 15, true),
    SPRINT_15("Sprint 15", 15, false)
}

class StudyFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StudyFlowRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudyFlowRepository(db.appDao())
    }

    private val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Database flows
    val tasks: StateFlow<List<TaskEntity>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySessionEntity>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.habits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayWaterLogs: StateFlow<List<WaterLogEntity>> = repository.getTodayWaterLogs(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWaterLogs: StateFlow<List<WaterLogEntity>> = repository.allWaterLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alarms: StateFlow<List<AlarmEntity>> = repository.alarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardEntity>> = repository.rewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<UserProfileEntity> = repository.profile
        .combine(MutableStateFlow(UserProfileEntity())) { user, fallback ->
            user ?: fallback
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfileEntity())

    val buddies: StateFlow<List<StudyBuddyEntity>> = repository.buddies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- CELEBRATION & TOAST STATE ---
    private val _celebrationEvent = MutableStateFlow<String?>(null)
    val celebrationEvent: StateFlow<String?> = _celebrationEvent.asStateFlow()

    fun dismissCelebration() {
        _celebrationEvent.value = null
    }

    private fun triggerCelebration(message: String) {
        _celebrationEvent.value = message
        vibratePhone(120)
    }

    // --- LIVE CLOCK STATE ---
    private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

    private val studentQuotes = listOf(
        "\"Focus is a muscle. The more you use it, the stronger it gets.\" — Cal Newport",
        "\"Small daily improvements over time lead to stunning results.\" — Robin Sharma",
        "\"You don't have to be extreme, just consistent.\" — James Clear",
        "\"Success is the sum of small efforts repeated day in and day out.\" — Robert Collier",
        "\"Discipline is choosing between what you want now and what you want most.\" — Abraham Lincoln",
        "\"The secret of getting ahead is getting started.\" — Mark Twain"
    )

    val currentQuote: String = studentQuotes[(System.currentTimeMillis() / 86400000 % studentQuotes.size).toInt()]

    init {
        viewModelScope.launch {
            while (true) {
                _currentTimeMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    // --- POMODORO / TIMER STATE ---
    private val _timerMode = MutableStateFlow(PomodoroMode.POMODORO_25)
    val timerMode: StateFlow<PomodoroMode> = _timerMode.asStateFlow()

    private val _totalTimerSeconds = MutableStateFlow(25 * 60)
    val totalTimerSeconds: StateFlow<Int> = _totalTimerSeconds.asStateFlow()

    private val _remainingTimerSeconds = MutableStateFlow(25 * 60)
    val remainingTimerSeconds: StateFlow<Int> = _remainingTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedTimerSubject = MutableStateFlow("Mathematics")
    val selectedTimerSubject: StateFlow<String> = _selectedTimerSubject.asStateFlow()

    private val _completedSessionsCount = MutableStateFlow(3)
    val completedSessionsCount: StateFlow<Int> = _completedSessionsCount.asStateFlow()

    private var timerJob: Job? = null

    fun setTimerPreset(mode: PomodoroMode) {
        pauseTimer()
        _timerMode.value = mode
        _totalTimerSeconds.value = mode.minutes * 60
        _remainingTimerSeconds.value = mode.minutes * 60
    }

    fun setCustomTimerMinutes(minutes: Int) {
        pauseTimer()
        val clamped = minutes.coerceIn(1, 180)
        _totalTimerSeconds.value = clamped * 60
        _remainingTimerSeconds.value = clamped * 60
    }

    fun setSelectedSubject(subject: String) {
        _selectedTimerSubject.value = subject
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_remainingTimerSeconds.value > 0 && _isTimerRunning.value) {
                delay(1000)
                _remainingTimerSeconds.value -= 1
            }
            if (_remainingTimerSeconds.value <= 0) {
                onTimerCompleted()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _remainingTimerSeconds.value = _totalTimerSeconds.value
    }

    private fun onTimerCompleted() {
        _isTimerRunning.value = false
        val durationMins = _totalTimerSeconds.value / 60
        val isBreak = _timerMode.value.isBreak

        vibratePhone(500)

        if (!isBreak) {
            val xpEarned = durationMins.coerceAtLeast(10)
            _completedSessionsCount.value += 1

            viewModelScope.launch {
                repository.insertSession(
                    StudySessionEntity(
                        subject = _selectedTimerSubject.value,
                        durationMinutes = durationMins,
                        timestamp = System.currentTimeMillis(),
                        notes = "Completed ${_timerMode.value.title} focus session",
                        xpEarned = xpEarned
                    )
                )
                addXp(xpEarned)
                triggerCelebration("🎯 Focus Session Complete! +$xpEarned XP for ${_selectedTimerSubject.value}!")
            }
        } else {
            triggerCelebration("☕ Break finished! Ready to dive back into deep study?")
        }
    }

    // --- STOPWATCH STATE ---
    private val _stopwatchElapsedMs = MutableStateFlow(0L)
    val stopwatchElapsedMs: StateFlow<Long> = _stopwatchElapsedMs.asStateFlow()

    private val _isStopwatchRunning = MutableStateFlow(false)
    val isStopwatchRunning: StateFlow<Boolean> = _isStopwatchRunning.asStateFlow()

    private val _laps = MutableStateFlow<List<StopwatchLap>>(emptyList())
    val laps: StateFlow<List<StopwatchLap>> = _laps.asStateFlow()

    private var stopwatchJob: Job? = null
    private var stopwatchStartTime = 0L

    fun startStopwatch() {
        if (_isStopwatchRunning.value) return
        _isStopwatchRunning.value = true
        stopwatchStartTime = System.currentTimeMillis() - _stopwatchElapsedMs.value
        stopwatchJob = viewModelScope.launch {
            while (_isStopwatchRunning.value) {
                _stopwatchElapsedMs.value = System.currentTimeMillis() - stopwatchStartTime
                delay(30)
            }
        }
    }

    fun pauseStopwatch() {
        _isStopwatchRunning.value = false
        stopwatchJob?.cancel()
    }

    fun resetStopwatch() {
        pauseStopwatch()
        _stopwatchElapsedMs.value = 0L
        _laps.value = emptyList()
    }

    fun recordLap() {
        if (!_isStopwatchRunning.value && _stopwatchElapsedMs.value == 0L) return
        val currentTotal = _stopwatchElapsedMs.value
        val previousTotal = _laps.value.firstOrNull()?.totalTimeMs ?: 0L
        val lapDelta = currentTotal - previousTotal
        val newLap = StopwatchLap(
            lapIndex = _laps.value.size + 1,
            lapTimeMs = lapDelta,
            totalTimeMs = currentTotal
        )
        _laps.value = listOf(newLap) + _laps.value
        vibratePhone(40)
    }

    fun saveStopwatchAsStudySession(subject: String, notes: String = "") {
        val minutes = (_stopwatchElapsedMs.value / 60000L).toInt()
        if (minutes <= 0) {
            triggerCelebration("Stopwatch session under 1 minute. Keep studying to log!")
            return
        }
        val xp = (minutes * 1.2).toInt().coerceAtLeast(10)
        viewModelScope.launch {
            repository.insertSession(
                StudySessionEntity(
                    subject = subject,
                    durationMinutes = minutes,
                    timestamp = System.currentTimeMillis(),
                    notes = notes.ifEmpty { "Stopwatch recorded session" },
                    xpEarned = xp
                )
            )
            addXp(xp)
            resetStopwatch()
            triggerCelebration("📚 Saved $minutes min study session! +$xp XP awarded!")
        }
    }

    // --- ALARMS STATE & SIMULATION ---
    private val _ringingAlarm = MutableStateFlow<AlarmEntity?>(null)
    val ringingAlarm: StateFlow<AlarmEntity?> = _ringingAlarm.asStateFlow()

    fun triggerAlarmRinging(alarm: AlarmEntity) {
        _ringingAlarm.value = alarm
        vibratePhone(1000)
    }

    fun dismissRingingAlarm() {
        _ringingAlarm.value = null
    }

    fun snoozeRingingAlarm() {
        _ringingAlarm.value?.let { alarm ->
            _ringingAlarm.value = null
            triggerCelebration("⏰ Snoozed '${alarm.label}' for 5 minutes!")
        }
    }

    fun addAlarm(hour: Int, minute: Int, label: String, repeatDays: String, isWaterAlarm: Boolean = false) {
        viewModelScope.launch {
            repository.insertAlarm(
                AlarmEntity(
                    hour = hour,
                    minute = minute,
                    label = label,
                    isEnabled = true,
                    repeatDays = repeatDays,
                    isWaterAlarm = isWaterAlarm
                )
            )
            triggerCelebration("Alarm '$label' set for ${String.format(Locale.getDefault(), "%02d:%02d", hour, minute)}")
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled))
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
        }
    }

    // --- TASKS / TO-DO LIST ---
    fun addTask(title: String, description: String, category: String, priority: String, dueDateTime: String) {
        viewModelScope.launch {
            val xp = when (priority) {
                "HIGH" -> 30
                "MEDIUM" -> 20
                else -> 15
            }
            repository.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    category = category,
                    priority = priority,
                    dueDateTime = dueDateTime,
                    xpReward = xp
                )
            )
            triggerCelebration("Task added! Complete it to earn +$xp XP 🔥")
        }
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            repository.updateTask(
                task.copy(
                    isCompleted = newCompleted,
                    completedTimestamp = if (newCompleted) System.currentTimeMillis() else 0L
                )
            )
            if (newCompleted) {
                addXp(task.xpReward)
                triggerCelebration("🎉 Task done! +${task.xpReward} XP earned! Keep the momentum!")
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- HABITS & ROUTINE TRACKER ---
    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val isCompletedNow = !habit.isCompletedToday
            val newStreak = if (isCompletedNow) habit.streakDays + 1 else (habit.streakDays - 1).coerceAtLeast(0)
            repository.updateHabit(
                habit.copy(
                    isCompletedToday = isCompletedNow,
                    streakDays = newStreak,
                    lastCompletedDate = if (isCompletedNow) todayDateString else habit.lastCompletedDate
                )
            )
            if (isCompletedNow) {
                addXp(habit.xpReward)
                triggerCelebration("🔥 Routine checked! +${habit.xpReward} XP • $newStreak Day Streak!")
            }
        }
    }

    fun addHabit(title: String, category: String, iconName: String) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title,
                    category = category,
                    streakDays = 0,
                    isCompletedToday = false,
                    xpReward = 20,
                    iconName = iconName
                )
            )
            triggerCelebration("New routine habit added: '$title'")
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // --- DRINK WATER TRACKER & ALARM ---
    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.insertWaterLog(
                WaterLogEntity(
                    amountMl = amountMl,
                    timestamp = System.currentTimeMillis(),
                    dateString = todayDateString
                )
            )
            addXp(10)
            triggerCelebration("💧 +${amountMl}ml logged! +10 XP • Hydrated students learn faster!")
        }
    }

    fun setWaterGoal(goalMl: Int) {
        viewModelScope.launch {
            val current = _profileState()
            repository.updateProfile(current.copy(dailyWaterGoalMl = goalMl.coerceIn(1000, 5000)))
            triggerCelebration("Daily water goal set to ${goalMl}ml 💦")
        }
    }

    fun triggerWaterAlarmTest() {
        val waterAlarm = AlarmEntity(
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            minute = Calendar.getInstance().get(Calendar.MINUTE),
            label = "💧 Drink Water Alarm! Time to hydrate!",
            isEnabled = true,
            repeatDays = "Every day",
            isWaterAlarm = true
        )
        triggerAlarmRinging(waterAlarm)
    }

    // --- REWARDS & GAMIFICATION ---
    fun addReward(title: String, costPoints: Int, icon: String = "star") {
        viewModelScope.launch {
            repository.insertReward(
                RewardEntity(
                    title = title,
                    costPoints = costPoints,
                    icon = icon
                )
            )
            triggerCelebration("New self-reward added: '$title' ($costPoints pts)")
        }
    }

    fun redeemReward(reward: RewardEntity) {
        val curProfile = _profileState()
        if (curProfile.totalXp < reward.costPoints) {
            triggerCelebration("Not enough points! Need ${reward.costPoints - curProfile.totalXp} more XP ⚡")
            return
        }
        viewModelScope.launch {
            repository.updateReward(
                reward.copy(
                    isRedeemed = true,
                    redeemedDate = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date())
                )
            )
            repository.updateProfile(curProfile.copy(totalXp = curProfile.totalXp - reward.costPoints))
            triggerCelebration("🎁 Redeemed: ${reward.title}! Enjoy your well-earned reward!")
        }
    }

    // --- SOCIAL ACCOUNTABILITY & STUDY BUDDIES ---
    fun sendCheerToBuddy(buddy: StudyBuddyEntity, message: String) {
        viewModelScope.launch {
            val updated = buddy.copy(cheerCount = buddy.cheerCount + 1)
            repository.updateBuddy(updated)
            triggerCelebration("🚀 Sent cheer to ${buddy.name}: \"$message\"!")
        }
    }

    fun addStudyBuddy(name: String, subject: String) {
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
        viewModelScope.launch {
            repository.insertBuddy(
                StudyBuddyEntity(
                    name = name,
                    avatarInitials = if (initials.isNotEmpty()) initials else "SB",
                    statusMessage = "Ready to conquer $subject! 📚",
                    currentStreak = 1,
                    isStudyingNow = false,
                    subjectNow = subject,
                    cheerCount = 0
                )
            )
            triggerCelebration("Added study partner $name for $subject! 🤝")
        }
    }

    // --- BADGES CALCULATION ---
    fun getBadges(): List<BadgeItem> {
        val p = _profileState()
        val taskCount = tasks.value.count { it.isCompleted }
        val sessionList = sessions.value
        val totalStudyMinutes = sessionList.sumOf { it.durationMinutes }
        val waterMlToday = todayWaterLogs.value.sumOf { it.amountMl }
        val habitStreakMax = habits.value.maxOfOrNull { it.streakDays } ?: 0

        return listOf(
            BadgeItem(
                id = "b1",
                title = "Novice Scholar",
                description = "Complete your first task",
                icon = "school",
                xpReward = 50,
                isUnlocked = taskCount >= 1,
                progress = (taskCount / 1f).coerceIn(0f, 1f)
            ),
            BadgeItem(
                id = "b2",
                title = "Deep Diver",
                description = "Complete a 50+ min study session",
                icon = "psychology",
                xpReward = 100,
                isUnlocked = sessionList.any { it.durationMinutes >= 50 },
                progress = if (sessionList.any { it.durationMinutes >= 50 }) 1f else 0.5f
            ),
            BadgeItem(
                id = "b3",
                title = "Hydro Master",
                description = "Reach 2000ml+ water intake in a day",
                icon = "water_drop",
                xpReward = 150,
                isUnlocked = waterMlToday >= 2000,
                progress = (waterMlToday / 2000f).coerceIn(0f, 1f)
            ),
            BadgeItem(
                id = "b4",
                title = "Routine Machine",
                description = "Maintain a 5-day habit streak",
                icon = "local_fire_department",
                xpReward = 200,
                isUnlocked = habitStreakMax >= 5,
                progress = (habitStreakMax / 5f).coerceIn(0f, 1f)
            ),
            BadgeItem(
                id = "b5",
                title = "Century Club",
                description = "Log over 100 minutes of total study time",
                icon = "timer",
                xpReward = 250,
                isUnlocked = totalStudyMinutes >= 100,
                progress = (totalStudyMinutes / 100f).coerceIn(0f, 1f)
            ),
            BadgeItem(
                id = "b6",
                title = "Master of Discipline",
                description = "Reach Level 3 in StudyFlow",
                icon = "military_tech",
                xpReward = 500,
                isUnlocked = p.level >= 3,
                progress = (p.level / 3f).coerceIn(0f, 1f)
            )
        )
    }

    private suspend fun addXp(amount: Int) {
        val cur = _profileState()
        val newXp = cur.totalXp + amount
        val newLevel = 1 + (newXp / 150)
        val leveledUp = newLevel > cur.level
        repository.updateProfile(
            cur.copy(
                totalXp = newXp,
                level = newLevel
            )
        )
        if (leveledUp) {
            triggerCelebration("⭐ LEVEL UP! You reached Level $newLevel! You are unstoppable!")
        }
    }

    private fun _profileState(): UserProfileEntity {
        return profile.value
    }

    private fun vibratePhone(durationMs: Long) {
        val app = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // gracefully fallback if vibration permission or hardware unavailable
        }
    }
}
