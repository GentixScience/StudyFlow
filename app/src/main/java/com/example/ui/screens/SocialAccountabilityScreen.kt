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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolunteerActivism
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudyBuddyEntity
import com.example.ui.components.ShareProgressDialog
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.FocusCyan
import com.example.ui.theme.FocusEmerald
import com.example.ui.theme.FocusPurple
import com.example.ui.theme.FocusRose

@Composable
fun SocialAccountabilityScreen(
    buddies: List<StudyBuddyEntity>,
    streakDays: Int,
    studyMinutesToday: Int,
    tasksCompletedToday: Int,
    waterMlToday: Int,
    level: Int,
    onSendCheer: (StudyBuddyEntity, String) -> Unit,
    onAddBuddy: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showShareModal by remember { mutableStateOf(false) }
    var showAddBuddyModal by remember { mutableStateOf(false) }
    var cheerTargetBuddy by remember { mutableStateOf<StudyBuddyEntity?>(null) }

    val cheerPhrases = listOf(
        "Keep crushing it! 🔥",
        "Hydrate or diedrate! 💧",
        "Study beast mode! ⚡",
        "You got this exam! 🎓",
        "Unstoppable discipline! 🚀"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Social Hub Header Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, FocusCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("card_social_hub_header")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Study Accountability Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Crush goals together with study partners",
                                fontSize = 12.sp,
                                color = FocusCyan
                            )
                        }

                        Button(
                            onClick = { showShareModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FocusCyan, contentColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_open_share_card")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Card", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Students with study accountability partners are 95% more likely to maintain daily study habits and achieve top grades.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Accountability Partners Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Study Partners (${buddies.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Button(
                    onClick = { showAddBuddyModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = FocusAmber),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_open_add_buddy")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Partner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Buddies List
        items(buddies, key = { it.id }) { buddy ->
            BuddyItemCard(
                buddy = buddy,
                onCheerClick = { cheerTargetBuddy = buddy }
            )
        }

        // Study Groups Section
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, tint = FocusPurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Accountability Study Guilds", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }

        item {
            StudyGroupCard(
                title = "Finals Week Warriors",
                members = "18 members",
                activity = "🔥 8 studying right now • 4.2h daily avg",
                isJoined = true
            )
        }

        item {
            StudyGroupCard(
                title = "Daily 5 AM Morning Club",
                members = "12 members",
                activity = "🌅 92% morning routine completion rate",
                isJoined = true
            )
        }

        item {
            StudyGroupCard(
                title = "Algorithms & Coding Guild",
                members = "27 members",
                activity = "💻 Daily problem set grind & streaks",
                isJoined = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showShareModal) {
        ShareProgressDialog(
            streakDays = streakDays,
            studyMinutesToday = studyMinutesToday,
            tasksCompletedToday = tasksCompletedToday,
            waterMlToday = waterMlToday,
            level = level,
            onDismiss = { showShareModal = false }
        )
    }

    if (showAddBuddyModal) {
        AddStudyBuddyDialog(
            onDismiss = { showAddBuddyModal = false },
            onConfirm = { name, subj ->
                onAddBuddy(name, subj)
                showAddBuddyModal = false
            }
        )
    }

    if (cheerTargetBuddy != null) {
        SendCheerDialog(
            buddy = cheerTargetBuddy!!,
            phrases = cheerPhrases,
            onDismiss = { cheerTargetBuddy = null },
            onSend = { phrase ->
                onSendCheer(cheerTargetBuddy!!, phrase)
                cheerTargetBuddy = null
            }
        )
    }
}

@Composable
private fun BuddyItemCard(
    buddy: StudyBuddyEntity,
    onCheerClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (buddy.isStudyingNow) FocusEmerald.copy(alpha = 0.5f) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth().testTag("buddy_item_${buddy.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(FocusPurple.copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, FocusPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buddy.avatarInitials,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FocusCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = buddy.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (buddy.isStudyingNow) {
                                Box(modifier = Modifier.size(6.dp).background(FocusEmerald, CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Studying ${buddy.subjectNow}",
                                    fontSize = 11.sp,
                                    color = FocusEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = buddy.statusMessage,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Streak Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FocusAmber.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FocusAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${buddy.currentStreak}d", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FocusAmber)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👏 ${buddy.cheerCount} encouragement cheers sent",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Button(
                    onClick = onCheerClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FocusPurple, contentColor = Color.White),
                    modifier = Modifier.testTag("btn_cheer_buddy_${buddy.id}")
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cheer On", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StudyGroupCard(
    title: String,
    members: String,
    activity: String,
    isJoined: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                Text(members, fontSize = 11.sp, color = FocusCyan, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
                Text(activity, fontSize = 11.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 2.dp))
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isJoined) FocusEmerald.copy(alpha = 0.2f) else Color(0xFF1E293B)
            ) {
                Text(
                    text = if (isJoined) "Joined ✓" else "Join Guild",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isJoined) FocusEmerald else Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SendCheerDialog(
    buddy: StudyBuddyEntity,
    phrases: List<String>,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Cheer on ${buddy.name}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text("Select an encouragement vibe to send:", fontSize = 12.sp, color = Color(0xFF94A3B8), modifier = Modifier.padding(top = 4.dp))

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    phrases.forEach { phrase ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSend(phrase) }
                        ) {
                            Text(
                                text = phrase,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                }
            }
        }
    }
}

@Composable
private fun AddStudyBuddyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Calculus & Coding") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FocusCyan),
            modifier = Modifier.fillMaxWidth().testTag("dialog_add_buddy")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add Study Accountability Partner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Partner Name (e.g. Jordan Lee)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_buddy_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Shared Goal / Subject") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FocusCyan,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_buddy_subject")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF94A3B8)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name, subject)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FocusCyan, contentColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_buddy")
                    ) {
                        Text("Add Partner", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
