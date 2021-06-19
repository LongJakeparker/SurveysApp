package com.example.surveysapp.entity

import com.example.surveysapp.model.AuthAttributes
import com.google.gson.annotations.SerializedName

/**
 * An entity handle attributes response from auth api
 * @author longtran
 * @since 13/06/2021
 */
data class AuthAttributesEntity(
    @SerializedName("access_token")
    val accessToken: String? = "",

    @SerializedName("token_type")
    val tokenType: String? = "",

    @SerializedName("expires_in")
    val expiresIn: Long? = 0,

    @SerializedName("refresh_token")
    val refreshToken: String? = "",

    @SerializedName("created_at")
    val createdAt: Long? = 0
)

/**
 * Extension converts from entity to AuthAttributes model
 */
fun AuthAttributesEntity?.toModel() = (this?.run {
    AuthAttributes(
        accessToken,
        tokenType,
        expiresIn,
        refreshToken,
        createdAt
    )
} ?: AuthAttributes())
