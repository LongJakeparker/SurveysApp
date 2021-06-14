package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 13/06/2021
 */
data class BaseEntity<T>(
    @SerializedName("data")
    val data: T? = null,
    val error: ErrorEntity? = null
)

data class BaseException(
    @SerializedName("errors")
    val errors: List<ErrorEntity>? = null
)