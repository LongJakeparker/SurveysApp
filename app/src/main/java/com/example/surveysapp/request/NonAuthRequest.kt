package com.example.surveysapp.request

import com.example.surveysapp.BuildConfig
import com.example.surveysapp.other.ApiKey
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 20/06/2021
 */
open class NonAuthRequest(
    @SerializedName(ApiKey.CLIENT_ID)
    val clientId: String? = BuildConfig.client_id,
    @SerializedName(ApiKey.CLIENT_SECRET)
    val clientSecret: String? = BuildConfig.client_secret
)
