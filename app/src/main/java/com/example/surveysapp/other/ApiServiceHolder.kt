package com.example.surveysapp.other

import com.example.surveysapp.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author longtran
 * @since 14/06/2021
 */
@Singleton
class ApiServiceHolder @Inject constructor() {
    var apiService: ApiService? = null
}