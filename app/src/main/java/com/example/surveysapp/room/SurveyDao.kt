package com.example.surveysapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * @author longtran
 * @since 13/06/2021
 */
@Dao
interface SurveyDao {
    @Query("SELECT * FROM ${SurveyRoomEntity.TABLE_NAME}")
    suspend fun getAllIssues(): List<SurveyRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIssues(surveys: List<SurveyRoomEntity?>?)

    @Query("DELETE FROM ${SurveyRoomEntity.TABLE_NAME}")
    suspend fun removeAll()
}