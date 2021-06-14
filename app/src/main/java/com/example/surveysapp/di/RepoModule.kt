package com.example.surveysapp.di

import com.example.surveysapp.BuildConfig
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.api.ApiService
import com.example.surveysapp.mapper.AuthAttributesMapper
import com.example.surveysapp.other.ApiServiceHolder
import com.example.surveysapp.other.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
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
    fun provideTokenAuthenticator(
        apiServiceHolder: ApiServiceHolder,
        sharedPreferencesManager: SharedPreferencesManager
    ) = object : Authenticator {
        @Synchronized
        override fun authenticate(route: Route?, response: Response): Request? {

            val currentToken = sharedPreferencesManager.getAuthorization()

            if (response.request.header(Constant.AUTHORIZATION) != sharedPreferencesManager.getAuthorization()) {
                return response.request.newBuilder()
                    .header(Constant.AUTHORIZATION, currentToken)
                    .build()
            }

            val result =
                apiServiceHolder.apiService?.refreshToken(sharedPreferencesManager.getRefreshToken())
                    ?.execute()

            return if (result!!.isSuccessful) {
                // refresh token is successful, we saved new token to storage.
                // Get your token from storage and set header
                val newAccessToken = result.body()?.data?.attributes?.run {
                    sharedPreferencesManager.putSignInData(AuthAttributesMapper.transform(this))
                    return@run accessToken
                }

                // execute failed request again with new access token
                response.request.newBuilder()
                    .header(Constant.AUTHORIZATION, newAccessToken ?: "")
                    .build()
            } else {
                // Logout user
                null
            }
        }
    }

    @Singleton
    @Provides
    fun provideTokenInterceptor(
        apiServiceHolder: ApiServiceHolder,
        sharedPreferencesManager: SharedPreferencesManager
    ) = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            var request = original.newBuilder()
                .header(Constant.AUTHORIZATION, sharedPreferencesManager.getAuthorization())
                .build()

            // get expire time from shared preferences
            val expireTime: Long = sharedPreferencesManager.getExpireTime()
            val calendar = Calendar.getInstance()
            val nowDate = calendar.time
            calendar.timeInMillis = expireTime
            val expireDate: Date = calendar.time

            val compareDateResult = nowDate.compareTo(expireDate)

            if (compareDateResult == -1) {
                val result =
                    apiServiceHolder.apiService?.refreshToken(sharedPreferencesManager.getRefreshToken())
                        ?.execute()
                if (result!!.isSuccessful) {
                    // refresh token is successful, we saved new token to storage.
                    // Get your token from storage and set header
                    val newAccessToken = result.body()?.data?.attributes?.run {
                        sharedPreferencesManager.putSignInData(AuthAttributesMapper.transform(this))
                        return@run accessToken
                    }

                    // execute failed request again with new access token
                    request = original.newBuilder()
                        .header(Constant.AUTHORIZATION, newAccessToken ?: "")
                        .build()
                }
            }

            return chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(tokenAuthenticator: Authenticator, tokenInterceptor: Interceptor) =
        OkHttpClient.Builder().apply {
            authenticator(tokenAuthenticator)
            addInterceptor(tokenInterceptor)
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
    fun provideApiService(retrofit: Retrofit, apiServiceHolder: ApiServiceHolder): ApiService {
        val apiService = retrofit.create(ApiService::class.java)
        apiServiceHolder.apiService = apiService
        return apiService
    }
}