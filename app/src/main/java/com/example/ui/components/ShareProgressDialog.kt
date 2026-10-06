package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusEmerald
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.WaterBlueLight

@Composable
fun ShareProgressDialog(
    streakDays: Int,
    studyMinutesToday: Int,
    tasksCompletedToday: Int,
    waterMlToday: Int,
    level: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareText = """
        🚀 StudyFlow Daily Student Habit Card
        🔥 Day Streak: $streakDays Days
        📚 Study Focus: $studyMinutesToday Minutes
        ✅ Tasks Done: $tasksCompletedToday Completed
        💧 Water Hydration: $waterMlToday ml
        ⭐ Level $level Scholar
        
        Crushing my academic goals with StudyFlow! Join my study partnership! ⚡
    """.trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("dialog_share_progress")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Habit Card",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0F2B48),
                                    Color(0xFF0A0F1D)
                                )
                            )
                        )
                        .border(1.dp, FocusCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STUDYFLOW",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 2.sp,
                                color = FocusCyan
                            )
                            Box(
                                modifier = Modifier
                                    .background(FocusAmber.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LVL $level SCHOLAR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FocusAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMiniBox(
                                label = "Streak",
                                value = "🔥 $streakDays d",
                                color = FocusAmber
                            )
                            StatMiniBox(
                                label = "Study Time",
                                value = "⏱ ${studyMinutesToday}m",
                                color = FocusPurple
                            )
                            StatMiniBox(
                                label = "Tasks",
                                value = "✅ $tasksCompletedToday",
                                color = FocusEmerald
                            )
                            StatMiniBox(
                                label = "Water",
                                value = "💧 ${waterMlToday}ml",
                                color = WaterBlueLight
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "\"Consistency transforms average students into masterminds.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share StudyFlow Progress")
                        context.startActivity(shareIntent)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_share"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FocusCyan,
                        contentColor = Color(0xFF0F172A)
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share to Friends & Study Groups", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatMiniBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
    }
}
