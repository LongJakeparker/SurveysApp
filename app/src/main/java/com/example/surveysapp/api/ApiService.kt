package com.example.surveysapp.api

import com.example.surveysapp.BuildConfig
import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.other.ApiKey
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author longtran
 * @since 13/06/2021
 */
interface ApiService {
    @FormUrlEncoded
    @POST("api/v1/oauth/token")
    suspend fun login(@Field(ApiKey.EMAIL) email: String,
                      @Field(ApiKey.PASSWORD) password: String,
                      @Field(ApiKey.GRANT_TYPE) grantType: String? = "password",
                      @Field(ApiKey.CLIENT_ID) clientId: String? = BuildConfig.client_id,
                      @Field(ApiKey.CLIENT_SECRET) clientSecret: String? = BuildConfig.client_secret,
    ): Response<BaseEntity<AuthEntity>>
}