package com.example.surveysapp.repository

import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.api.ApiService
import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.BaseException
import com.example.surveysapp.other.ApiKey
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    suspend fun login(email: String, password: String): BaseEntity<AuthEntity> {
        val response = apiService.login(email = email, password = password)

        return if (response.isSuccessful) {
            response.body()!!
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }

    suspend fun logout(): BaseEntity<Boolean> {
        val response = apiService.logout(sharedPreferencesManager.getAccessToken())

        return if (response.isSuccessful) {
            BaseEntity(true, null)
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }

    suspend fun forgotPassword(email: String): BaseEntity<Boolean> {
        val response = apiService.forgotPassword(HashMap<String, String>().apply {
            put(ApiKey.EMAIL, email)
        })

        return if (response.isSuccessful) {
            BaseEntity(true, null)
        } else {
            // Convert errorBody to ErrorEntity for shorter access
            val baseException = Gson().fromJson(response.errorBody()?.string(), BaseException::class.java)
            BaseEntity(null, baseException.errors?.get(0))
        }
    }
}