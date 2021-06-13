package com.example.surveysapp.di

import com.example.surveysapp.BuildConfig
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.api.ApiService
import com.example.surveysapp.other.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author longtran
 * @since 13/06/2021
 */
@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Provides
    fun provideBaseUrl() = Constant.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(sharedPreferencesManager: SharedPreferencesManager) = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header(Constant.AUTHORIZATION, sharedPreferencesManager.getAuthorization())
                .method(original.method, original.body)
                .build()
            val response = chain.proceed(request)
            // todo deal with the issues the way you need to
            response
        }
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            addInterceptor(logging)
        }
    }.build()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)
}