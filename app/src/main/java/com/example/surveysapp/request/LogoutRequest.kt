package com.example.surveysapp.request

import com.example.surveysapp.other.ApiKey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 20/06/2021
 */
data class LogoutRequest(
    @SerializedName(ApiKey.TOKEN)
    val accessToken: String
) : NonAuthRequest()