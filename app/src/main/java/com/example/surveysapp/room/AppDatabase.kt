package com.example.surveysapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * @author longtran
 * @since 13/06/2021
 */
@Database(entities = [SurveyRoomEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun surveyDao(): SurveyDao

    companion object {
        const val DB_NAME = "app.db"
    }
}