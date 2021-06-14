package com.example.surveysapp.model

/**
 * @author longtran
 * @since 13/06/2021
 */
data class AuthAttributes(
    val accessToken: String? = "",
    val tokenType: String? = "",
    val expiresIn: Long? = 0,
    val refreshToken: String? = "",
    val createdAt: Long? = 0
)
