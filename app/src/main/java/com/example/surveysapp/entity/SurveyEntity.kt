package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 14/06/2021
 */
data class SurveyEntity(
    @SerializedName("title")
    val title: String? = "",

    @SerializedName("description")
    val description: String? = "",

    @SerializedName("cover_image_url")
    val coverImageUrl: String? = ""
)
