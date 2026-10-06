package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Study", // Study, Assignment, Exam Prep, Routine, Personal
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val dueDateTime: String = "Today",
    val isCompleted: Boolean = false,
    val completedTimestamp: Long = 0L,
    val xpReward: Int = 15
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val xpEarned: Int = 25
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String = "Daily", // Morning, Night, Study, Health
    val streakDays: Int = 0,
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String = "",
    val xpReward: Int = 20,
    val iconName: String = "bolt"
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String,
    val isEnabled: Boolean = true,
    val repeatDays: String = "Mon, Tue, Wed, Thu, Fri",
    val isWaterAlarm: Boolean = false
)

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val costPoints: Int,
    val icon: String = "star",
    val isRedeemed: Boolean = false,
    val redeemedDate: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalXp: Int = 180,
    val level: Int = 2,
    val dailyStreak: Int = 4,
    val dailyWaterGoalMl: Int = 2500,
    val dailyStudyGoalMinutes: Int = 150
)

@Entity(tableName = "study_buddies")
data class StudyBuddyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarInitials: String,
    val statusMessage: String,
    val currentStreak: Int = 5,
    val isStudyingNow: Boolean = false,
    val subjectNow: String = "",
    val cheerCount: Int = 0
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val isUnlocked: Boolean,
    val progress: Float = 1.0f
)
