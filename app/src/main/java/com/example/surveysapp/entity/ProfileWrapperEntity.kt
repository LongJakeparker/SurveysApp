package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 14/06/2021
 */
data class ProfileWrapperEntity(
    @SerializedName("id")
    val id: String? = "",

    @SerializedName("type")
    val type: String? = "",

    @SerializedName("attributes")
    val attributes: ProfileEntity? = null
)