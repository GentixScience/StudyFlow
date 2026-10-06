package com.example.data.repository

import com.example.data.dao.AppDao
import com.example.data.model.AlarmEntity
import com.example.data.model.HabitEntity
import com.example.data.model.RewardEntity
import com.example.data.model.StudyBuddyEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WaterLogEntity
import kotlinx.coroutines.flow.Flow

class StudyFlowRepository(private val dao: AppDao) {
    val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val sessions: Flow<List<StudySessionEntity>> = dao.getAllSessions()
    val habits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val allWaterLogs: Flow<List<WaterLogEntity>> = dao.getAllWaterLogs()
    val alarms: Flow<List<AlarmEntity>> = dao.getAllAlarms()
    val rewards: Flow<List<RewardEntity>> = dao.getAllRewards()
    val profile: Flow<UserProfileEntity?> = dao.getProfile()
    val buddies: Flow<List<StudyBuddyEntity>> = dao.getAllBuddies()

    fun getTodayWaterLogs(dateString: String): Flow<List<WaterLogEntity>> =
        dao.getWaterLogsByDate(dateString)

    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

    suspend fun insertSession(session: StudySessionEntity) = dao.insertSession(session)
    suspend fun deleteSession(session: StudySessionEntity) = dao.deleteSession(session)

    suspend fun insertHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)

    suspend fun insertWaterLog(log: WaterLogEntity) = dao.insertWaterLog(log)
    suspend fun deleteWaterLog(log: WaterLogEntity) = dao.deleteWaterLog(log)

    suspend fun insertAlarm(alarm: AlarmEntity) = dao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = dao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = dao.deleteAlarm(alarm)

    suspend fun insertReward(reward: RewardEntity) = dao.insertReward(reward)
    suspend fun updateReward(reward: RewardEntity) = dao.updateReward(reward)
    suspend fun deleteReward(reward: RewardEntity) = dao.deleteReward(reward)

    suspend fun updateProfile(profile: UserProfileEntity) = dao.insertProfile(profile)

    suspend fun insertBuddy(buddy: StudyBuddyEntity) = dao.insertBuddy(buddy)
    suspend fun updateBuddy(buddy: StudyBuddyEntity) = dao.updateBuddy(buddy)
    suspend fun deleteBuddy(buddy: StudyBuddyEntity) = dao.deleteBuddy(buddy)
}
