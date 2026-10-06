package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AlarmEntity
import com.example.ui.components.AnalogClockCanvas
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.WaterBlueLight
import java.util.Locale

@Composable
fun ClockAlarmScreen(
    currentTimeMillis: Long,
    quote: String,
    alarms: List<AlarmEntity>,
    onToggleAlarm: (AlarmEntity) -> Unit,
    onDeleteAlarm: (AlarmEntity) -> Unit,
    onTriggerAlarmTest: (AlarmEntity) -> Unit,
    onAddAlarm: (hour: Int, min: Int, label: String, repeatDays: String, isWater: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Analog & Digital Live Clock
                AnalogClockCanvas(timestampMillis = currentTimeMillis)
            }

            // Student Quote of the Day
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(FocusPurple.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = FocusPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Daily Student Mindset",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FocusPurple
                            )
                            Text(
                                text = quote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Alarms Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Student Routine Alarms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${alarms.count { it.isEnabled }} Active",
                        fontSize = 12.sp,
                        color = FocusCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Alarms List
            items(alarms, key = { it.id }) { alarm ->
                AlarmItemCard(
                    alarm = alarm,
                    onToggle = { onToggleAlarm(alarm) },
                    onDelete = { onDeleteAlarm(alarm) },
                    onTestRing = { onTriggerAlarmTest(alarm) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to add alarm
        FloatingActionButton(
            onClick = { showAddAlarmDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_alarm"),
            containerColor = FocusAmber,
            contentColor = Color(0xFF0F172A),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Alarm")
        }
    }

    if (showAddAlarmDialog) {
        AddAlarmDialog(
            onDismiss = { showAddAlarmDialog = false },
            onConfirm = { h, m, label, repeat, isWater ->
                onAddAlarm(h, m, label, repeat, isWater)
                showAddAlarmDialog = false
            }
        )
    }
}

@Composable
private fun AlarmItemCard(
    alarm: AlarmEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onTestRing: () -> Unit
) {
    val amPm = if (alarm.hour >= 12) "PM" else "AM"
    val displayHour = when {
        alarm.hour == 0 -> 12
        alarm.hour > 12 -> alarm.hour - 12
        else -> alarm.hour
    }
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", displayHour, alarm.minute)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (alarm.isEnabled) {
                if (alarm.isWaterAlarm) WaterBlueLight.copy(alpha = 0.5f) else FocusAmber.copy(alpha = 0.5f)
            } else Color(0xFF334155)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alarm_item_${alarm.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (alarm.isEnabled) Color.White else Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = amPm,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.isEnabled) (if (alarm.isWaterAlarm) WaterBlueLight else FocusAmber) else Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("switch_alarm_${alarm.id}"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF0F172A),
                        checkedTrackColor = if (alarm.isWaterAlarm) WaterBlueLight else FocusAmber
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (alarm.isWaterAlarm) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = WaterBlueLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = alarm.label,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = if (alarm.isEnabled) Color.White else Color(0xFF94A3B8)
                        )
                    }
                    Text(
                        text = alarm.repeatDays,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Test Ring Button
                    IconButton(
                        onClick = onTestRing,
                        modifier = Modifier.size(34.dp).testTag("btn_test_ring_${alarm.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Test Ring",
                            tint = if (alarm.isWaterAlarm) WaterBlueLight else FocusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp).testTag("btn_delete_alarm_${alarm.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Alarm",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, String, String, Boolean) -> Unit
) {
    var hour by remember { mutableIntStateOf(7) }
    var minute by remember { mutableIntStateOf(0) }
    var isPm by remember { mutableStateOf(false) }
    var label by remember { mutableStateOf("Study Block Focus") }
    var isWaterAlarm by remember { mutableStateOf(false) }
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    var selectedDays by remember { mutableStateOf(setOf("Mon", "Tue", "Wed", "Thu", "Fri")) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusAmber),
            modifier = Modifier.fillMaxWidth().testTag("dialog_add_alarm")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Set Student Alarm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time Pickers (Hour : Minute : AM/PM)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Box
                    TimeNumberBox(
                        value = hour,
                        onIncrement = { hour = if (hour == 12) 1 else hour + 1 },
                        onDecrement = { hour = if (hour == 1) 12 else hour - 1 }
                    )

                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Minute Box
                    TimeNumberBox(
                        value = minute,
                        onIncrement = { minute = (minute + 5) % 60 },
                        onDecrement = { minute = if (minute - 5 < 0) 55 else minute - 5 }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // AM / PM Switcher
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isPm) FocusAmber else Color(0xFF1E293B),
                            contentColor = if (!isPm) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.clickable { isPm = false }
                        ) {
                            Text("AM", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPm) FocusAmber else Color(0xFF1E293B),
                            contentColor = if (isPm) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier.clickable { isPm = true }
                        ) {
                            Text("PM", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Alarm Label (e.g. Physics Problem Set)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusAmber,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_alarm_label")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Water Alarm toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isWaterAlarm = !isWaterAlarm }
                ) {
                    Checkbox(
                        checked = isWaterAlarm,
                        onCheckedChange = { isWaterAlarm = it },
                        colors = CheckboxDefaults.colors(checkedColor = WaterBlueLight)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tag as Drink Water Hydration Alarm 💧", fontSize = 13.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Repeat Days
                Text("Repeat Days", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    days.forEach { day ->
                        val isSelected = selectedDays.contains(day)
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) FocusAmber else Color(0xFF1E293B),
                            contentColor = if (isSelected) Color(0xFF0F172A) else Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedDays = if (isSelected) selectedDays - day else selectedDays + day
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(day.take(1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val actualHour = when {
                                isPm && hour < 12 -> hour + 12
                                !isPm && hour == 12 -> 0
                                else -> hour
                            }
                            val repStr = if (selectedDays.size == 7) "Every day" else selectedDays.joinToString(", ")
                            onConfirm(actualHour, minute, label, repStr, isWaterAlarm)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusAmber, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_alarm")
                    ) {
                        Text("Save Alarm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeNumberBox(
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.clickable { onIncrement() }
        ) {
            Text("▲", fontSize = 12.sp, color = FocusAmber, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }

        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(60.dp)
                .background(Color(0xFF1E293B), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(Locale.getDefault(), "%02d", value),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF1E293B),
            modifier = Modifier.clickable { onDecrement() }
        ) {
            Text("▼", fontSize = 12.sp, color = FocusAmber, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
    }
}
