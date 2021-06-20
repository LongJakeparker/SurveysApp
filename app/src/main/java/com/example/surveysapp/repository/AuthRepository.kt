package com.example.surveysapp.repository

import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.api.NonAuthApiService
import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.result
import com.example.surveysapp.entity.resultBoolean
import com.example.surveysapp.request.ForgotPasswordRequest
import com.example.surveysapp.request.LoginRequest
import com.example.surveysapp.request.LogoutRequest
import com.example.surveysapp.request.UserRequest
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class AuthRepository @Inject constructor(
    private val nonAuthApiService: NonAuthApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) {
    suspend fun login(email: String, password: String): BaseEntity<AuthEntity> {
        return nonAuthApiService.login(LoginRequest(email, password)).result()
    }

    suspend fun logout(): BaseEntity<Boolean> {
        return nonAuthApiService.logout(LogoutRequest(sharedPreferencesManager.getAccessToken()))
            .resultBoolean()
    }

    suspend fun forgotPassword(email: String): BaseEntity<Boolean> {
        return nonAuthApiService.forgotPassword(ForgotPasswordRequest(UserRequest(email)))
            .resultBoolean()
    }
}