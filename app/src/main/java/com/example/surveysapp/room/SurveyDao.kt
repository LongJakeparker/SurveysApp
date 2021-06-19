package com.example.surveysapp.room

import androidx.room.*

/**
 * @author longtran
 * @since 13/06/2021
 */
@Dao
interface SurveyDao {

    @Query("SELECT * FROM ${SurveyRoomEntity.TABLE_NAME}")
    suspend fun getAllSurveys(): List<SurveyRoomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSurveys(surveys: List<SurveyRoomEntity?>?)

    @Query("SELECT * FROM ${SurveyRoomEntity.TABLE_NAME} LIMIT 1")
    suspend fun getSurvey(): SurveyRoomEntity?

    @Transaction
    suspend fun insertSurveys(surveys: List<SurveyRoomEntity?>?, isLoadMore: Boolean = false){
        // If table is empty or loading more data then don't need to clear the table
        if (getSurvey() != null && !isLoadMore) {
            removeAll()
        }

        insertAllSurveys(surveys)
    }

    @Query("DELETE FROM ${SurveyRoomEntity.TABLE_NAME}")
    suspend fun removeAll()
}