package com.example.surveysapp.entity

import com.example.surveysapp.model.Survey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 14/06/2021
 */
data class SurveyWrapperEntity(
    @SerializedName("id")
    val id: String? = "",

    @SerializedName("type")
    val type: String? = "",

    @SerializedName("attributes")
    val attributes: SurveyEntity? = null
)

/**
 * Extension converts from entity to Survey model
 */
fun SurveyWrapperEntity?.toModel() = (this?.run {
    Survey(
        id.orEmpty(),
        attributes?.title,
        attributes?.description,
        attributes?.coverImageUrl + "l"
    )
} ?: Survey())

/**
 * Extension converts from entity list to Survey model list
 */
fun List<SurveyWrapperEntity?>?.toModelList() = this?.map { surveyEntity -> surveyEntity.toModel() }
    ?: ArrayList()