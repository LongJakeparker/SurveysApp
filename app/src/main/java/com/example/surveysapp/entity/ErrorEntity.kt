package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 13/06/2021
 */
data class ErrorEntity(
    @SerializedName("detail")
    val message: String? = null,

    @SerializedName("code")
    val code: String? = null,
)