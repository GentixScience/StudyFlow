package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusPurple
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockCanvas(
    timestampMillis: Long,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
    val hours = calendar.get(Calendar.HOUR)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)

    val timeFormatted = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(timestampMillis))
    val dateFormatted = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(timestampMillis))

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, FocusCyan.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Analog Clock Face
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(2.dp, FocusCyan.copy(alpha = 0.4f * pulseGlow), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw 12 hour ticks
                    for (i in 0 until 12) {
                        val angle = (i * 30.0 - 90.0) * (Math.PI / 180.0)
                        val isQuarter = (i % 3 == 0)
                        val tickLen = if (isQuarter) 14.dp.toPx() else 8.dp.toPx()
                        val tickColor = if (isQuarter) FocusCyan else Color(0xFF64748B)
                        val strokeW = if (isQuarter) 3.dp.toPx() else 1.5.dp.toPx()

                        val startX = (center.x + (radius - tickLen) * cos(angle)).toFloat()
                        val startY = (center.y + (radius - tickLen) * sin(angle)).toFloat()
                        val endX = (center.x + radius * cos(angle)).toFloat()
                        val endY = (center.y + radius * sin(angle)).toFloat()

                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = strokeW,
                            cap = StrokeCap.Round
                        )
                    }

                    // Hour hand
                    val hourAngle = ((hours + minutes / 60.0) * 30.0 - 90.0) * (Math.PI / 180.0)
                    val hourHandLen = radius * 0.52f
                    drawLine(
                        color = Color.White,
                        start = center,
                        end = Offset(
                            (center.x + hourHandLen * cos(hourAngle)).toFloat(),
                            (center.y + hourHandLen * sin(hourAngle)).toFloat()
                        ),
                        strokeWidth = 4.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Minute hand
                    val minAngle = ((minutes + seconds / 60.0) * 6.0 - 90.0) * (Math.PI / 180.0)
                    val minHandLen = radius * 0.72f
                    drawLine(
                        color = FocusCyan,
                        start = center,
                        end = Offset(
                            (center.x + minHandLen * cos(minAngle)).toFloat(),
                            (center.y + minHandLen * sin(minAngle)).toFloat()
                        ),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Second hand
                    val secAngle = (seconds * 6.0 - 90.0) * (Math.PI / 180.0)
                    val secHandLen = radius * 0.85f
                    drawLine(
                        color = FocusPurple,
                        start = center,
                        end = Offset(
                            (center.x + secHandLen * cos(secAngle)).toFloat(),
                            (center.y + secHandLen * sin(secAngle)).toFloat()
                        ),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Center pivot point
                    drawCircle(FocusCyan, radius = 5.dp.toPx(), center = center)
                    drawCircle(Color.White, radius = 2.dp.toPx(), center = center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Digital Time display
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = FocusCyan
            )

            Text(
                text = dateFormatted,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
