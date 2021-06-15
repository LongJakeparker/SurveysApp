package com.example.surveysapp.api

import com.example.surveysapp.BuildConfig
import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.ProfileWrapperEntity
import com.example.surveysapp.entity.SurveyWrapperEntity
import com.example.surveysapp.other.ApiKey
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * @author longtran
 * @since 13/06/2021
 */
interface ApiService {
    @FormUrlEncoded
    @POST("api/v1/oauth/token")
    suspend fun login(
        @Field(ApiKey.EMAIL) email: String,
        @Field(ApiKey.PASSWORD) password: String,
        @Field(ApiKey.GRANT_TYPE) grantType: String? = "password",
        @Field(ApiKey.CLIENT_ID) clientId: String? = BuildConfig.client_id,
        @Field(ApiKey.CLIENT_SECRET) clientSecret: String? = BuildConfig.client_secret
    ): Response<BaseEntity<AuthEntity>>

    @FormUrlEncoded
    @POST("api/v1/oauth/token")
    fun refreshToken(
        @Field(ApiKey.REFRESH_TOKEN) refreshToken: String,
        @Field(ApiKey.GRANT_TYPE) grantType: String? = "refresh_token",
        @Field(ApiKey.CLIENT_ID) clientId: String? = BuildConfig.client_id,
        @Field(ApiKey.CLIENT_SECRET) clientSecret: String? = BuildConfig.client_secret
    ): Call<BaseEntity<AuthEntity>>

    @FormUrlEncoded
    @POST("api/v1/oauth/revoke")
    suspend fun logout(
        @Field(ApiKey.TOKEN) accessToken: String,
        @Field(ApiKey.CLIENT_ID) clientId: String? = BuildConfig.client_id,
        @Field(ApiKey.CLIENT_SECRET) clientSecret: String? = BuildConfig.client_secret
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("api/v1/passwords")
    suspend fun forgotPassword(
        @Field(ApiKey.USER) user: HashMap<String, String>,
        @Field(ApiKey.CLIENT_ID) clientId: String? = BuildConfig.client_id,
        @Field(ApiKey.CLIENT_SECRET) clientSecret: String? = BuildConfig.client_secret
    ): Response<ResponseBody>

    @GET("api/v1/surveys")
    suspend fun getSurveyList(
        @Query(ApiKey.PAGE_NUMBER) pageNumber: Int?,
        @Query(ApiKey.PAGE_SIZE) pageSize: Int?
    ): Response<BaseEntity<List<SurveyWrapperEntity>>>

    @GET("api/v1/me")
    suspend fun getProfile(): Response<BaseEntity<ProfileWrapperEntity>>
}