package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AlarmEntity
import com.example.data.model.HabitEntity
import com.example.data.model.RewardEntity
import com.example.data.model.StudyBuddyEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WaterLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Study Sessions
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    // Habits
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    // Water Logs
    @Query("SELECT * FROM water_logs WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getWaterLogsByDate(dateString: String): Flow<List<WaterLogEntity>>

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLogEntity): Long

    @Delete
    suspend fun deleteWaterLog(log: WaterLogEntity)

    // Alarms
    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    // Rewards
    @Query("SELECT * FROM rewards ORDER BY isRedeemed ASC, costPoints ASC")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity): Long

    @Update
    suspend fun updateReward(reward: RewardEntity)

    @Delete
    suspend fun deleteReward(reward: RewardEntity)

    // Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    // Study Buddies
    @Query("SELECT * FROM study_buddies ORDER BY isStudyingNow DESC, cheerCount DESC")
    fun getAllBuddies(): Flow<List<StudyBuddyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuddy(buddy: StudyBuddyEntity): Long

    @Update
    suspend fun updateBuddy(buddy: StudyBuddyEntity)

    @Delete
    suspend fun deleteBuddy(buddy: StudyBuddyEntity)
}
