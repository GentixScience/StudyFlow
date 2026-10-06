package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HabitEntity
import com.example.data.model.WaterLogEntity
import com.example.ui.components.WaterWaveContainer
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusEmerald
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.WaterBlueLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HabitsRoutineScreen(
    habits: List<HabitEntity>,
    todayWaterLogs: List<WaterLogEntity>,
    dailyWaterGoalMl: Int,
    onToggleHabit: (HabitEntity) -> Unit,
    onDeleteHabit: (HabitEntity) -> Unit,
    onAddHabit: (title: String, category: String, iconName: String) -> Unit,
    onAddWater: (Int) -> Unit,
    onSetWaterGoal: (Int) -> Unit,
    onTriggerWaterAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    var showSetGoalDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Morning", "Study", "Night", "Health")

    val filteredHabits = habits.filter {
        if (selectedCategoryFilter == "All") true else it.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    val totalWaterMl = todayWaterLogs.sumOf { it.amountMl }
    val completedHabitsCount = habits.count { it.isCompletedToday }
    val totalHabitsCount = habits.size
    val completionPct = if (totalHabitsCount > 0) (completedHabitsCount * 100 / totalHabitsCount) else 0

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Top Routine Progress Card
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Student Routine Mastery",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$completedHabitsCount of $totalHabitsCount daily habits checked",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FocusAmber
                            )
                            Text(
                                text = "Keep building streaks to level up! 🔥",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(FocusAmber.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, FocusAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$completionPct%",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = FocusAmber
                            )
                        }
                    }
                }
            }

            // Hydration Section
            item {
                WaterWaveContainer(
                    currentMl = totalWaterMl,
                    goalMl = dailyWaterGoalMl,
                    onAddWater = onAddWater,
                    onTriggerWaterAlarm = onTriggerWaterAlarm
                )
            }

            // Water Target Adjustment
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Water Intake History (${todayWaterLogs.size} logs)",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF94A3B8)
                    )
                    TextButton(onClick = { showSetGoalDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp), tint = WaterBlueLight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Target", fontSize = 12.sp, color = WaterBlueLight)
                    }
                }
            }

            // Water Logs chips
            if (todayWaterLogs.isNotEmpty()) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(todayWaterLogs) { log ->
                            val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.timestamp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F2B48),
                                border = androidx.compose.foundation.BorderStroke(1.dp, WaterBlueLight.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = WaterBlueLight, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+${log.amountMl}ml", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("($timeStr)", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }
            }

            // Routines Header & Filters
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Routine Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSel = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) FocusAmber else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSel) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Habits List
            items(filteredHabits, key = { it.id }) { habit ->
                HabitCardItem(
                    habit = habit,
                    onToggle = { onToggleHabit(habit) },
                    onDelete = { onDeleteHabit(habit) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to add habit
        FloatingActionButton(
            onClick = { showAddHabitDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_habit"),
            containerColor = FocusAmber,
            contentColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Habit")
        }
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onConfirm = { title, cat, icon ->
                onAddHabit(title, cat, icon)
                showAddHabitDialog = false
            }
        )
    }

    if (showSetGoalDialog) {
        SetWaterGoalDialog(
            currentGoal = dailyWaterGoalMl,
            onDismiss = { showSetGoalDialog = false },
            onConfirm = { goal ->
                onSetWaterGoal(goal)
                showSetGoalDialog = false
            }
        )
    }
}

@Composable
private fun HabitCardItem(
    habit: HabitEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isCompletedToday) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (habit.isCompletedToday) FocusEmerald.copy(alpha = 0.5f) else Color(0xFF334155)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_item_${habit.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (habit.isCompletedToday) FocusEmerald else Color.Transparent)
                    .border(2.dp, if (habit.isCompletedToday) FocusEmerald else Color(0xFF64748B), CircleShape)
                    .clickable { onToggle() }
                    .testTag("checkbox_habit_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (habit.isCompletedToday) {
                    Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (habit.isCompletedToday) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                    textDecoration = if (habit.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category pill
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                        Text(
                            text = habit.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FocusCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Streak Fire Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FocusAmber.copy(alpha = 0.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${habit.streakDays} Day Streak",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FocusAmber
                            )
                        }
                    }

                    // XP Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = FocusPurple.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "+${habit.xpReward} XP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FocusPurple,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp).testTag("btn_delete_habit_${habit.id}")
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Habit", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Morning") }
    val categories = listOf("Morning", "Study", "Night", "Health")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusAmber),
            modifier = Modifier.fillMaxWidth().testTag("dialog_add_habit")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add Routine Habit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Name (e.g. Read 15m or Stretch)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusAmber,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_habit_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Routine Category", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = category == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) FocusAmber else Color(0xFF1E293B),
                            contentColor = if (isSel) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.weight(1f).clickable { category = cat }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, category, "bolt")
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = FocusAmber, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_habit")
                    ) {
                        Text("Add Habit (+20 XP)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SetWaterGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, WaterBlueLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Set Daily Hydration Target", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it.filter { char -> char.isDigit() } },
                    label = { Text("Milliliters (e.g. 2500)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WaterBlueLight,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_water_goal")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val g = goalText.toIntOrNull() ?: 2500
                            onConfirm(g)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlueLight, contentColor = Color(0xFF0F172A))
                    ) {
                        Text("Update Target", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
