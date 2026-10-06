package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppDao
import com.example.data.model.AlarmEntity
import com.example.data.model.HabitEntity
import com.example.data.model.RewardEntity
import com.example.data.model.StudyBuddyEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WaterLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        StudySessionEntity::class,
        HabitEntity::class,
        WaterLogEntity::class,
        AlarmEntity::class,
        RewardEntity::class,
        UserProfileEntity::class,
        StudyBuddyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studyflow_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.appDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: AppDao) {
                // Profile
                dao.insertProfile(
                    UserProfileEntity(
                        id = 1,
                        totalXp = 320,
                        level = 3,
                        dailyStreak = 5,
                        dailyWaterGoalMl = 2500,
                        dailyStudyGoalMinutes = 180
                    )
                )

                // Starter Tasks
                dao.insertTask(
                    TaskEntity(
                        title = "Revise Calculus Chapter 4 Integrals",
                        description = "Solve problem set 4.1 to 4.5 and review theorem proofs",
                        category = "Study",
                        priority = "HIGH",
                        dueDateTime = "Today, 4:00 PM",
                        xpReward = 25
                    )
                )
                dao.insertTask(
                    TaskEntity(
                        title = "Finish Data Structures Assignment",
                        description = "Implement Binary Search Tree balancing in Kotlin",
                        category = "Assignment",
                        priority = "HIGH",
                        dueDateTime = "Today, 8:00 PM",
                        xpReward = 30
                    )
                )
                dao.insertTask(
                    TaskEntity(
                        title = "Read Physics Modern Mechanics Notes",
                        description = "Review lecture slides on Wave Optics",
                        category = "Study",
                        priority = "MEDIUM",
                        dueDateTime = "Tomorrow, 10:00 AM",
                        xpReward = 20
                    )
                )
                dao.insertTask(
                    TaskEntity(
                        title = "Submit Literature Essay Draft",
                        description = "Check citations and formatting before submission",
                        category = "Assignment",
                        priority = "LOW",
                        dueDateTime = "Tomorrow, 6:00 PM",
                        xpReward = 15
                    )
                )

                // Starter Habits / Routines
                dao.insertHabit(
                    HabitEntity(
                        title = "Morning Mindset & No Phone (30 min)",
                        category = "Morning",
                        streakDays = 5,
                        isCompletedToday = true,
                        xpReward = 20,
                        iconName = "alarm"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "Review Flashcards / Key Formulas",
                        category = "Study",
                        streakDays = 4,
                        isCompletedToday = false,
                        xpReward = 25,
                        iconName = "school"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "Drink 1 Full Glass of Water at Dawn",
                        category = "Health",
                        streakDays = 7,
                        isCompletedToday = true,
                        xpReward = 15,
                        iconName = "water_drop"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "Deep Focus Study Session (50 min)",
                        category = "Study",
                        streakDays = 3,
                        isCompletedToday = false,
                        xpReward = 30,
                        iconName = "timer"
                    )
                )
                dao.insertHabit(
                    HabitEntity(
                        title = "Nightly Desk Cleanup & Next Day Prep",
                        category = "Night",
                        streakDays = 6,
                        isCompletedToday = false,
                        xpReward = 20,
                        iconName = "bedtime"
                    )
                )

                // Starter Study Sessions
                val now = System.currentTimeMillis()
                val oneDay = 86400000L
                dao.insertSession(
                    StudySessionEntity(
                        subject = "Mathematics",
                        durationMinutes = 60,
                        timestamp = now - 3600000L,
                        notes = "Deep work on Integration by parts",
                        xpEarned = 60
                    )
                )
                dao.insertSession(
                    StudySessionEntity(
                        subject = "Computer Science",
                        durationMinutes = 45,
                        timestamp = now - (oneDay + 7200000L),
                        notes = "Graph traversal algorithms",
                        xpEarned = 45
                    )
                )
                dao.insertSession(
                    StudySessionEntity(
                        subject = "Physics",
                        durationMinutes = 50,
                        timestamp = now - (oneDay * 2 + 5000000L),
                        notes = "Electromagnetism chapter review",
                        xpEarned = 50
                    )
                )
                dao.insertSession(
                    StudySessionEntity(
                        subject = "Literature",
                        durationMinutes = 30,
                        timestamp = now - (oneDay * 3 + 3000000L),
                        notes = "Essay structure planning",
                        xpEarned = 30
                    )
                )

                // Water Logs for today
                val dateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                dao.insertWaterLog(WaterLogEntity(amountMl = 350, timestamp = now - 14400000L, dateString = dateString))
                dao.insertWaterLog(WaterLogEntity(amountMl = 250, timestamp = now - 7200000L, dateString = dateString))
                dao.insertWaterLog(WaterLogEntity(amountMl = 500, timestamp = now - 1800000L, dateString = dateString))

                // Alarms
                dao.insertAlarm(
                    AlarmEntity(
                        hour = 6,
                        minute = 30,
                        label = "Rise & Shine Student Hustle",
                        isEnabled = true,
                        repeatDays = "Mon, Tue, Wed, Thu, Fri"
                    )
                )
                dao.insertAlarm(
                    AlarmEntity(
                        hour = 10,
                        minute = 0,
                        label = "Hydration & Posture Reset",
                        isEnabled = true,
                        repeatDays = "Every day",
                        isWaterAlarm = true
                    )
                )
                dao.insertAlarm(
                    AlarmEntity(
                        hour = 15,
                        minute = 30,
                        label = "Afternoon Study Power Hour",
                        isEnabled = true,
                        repeatDays = "Mon, Tue, Wed, Thu, Fri"
                    )
                )
                dao.insertAlarm(
                    AlarmEntity(
                        hour = 22,
                        minute = 30,
                        label = "Digital Sunset & Wind Down",
                        isEnabled = false,
                        repeatDays = "Every day"
                    )
                )

                // Rewards
                dao.insertReward(RewardEntity(title = "1-Hour Gaming Session", costPoints = 150, icon = "sports_esports"))
                dao.insertReward(RewardEntity(title = "Watch Anime / Netflix Episode", costPoints = 200, icon = "movie"))
                dao.insertReward(RewardEntity(title = "Special Boba / Iced Coffee Treat", costPoints = 120, icon = "local_cafe"))
                dao.insertReward(RewardEntity(title = "Guilt-Free Guiltless Nap (30m)", costPoints = 100, icon = "bed"))
                dao.insertReward(RewardEntity(title = "Order Favorite Takeout Dinner", costPoints = 350, icon = "restaurant"))

                // Study Buddies & Accountability Partners
                dao.insertBuddy(
                    StudyBuddyEntity(
                        name = "Alex Chen",
                        avatarInitials = "AC",
                        statusMessage = "Grinding Calculus integrals! 🔥",
                        currentStreak = 7,
                        isStudyingNow = true,
                        subjectNow = "Mathematics",
                        cheerCount = 14
                    )
                )
                dao.insertBuddy(
                    StudyBuddyEntity(
                        name = "Sarah Miller",
                        avatarInitials = "SM",
                        statusMessage = "Coding binary search trees 💻",
                        currentStreak = 12,
                        isStudyingNow = true,
                        subjectNow = "Computer Science",
                        cheerCount = 28
                    )
                )
                dao.insertBuddy(
                    StudyBuddyEntity(
                        name = "Marcus Vance",
                        avatarInitials = "MV",
                        statusMessage = "Organic Chemistry flashcards done!",
                        currentStreak = 9,
                        isStudyingNow = false,
                        subjectNow = "Chemistry",
                        cheerCount = 19
                    )
                )
                dao.insertBuddy(
                    StudyBuddyEntity(
                        name = "Elena Rostova",
                        avatarInitials = "ER",
                        statusMessage = "Hit 2500ml water goal & morning routine ✨",
                        currentStreak = 5,
                        isStudyingNow = false,
                        subjectNow = "Biology",
                        cheerCount = 11
                    )
                )
            }
        }
    }
}
