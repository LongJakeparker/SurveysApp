package com.example.surveysapp.model

import com.example.surveysapp.room.SurveyRoomEntity

/**
 * @author longtran
 * @since 14/06/2021
 */
data class Survey(
    val id: String = "",
    val title: String? = "",
    val description: String? = "",
    val coverImageUrl: String? = ""
)

/**
 * Extension converts from model to Survey Room entity
 */
fun Survey?.toRoomEntity() = (this?.run {
    SurveyRoomEntity(
        id,
        title,
        description,
        coverImageUrl
    )
} ?: SurveyRoomEntity())

/**
 * Extension converts from model list to Survey Room entity list
 */
fun List<Survey?>?.toRoomEntityList() = this?.map { survey -> survey.toRoomEntity() }
    ?: ArrayList()
