package com.example.surveysapp.api

import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.request.ForgotPasswordRequest
import com.example.surveysapp.request.LoginRequest
import com.example.surveysapp.request.LogoutRequest
import com.example.surveysapp.request.RefreshTokenRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author longtran
 * @since 13/06/2021
 */
interface NonAuthApiService {
    @POST("api/v1/oauth/token")
    suspend fun login(@Body body: LoginRequest): Response<BaseEntity<AuthEntity>>

    @POST("api/v1/oauth/token")
    fun refreshToken(@Body body: RefreshTokenRequest): Call<BaseEntity<AuthEntity>>

    @POST("api/v1/oauth/revoke")
    suspend fun logout(@Body body: LogoutRequest): Response<ResponseBody>

    @POST("api/v1/passwords")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<ResponseBody>
}