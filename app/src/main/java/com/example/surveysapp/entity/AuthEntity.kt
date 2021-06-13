package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * A root entity of auth api
 * @author longtran
 * @since 13/06/2021
 */
data class AuthEntity(
    @SerializedName("id")
    val id: Int? = 0,

    @SerializedName("type")
    val type: String? = "",

    @SerializedName("attributes")
    val attributes: AuthAttributesEntity? = null
)