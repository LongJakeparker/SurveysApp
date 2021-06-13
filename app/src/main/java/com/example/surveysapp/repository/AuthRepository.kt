package com.example.surveysapp.repository

import com.example.surveysapp.api.ApiService
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
class AuthRepository @Inject constructor(
    private val apiService: ApiService
)