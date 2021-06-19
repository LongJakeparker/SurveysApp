package com.example.surveysapp.repository

import com.example.surveysapp.api.AuthApiService
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.BaseException
import com.example.surveysapp.entity.SurveyWrapperEntity
import com.example.surveysapp.room.SurveyDao
import com.example.surveysapp.room.SurveyRoomEntity
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class SurveyRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val surveyDao: SurveyDao
) {
    /**
     * Queries survey list
     * @param pageNumber
     * @param pageSize
     */
    suspend fun getSurveyList(pageNumber: Int? = null, pageSize: Int? = null): BaseEntity<List<SurveyWrapperEntity>> {
        val response = authApiService.getSurveyList(pageNumber, pageSize)

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }

    /**
     * Queries all surveys from Room database
     * @return List<IssueEntity>
     */
    suspend fun getLocalSurveys(): List<SurveyRoomEntity> {
        return surveyDao.getAllSurveys()
    }

    /**
     * Inserts all surveys into Room database
     * @param issues
     */
    suspend fun insertSurveys(issues: List<SurveyRoomEntity?>?) {
        return surveyDao.insertSurveys(issues)
    }

    /**
     * Removes all surveys in Room database
     */
    suspend fun removeSurveys() {
        return surveyDao.removeAll()
    }
}