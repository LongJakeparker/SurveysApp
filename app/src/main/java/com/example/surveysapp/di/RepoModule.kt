package com.example.surveysapp.di

import android.content.Context
import android.net.Uri
import androidx.room.Room
import com.example.surveysapp.BuildConfig
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.api.ApiService
import com.example.surveysapp.mapper.AuthAttributesMapper
import com.example.surveysapp.other.ApiServiceHolder
import com.example.surveysapp.other.Constant
import com.example.surveysapp.room.AppDatabase
import com.example.surveysapp.room.SurveyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
                // Get token from storage and set header
                result.body()?.data?.attributes?.let {
                    sharedPreferencesManager.putSignInData(AuthAttributesMapper.transform(it))
                }

                // execute failed request again with new access token
                response.request.newBuilder()
                    .header(Constant.AUTHORIZATION, sharedPreferencesManager.getAuthorization())
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

            // Only check if this request is not for login or refresh token
            if (Uri.parse(original.url.toString()).lastPathSegment != Constant.URL_SEGMENT_REFRESH_TOKEN) {
                // get expire time from shared preferences
                val expireTime: Long = sharedPreferencesManager.getExpireTime()
                val calendar = Calendar.getInstance()
                val nowDate = calendar.time
                calendar.timeInMillis = expireTime
                val expireDate: Date = calendar.time

                val compareDateResult = expireDate.compareTo(nowDate)

                if (compareDateResult == -1) { // Token has expired
                    val result =
                        apiServiceHolder.apiService?.refreshToken(sharedPreferencesManager.getRefreshToken())
                            ?.execute()
                    if (result!!.isSuccessful) {
                        // refresh token is successful, we saved new token to storage.
                        // Get token from storage and set header
                        result.body()?.data?.attributes?.let {
                            sharedPreferencesManager.putSignInData(AuthAttributesMapper.transform(it))
                        }

                        // execute failed request again with new access token
                        request = original.newBuilder()
                            .header(Constant.AUTHORIZATION, sharedPreferencesManager.getAuthorization())
                            .build()
                    }
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DB_NAME
        ).build()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): SurveyDao {
        return appDatabase.surveyDao()
    }
}