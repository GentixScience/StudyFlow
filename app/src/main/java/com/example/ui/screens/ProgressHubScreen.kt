package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeItem
import com.example.data.model.HabitEntity
import com.example.data.model.RewardEntity
import com.example.data.model.StudyBuddyEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WaterLogEntity
import com.example.ui.theme.FocusCyan

@Composable
fun ProgressHubScreen(
    tasks: List<TaskEntity>,
    sessions: List<StudySessionEntity>,
    habits: List<HabitEntity>,
    waterLogs: List<WaterLogEntity>,
    dailyWaterGoalMl: Int,
    profile: UserProfileEntity,
    badges: List<BadgeItem>,
    rewards: List<RewardEntity>,
    buddies: List<StudyBuddyEntity>,
    onRedeemReward: (RewardEntity) -> Unit,
    onAddReward: (String, Int) -> Unit,
    onSendCheer: (StudyBuddyEntity, String) -> Unit,
    onAddBuddy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Analytics, 1: Rewards/XP, 2: Social

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = FocusCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = FocusCyan,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                modifier = Modifier.testTag("subtab_analytics")
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("XP & Badges", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                modifier = Modifier.testTag("subtab_rewards")
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Study Buddies", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                modifier = Modifier.testTag("subtab_social")
            )
        }

        when (selectedSubTab) {
            0 -> DashboardAnalyticsScreen(
                tasks = tasks,
                sessions = sessions,
                habits = habits,
                waterLogs = waterLogs,
                dailyWaterGoalMl = dailyWaterGoalMl
            )
            1 -> GamificationRewardsScreen(
                profile = profile,
                badges = badges,
                rewards = rewards,
                onRedeemReward = onRedeemReward,
                onAddReward = onAddReward
            )
            2 -> SocialAccountabilityScreen(
                buddies = buddies,
                streakDays = profile.dailyStreak,
                studyMinutesToday = sessions.sumOf { it.durationMinutes },
                tasksCompletedToday = tasks.count { it.isCompleted },
                waterMlToday = waterLogs.sumOf { it.amountMl },
                level = profile.level,
                onSendCheer = onSendCheer,
                onAddBuddy = onAddBuddy
            )
        }
    }
}
