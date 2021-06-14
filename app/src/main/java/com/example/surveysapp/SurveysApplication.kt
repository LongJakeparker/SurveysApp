package com.example.surveysapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @author longtran
 * @since 13/06/2021
 */
@HiltAndroidApp
class SurveysApplication: Application() {
    companion object {
        lateinit var instance: SurveysApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}