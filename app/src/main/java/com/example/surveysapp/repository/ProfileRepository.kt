package com.example.surveysapp.repository

import com.example.surveysapp.api.AuthApiService
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.ProfileWrapperEntity
import com.example.surveysapp.entity.result
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class ProfileRepository @Inject constructor(
    private val authApiService: AuthApiService
) {
    suspend fun getProfile(): BaseEntity<ProfileWrapperEntity> {
        return authApiService.getProfile().result()
    }
}