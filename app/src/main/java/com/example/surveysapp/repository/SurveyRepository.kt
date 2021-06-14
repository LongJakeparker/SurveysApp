package com.example.surveysapp.repository

import com.example.surveysapp.api.ApiService
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.BaseException
import com.example.surveysapp.entity.SurveyWrapperEntity
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class SurveyRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSurveyList(pageNumber: Int? = null, pageSize: Int? = null): BaseEntity<List<SurveyWrapperEntity>> {
        val response = apiService.getSurveyList(pageNumber, pageSize)

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }
}