package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusEmerald
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.FocusRose
import com.example.ui.viewmodel.PomodoroMode
import com.example.ui.viewmodel.StopwatchLap
import java.util.Locale

@Composable
fun TimerStopwatchScreen(
    timerMode: PomodoroMode,
    totalTimerSeconds: Int,
    remainingTimerSeconds: Int,
    isTimerRunning: Boolean,
    selectedSubject: String,
    completedSessionsCount: Int,
    onSetTimerPreset: (PomodoroMode) -> Unit,
    onSetCustomMinutes: (Int) -> Unit,
    onSelectSubject: (String) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    // Stopwatch parameters
    stopwatchElapsedMs: Long,
    isStopwatchRunning: Boolean,
    laps: List<StopwatchLap>,
    onStartStopwatch: () -> Unit,
    onPauseStopwatch: () -> Unit,
    onResetStopwatch: () -> Unit,
    onRecordLap: () -> Unit,
    onSaveStopwatchToTracker: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Focus Timer, 1: Stopwatch

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = FocusCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FocusCyan,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Study Focus Timer", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("tab_focus_timer")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Precision Stopwatch", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                modifier = Modifier.testTag("tab_stopwatch")
            )
        }

        if (selectedTab == 0) {
            FocusTimerContent(
                timerMode = timerMode,
                totalTimerSeconds = totalTimerSeconds,
                remainingTimerSeconds = remainingTimerSeconds,
                isTimerRunning = isTimerRunning,
                selectedSubject = selectedSubject,
                completedSessionsCount = completedSessionsCount,
                onSetTimerPreset = onSetTimerPreset,
                onSetCustomMinutes = onSetCustomMinutes,
                onSelectSubject = onSelectSubject,
                onStartTimer = onStartTimer,
                onPauseTimer = onPauseTimer,
                onResetTimer = onResetTimer
            )
        } else {
            StopwatchContent(
                stopwatchElapsedMs = stopwatchElapsedMs,
                isStopwatchRunning = isStopwatchRunning,
                laps = laps,
                onStartStopwatch = onStartStopwatch,
                onPauseStopwatch = onPauseStopwatch,
                onResetStopwatch = onResetStopwatch,
                onRecordLap = onRecordLap,
                onSaveStopwatchToTracker = onSaveStopwatchToTracker
            )
        }
    }
}

@Composable
private fun FocusTimerContent(
    timerMode: PomodoroMode,
    totalTimerSeconds: Int,
    remainingTimerSeconds: Int,
    isTimerRunning: Boolean,
    selectedSubject: String,
    completedSessionsCount: Int,
    onSetTimerPreset: (PomodoroMode) -> Unit,
    onSetCustomMinutes: (Int) -> Unit,
    onSelectSubject: (String) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    val subjects = listOf("Mathematics", "Physics", "Computer Science", "Chemistry", "Literature", "Biology", "General Study")

    val progress = if (totalTimerSeconds > 0) {
        remainingTimerSeconds.toFloat() / totalTimerSeconds.toFloat()
    } else 0f

    val minutes = remainingTimerSeconds / 60
    val seconds = remainingTimerSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subject Selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Current Study Subject",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subj ->
                        val isSel = selectedSubject == subj
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSel) FocusPurple else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .clickable { onSelectSubject(subj) }
                                .testTag("subject_chip_$subj")
                        ) {
                            Text(
                                text = subj,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Timer Preset Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PomodoroMode.entries.forEach { mode ->
                    val isSel = timerMode == mode
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) FocusCyan else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSel) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSetTimerPreset(mode) }
                            .testTag("preset_${mode.name}")
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = mode.title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Circular Timer Dial
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0A0F1D)
                            )
                        )
                    )
                    .border(
                        2.dp,
                        if (isTimerRunning) FocusCyan.copy(alpha = 0.5f * pulseGlow) else Color(0xFF334155),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val strokeW = 14.dp.toPx()

                    // Background ring track
                    drawCircle(
                        color = Color(0xFF1E293B),
                        radius = size.minDimension / 2f - strokeW / 2,
                        style = Stroke(strokeW)
                    )

                    // Foreground animated progress arc
                    val sweep = 360f * progress
                    val ringColor = if (timerMode.isBreak) FocusEmerald else FocusCyan

                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                ringColor.copy(alpha = 0.8f),
                                ringColor,
                                FocusPurple
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(strokeW, cap = StrokeCap.Round),
                        size = Size(size.width - strokeW, size.height - strokeW),
                        topLeft = Offset(strokeW / 2, strokeW / 2)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedSubject,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FocusPurple
                    )
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                    Text(
                        text = if (timerMode.isBreak) "☕ Rest & Rehydrate" else "🎯 Deep Focus",
                        fontSize = 12.sp,
                        color = if (timerMode.isBreak) FocusEmerald else FocusCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🔥 $completedSessionsCount sessions done today",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Controls (Play, Pause, Reset, Custom)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = onResetTimer,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("btn_reset_timer")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                }

                // Play / Pause Button
                Button(
                    onClick = if (isTimerRunning) onPauseTimer else onStartTimer,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTimerRunning) FocusRose else FocusCyan,
                        contentColor = Color(0xFF0F172A)
                    ),
                    modifier = Modifier
                        .size(76.dp)
                        .testTag("btn_toggle_timer")
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pause" else "Start",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Custom Time Button
                OutlinedButton(
                    onClick = { showCustomDialog = true },
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("btn_custom_timer")
                ) {
                    Icon(Icons.Default.Timer, contentDescription = "Custom Minutes", tint = Color.White)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showCustomDialog) {
        CustomMinutesDialog(
            currentMinutes = totalTimerSeconds / 60,
            onDismiss = { showCustomDialog = false },
            onConfirm = { mins ->
                onSetCustomMinutes(mins)
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun StopwatchContent(
    stopwatchElapsedMs: Long,
    isStopwatchRunning: Boolean,
    laps: List<StopwatchLap>,
    onStartStopwatch: () -> Unit,
    onPauseStopwatch: () -> Unit,
    onResetStopwatch: () -> Unit,
    onRecordLap: () -> Unit,
    onSaveStopwatchToTracker: (String, String) -> Unit
) {
    var showSaveModal by remember { mutableStateOf(false) }

    val hours = (stopwatchElapsedMs / 3600000L).toInt()
    val minutes = ((stopwatchElapsedMs % 3600000L) / 60000L).toInt()
    val seconds = ((stopwatchElapsedMs % 60000L) / 1000L).toInt()
    val millis = ((stopwatchElapsedMs % 1000L) / 10L).toInt()

    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    val msFormatted = String.format(Locale.getDefault(), ".%02d", millis)

    val fastestLap = laps.minByOrNull { it.lapTimeMs }
    val slowestLap = if (laps.size > 1) laps.maxByOrNull { it.lapTimeMs } else null

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Big Stopwatch Display
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, FocusPurple.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STOPWATCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = FocusPurple
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                        Text(
                            text = msFormatted,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = FocusCyan,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "${laps.size} Laps recorded",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onResetStopwatch,
                    shape = CircleShape,
                    modifier = Modifier.size(54.dp).testTag("btn_reset_stopwatch")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                }

                Button(
                    onClick = if (isStopwatchRunning) onPauseStopwatch else onStartStopwatch,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStopwatchRunning) FocusRose else FocusPurple,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(72.dp).testTag("btn_toggle_stopwatch")
                ) {
                    Icon(
                        imageVector = if (isStopwatchRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Start/Stop",
                        modifier = Modifier.size(34.dp)
                    )
                }

                OutlinedButton(
                    onClick = onRecordLap,
                    shape = CircleShape,
                    enabled = isStopwatchRunning || stopwatchElapsedMs > 0,
                    modifier = Modifier.size(54.dp).testTag("btn_lap_stopwatch")
                ) {
                    Icon(Icons.Default.Flag, contentDescription = "Lap", tint = Color.White)
                }
            }
        }

        // Save session button
        item {
            Button(
                onClick = { showSaveModal = true },
                enabled = stopwatchElapsedMs >= 30000L, // at least 30 seconds
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FocusEmerald, contentColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth().testTag("btn_save_stopwatch_session")
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save to Study Tracker (+XP)", fontWeight = FontWeight.Bold)
            }
        }

        // Lap Times List
        if (laps.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lap", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Text("Split", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    Text("Total", fontSize = 12.sp, color = Color(0xFF94A3B8))
                }
            }

            items(laps) { lap ->
                val isFastest = lap == fastestLap && laps.size > 1
                val isSlowest = lap == slowestLap && laps.size > 1

                val lapColor = when {
                    isFastest -> FocusEmerald
                    isSlowest -> FocusRose
                    else -> Color.White
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Lap ${lap.lapIndex}", fontWeight = FontWeight.Bold, color = lapColor, fontSize = 13.sp)
                        Text(formatMs(lap.lapTimeMs), fontFamily = FontFamily.Monospace, color = lapColor, fontSize = 13.sp)
                        Text(formatMs(lap.totalTimeMs), fontFamily = FontFamily.Monospace, color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showSaveModal) {
        SaveStopwatchDialog(
            durationMinutes = ((stopwatchElapsedMs / 60000L).toInt()).coerceAtLeast(1),
            onDismiss = { showSaveModal = false },
            onConfirm = { subject, notes ->
                onSaveStopwatchToTracker(subject, notes)
                showSaveModal = false
            }
        )
    }
}

private fun formatMs(ms: Long): String {
    val m = (ms / 60000L)
    val s = (ms % 60000L) / 1000L
    val cs = (ms % 1000L) / 10L
    return String.format(Locale.getDefault(), "%02d:%02d.%02d", m, s, cs)
}

@Composable
private fun CustomMinutesDialog(
    currentMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var textVal by remember { mutableStateOf(currentMinutes.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusCyan),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Custom Focus Duration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = textVal,
                    onValueChange = { textVal = it.filter { char -> char.isDigit() } },
                    label = { Text("Minutes (1 to 180)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_custom_minutes")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val mins = textVal.toIntOrNull() ?: 25
                            onConfirm(mins)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusCyan, contentColor = Color(0xFF0F172A))
                    ) {
                        Text("Set Timer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveStopwatchDialog(
    durationMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var subject by remember { mutableStateOf("Mathematics") }
    var notes by remember { mutableStateOf("") }
    val subjects = listOf("Mathematics", "Physics", "Computer Science", "Chemistry", "Literature", "Biology")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusEmerald),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Save Study Session", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Duration: $durationMinutes minutes (+${durationMinutes * 2} XP)", fontSize = 13.sp, color = FocusEmerald)

                Spacer(modifier = Modifier.height(14.dp))
                Text("Subject", fontSize = 12.sp, color = Color(0xFF94A3B8))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    items(subjects) { s ->
                        val isSel = subject == s
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) FocusEmerald else Color(0xFF1E293B),
                            contentColor = if (isSel) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.clickable { subject = s }
                        ) {
                            Text(s, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Session Notes (optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusEmerald,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_session_notes")
                )

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(subject, notes) },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusEmerald, contentColor = Color(0xFF0F172A))
                    ) {
                        Text("Log Session", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
