package com.example.surveysapp.entity

import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 14/06/2021
 */
data class ProfileEntity(
    @SerializedName("email")
    val email: String? = "",

    @SerializedName("avatar_url")
    val avatarUrl: String? = "",
)