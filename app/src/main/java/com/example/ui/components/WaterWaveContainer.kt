package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterBlueLight
import kotlin.math.sin

@Composable
fun WaterWaveContainer(
    currentMl: Int,
    goalMl: Int,
    onAddWater: (Int) -> Unit,
    onTriggerWaterAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (currentMl.toFloat() / goalMl.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "waterFill"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, WaterBlueLight.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(WaterBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Water Drop",
                            tint = WaterBlueLight,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Daily Hydration Flow",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Target: $goalMl ml • Brain power boost",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = onTriggerWaterAlarm,
                    modifier = Modifier.testTag("btn_test_water_alarm"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WaterBlueLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alarm Test",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Alarm Now", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Water Bottle / Cylinder
            Box(
                modifier = Modifier
                    .size(width = 150.dp, height = 190.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF0F1E36))
                    .border(2.dp, WaterBlueLight.copy(alpha = 0.4f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val waterLevel = height * (1f - animatedProgress)

                    // Draw back wave
                    val pathBack = Path()
                    pathBack.moveTo(0f, height)
                    pathBack.lineTo(0f, waterLevel)

                    var x = 0f
                    val waveHeight = 8.dp.toPx()
                    val waveLength = width
                    while (x <= width) {
                        val y = waterLevel + (sin((x / waveLength * 2f * Math.PI.toFloat()) + waveOffset + 1.2f) * waveHeight)
                        pathBack.lineTo(x, y)
                        x += 4f
                    }
                    pathBack.lineTo(width, height)
                    pathBack.close()

                    drawPath(
                        path = pathBack,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0284C7).copy(alpha = 0.5f),
                                Color(0xFF0369A1).copy(alpha = 0.8f)
                            )
                        )
                    )

                    // Draw front wave
                    val pathFront = Path()
                    pathFront.moveTo(0f, height)
                    pathFront.lineTo(0f, waterLevel)

                    x = 0f
                    while (x <= width) {
                        val y = waterLevel + (sin((x / waveLength * 2f * Math.PI.toFloat()) - waveOffset) * (waveHeight * 0.9f))
                        pathFront.lineTo(x, y)
                        x += 4f
                    }
                    pathFront.lineTo(width, height)
                    pathFront.close()

                    drawPath(
                        path = pathFront,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                WaterBlueLight.copy(alpha = 0.9f),
                                WaterBlue.copy(alpha = 0.95f),
                                Color(0xFF075985)
                            )
                        )
                    )

                    // Bubbles
                    if (animatedProgress > 0.1f) {
                        val bubbleY1 = waterLevel + 40f + (sin(waveOffset * 2f) * 10f)
                        val bubbleY2 = waterLevel + 70f + (sin(waveOffset * 3f + 1f) * 15f)
                        drawCircle(Color.White.copy(alpha = 0.4f), radius = 3.dp.toPx(), center = Offset(width * 0.35f, bubbleY1))
                        drawCircle(Color.White.copy(alpha = 0.3f), radius = 4.dp.toPx(), center = Offset(width * 0.65f, bubbleY2))
                    }
                }

                // Center percentage text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .shadow(4.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "$currentMl",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "/ $goalMl ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = WaterBlueLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Add Water Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WaterCupButton(amountMl = 150, label = "+150ml", onClick = { onAddWater(150) }, modifier = Modifier.weight(1f))
                WaterCupButton(amountMl = 250, label = "+250ml", onClick = { onAddWater(250) }, modifier = Modifier.weight(1f))
                WaterCupButton(amountMl = 500, label = "+500ml", onClick = { onAddWater(500) }, modifier = Modifier.weight(1f))
                WaterCupButton(amountMl = 750, label = "+750ml", onClick = { onAddWater(750) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WaterCupButton(
    amountMl: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(44.dp)
            .testTag("btn_water_$amountMl"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0F2B48),
            contentColor = WaterBlueLight
        )
    ) {
        Icon(
            imageVector = Icons.Default.Opacity,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
