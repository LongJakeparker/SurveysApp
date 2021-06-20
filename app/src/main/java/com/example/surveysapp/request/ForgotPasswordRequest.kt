package com.example.surveysapp.request

import com.example.surveysapp.other.ApiKey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 20/06/2021
 */
data class ForgotPasswordRequest(
    @SerializedName(ApiKey.USER)
    val user: UserRequest,
) : NonAuthRequest()

data class UserRequest(
    @SerializedName(ApiKey.EMAIL)
    val email: String,
)