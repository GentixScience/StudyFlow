package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.WaterBlueLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyFlowTopBar(
    streakDays: Int,
    totalXp: Int,
    todayWaterMl: Int,
    onWaterClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "StudyFlow",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 0.5.sp,
                    color = FocusCyan
                )

                // Streak Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FocusAmber.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${streakDays}d", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FocusAmber)
                    }
                }

                // XP Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("$totalXp", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        },
        actions = {
            // Quick Water badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF0F2B48),
                modifier = Modifier
                    .clickable { onWaterClick() }
                    .padding(end = 4.dp)
                    .testTag("topbar_water_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = "Water", tint = WaterBlueLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${todayWaterMl}ml", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = WaterBlueLight)
                }
            }

            IconButton(
                onClick = onShareClick,
                modifier = Modifier.testTag("topbar_btn_share")
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share Habit Card", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBackground,
            titleContentColor = Color.White
        ),
        modifier = modifier
    )
}
