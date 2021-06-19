package com.example.surveysapp.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surveysapp.model.Survey
import com.example.surveysapp.room.SurveyRoomEntity.Companion.TABLE_NAME

/**
 * @author longtran
 * @since 13/06/2021
 */
@Entity(tableName = TABLE_NAME)
data class SurveyRoomEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "title")
    val title: String? = "",
    @ColumnInfo(name = "description")
    val description: String? = "",
    @ColumnInfo(name = "coverImageUrl")
    val coverImageUrl: String? = ""
) {
    companion object {
        const val TABLE_NAME = "survey"
    }
}

/**
 * Extension converts from Room entity to Survey model
 */
fun SurveyRoomEntity?.toModel() = (this?.run {
    Survey(
        id,
        title,
        description,
        coverImageUrl
    )
} ?: Survey())

/**
 * Extension converts from Room entity list to Survey model list
 */
fun List<SurveyRoomEntity?>?.toModelList() = this?.map { surveyEntity -> surveyEntity.toModel() }
    ?: ArrayList()