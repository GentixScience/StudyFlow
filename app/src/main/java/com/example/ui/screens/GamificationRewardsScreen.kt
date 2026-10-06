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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BadgeItem
import com.example.data.model.RewardEntity
import com.example.data.model.UserProfileEntity
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusEmerald
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.FocusRose

@Composable
fun GamificationRewardsScreen(
    profile: UserProfileEntity,
    badges: List<BadgeItem>,
    rewards: List<RewardEntity>,
    onRedeemReward: (RewardEntity) -> Unit,
    onAddReward: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddRewardModal by remember { mutableStateOf(false) }

    val nextLevelTargetXp = profile.level * 150
    val xpInCurrentLevel = profile.totalXp % 150
    val xpProgress = (xpInCurrentLevel.toFloat() / 150f).coerceIn(0f, 1f)

    val rankTitle = when (profile.level) {
        1 -> "Novice Scholar"
        2 -> "Focus Apprentice"
        3 -> "Academic Warrior"
        4 -> "Discipline Master"
        5 -> "Knowledge Grandmaster"
        else -> "Zenith Polymath"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Scholar Level Hero Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("card_scholar_level")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LEVEL ${profile.level}",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = FocusCyan
                            )
                            Text(
                                text = rankTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FocusAmber
                            )
                        }

                        // Streak Pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = FocusAmber.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${profile.dailyStreak}d Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FocusAmber
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // XP Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP Progress: $xpInCurrentLevel / 150 XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Total: ${profile.totalXp} XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FocusCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { xpProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = FocusCyan,
                        trackColor = Color(0xFF0F172A),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Earn XP by completing to-dos (+15-30), timer sessions (+25-60), checking routines (+20), and drinking water (+10)!",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Unlockable Badges Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Achievement Badges Vault", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Text("${badges.count { it.isUnlocked }}/${badges.size} Unlocked", fontSize = 12.sp, color = FocusAmber, fontWeight = FontWeight.Bold)
            }
        }

        // Badges Grid / Cards
        items(badges, key = { it.id }) { badge ->
            BadgeItemCard(badge = badge)
        }

        // Rewards Shop Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Redeem, contentDescription = null, tint = FocusEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Self-Rewards Vault", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { showAddRewardModal = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = FocusEmerald),
                    modifier = Modifier.testTag("btn_open_add_reward")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Reward", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Rewards Items
        items(rewards, key = { it.id }) { reward ->
            RewardItemCard(
                reward = reward,
                userPoints = profile.totalXp,
                onRedeem = { onRedeemReward(reward) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showAddRewardModal) {
        AddRewardDialog(
            onDismiss = { showAddRewardModal = false },
            onConfirm = { t, c ->
                onAddReward(t, c)
                showAddRewardModal = false
            }
        )
    }
}

@Composable
private fun BadgeItemCard(badge: BadgeItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) Color(0xFF131D33) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (badge.isUnlocked) FocusAmber.copy(alpha = 0.6f) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth().testTag("badge_${badge.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (badge.isUnlocked) FocusAmber.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (badge.isUnlocked) FocusAmber else Color(0xFF475569),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (badge.isUnlocked) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = badge.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (badge.isUnlocked) Color.White else Color(0xFF94A3B8)
                    )
                    Text(
                        text = "+${badge.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (badge.isUnlocked) FocusAmber else Color(0xFF64748B)
                    )
                }

                Text(
                    text = badge.description,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!badge.isUnlocked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { badge.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = FocusAmber,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardItemCard(
    reward: RewardEntity,
    userPoints: Int,
    onRedeem: () -> Unit
) {
    val canAfford = userPoints >= reward.costPoints

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reward.isRedeemed) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (reward.isRedeemed) FocusEmerald.copy(alpha = 0.3f) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth().testTag("reward_item_${reward.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (reward.isRedeemed) Color(0xFF94A3B8) else Color.White
                )
                Text(
                    text = if (reward.isRedeemed) "Redeemed on ${reward.redeemedDate}" else "Cost: ${reward.costPoints} points",
                    fontSize = 12.sp,
                    color = if (reward.isRedeemed) FocusEmerald else FocusAmber,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (reward.isRedeemed) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FocusEmerald.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = FocusEmerald, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Claimed", fontSize = 11.sp, color = FocusEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onRedeem,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = FocusAmber, contentColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_redeem_${reward.id}")
                ) {
                    Text("Redeem", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AddRewardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var costStr by remember { mutableStateOf("150") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusEmerald),
            modifier = Modifier.fillMaxWidth().testTag("dialog_add_reward")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Create Custom Self-Reward", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Reward Name (e.g. 1hr Gaming, Movie, Pizza)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusEmerald,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_reward_title")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = costStr,
                    onValueChange = { costStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Points Cost (e.g. 150)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusEmerald,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_reward_cost")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val c = costStr.toIntOrNull() ?: 100
                            if (title.isNotBlank()) {
                                onConfirm(title, c)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusEmerald, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_reward")
                    ) {
                        Text("Add Reward", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
