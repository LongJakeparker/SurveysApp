package com.example.surveysapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surveysapp.room.SurveyRoomEntity.Companion.TABLE_NAME

/**
 * @author longtran
 * @since 13/06/2021
 */
@Entity(tableName = TABLE_NAME)
data class SurveyRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = ""
) {
    companion object {
        const val TABLE_NAME = "survey"
    }
}