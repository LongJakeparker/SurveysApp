package com.example.surveysapp.repository

import com.example.surveysapp.api.ApiService
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.BaseException
import com.example.surveysapp.entity.ProfileWrapperEntity
import com.example.surveysapp.entity.SurveyWrapperEntity
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProfile(): BaseEntity<ProfileWrapperEntity> {
        val response = apiService.getProfile()

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }
}