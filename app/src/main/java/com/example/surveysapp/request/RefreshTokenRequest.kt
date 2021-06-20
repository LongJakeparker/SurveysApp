package com.example.surveysapp.request

import com.example.surveysapp.other.ApiKey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 20/06/2021
 */
data class RefreshTokenRequest(
    @SerializedName(ApiKey.REFRESH_TOKEN)
    val refreshToken: String,
    @SerializedName(ApiKey.GRANT_TYPE)
    val grantType: String = "refresh_token"
) : NonAuthRequest()
