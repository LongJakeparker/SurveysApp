package com.example.surveysapp.api

import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.ProfileWrapperEntity
import com.example.surveysapp.entity.SurveyWrapperEntity
import com.example.surveysapp.other.ApiKey
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author longtran
 * @since 13/06/2021
 */
interface AuthApiService {
    @GET("api/v1/surveys")
    suspend fun getSurveyList(
        @Query(ApiKey.PAGE_NUMBER) pageNumber: Int?,
        @Query(ApiKey.PAGE_SIZE) pageSize: Int?
    ): Response<BaseEntity<List<SurveyWrapperEntity>>>

    @GET("api/v1/me")
    suspend fun getProfile(): Response<BaseEntity<ProfileWrapperEntity>>
}