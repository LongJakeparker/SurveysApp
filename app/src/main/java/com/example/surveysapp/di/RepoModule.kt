package com.example.surveysapp.di

import android.content.Context
import androidx.room.Room
import com.example.surveysapp.BuildConfig
import com.example.surveysapp.api.AuthApiService
import com.example.surveysapp.api.NonAuthApiService
import com.example.surveysapp.api.TokenAuthenticator
import com.example.surveysapp.api.TokenInterceptor
import com.example.surveysapp.room.AppDatabase
import com.example.surveysapp.room.SurveyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * @author longtran
 * @since 13/06/2021
 */
@Module
@InstallIn(SingletonComponent::class)
object RepoModule {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NonAuthOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NonAuthRetrofit

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @AuthOkHttpClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TokenInterceptor
    ) =
        OkHttpClient.Builder().apply {
            authenticator(tokenAuthenticator)
            addInterceptor(tokenInterceptor)
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                readTimeout(60, TimeUnit.SECONDS)
                connectTimeout(60, TimeUnit.SECONDS)
                addInterceptor(logging)
            }
        }.build()

    @NonAuthOkHttpClient
    @Singleton
    @Provides
    fun provideNonAuthOkHttpClient() =
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                readTimeout(60, TimeUnit.SECONDS)
                connectTimeout(60, TimeUnit.SECONDS)
                addInterceptor(logging)
            }
        }.build()

    @AuthRetrofit
    @Singleton
    @Provides
    fun provideAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @NonAuthRetrofit
    @Singleton
    @Provides
    fun provideNonAuthRetrofit(
        @NonAuthOkHttpClient okHttpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNonAuthApiService(@NonAuthRetrofit retrofit: Retrofit): NonAuthApiService {
        return retrofit.create(NonAuthApiService::class.java)
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