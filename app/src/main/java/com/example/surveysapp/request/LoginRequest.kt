package com.example.surveysapp.request

import com.example.surveysapp.other.ApiKey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 20/06/2021
 */
data class LoginRequest(
    @SerializedName(ApiKey.EMAIL)
    val email: String,
    @SerializedName(ApiKey.PASSWORD)
    val password: String,
    @SerializedName(ApiKey.GRANT_TYPE)
    val grantType: String = "password"
) : NonAuthRequest()
