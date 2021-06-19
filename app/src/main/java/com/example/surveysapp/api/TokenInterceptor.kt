package com.example.surveysapp.api

import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.mapper.AuthAttributesMapper
import com.example.surveysapp.other.Constant
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author longtran
 * @since 18/06/2021
 */
@Singleton
class TokenInterceptor @Inject constructor(
    private val nonAuthApiService: NonAuthApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        var request = original.newBuilder()
            .header(Constant.AUTHORIZATION, sharedPreferencesManager.getAuthorization())
            .build()

        // get expire time from shared preferences
        val expireTime: Long = sharedPreferencesManager.getExpireTime()
        if (expireTime > 0) {
            val calendar = Calendar.getInstance()
            val nowDate = calendar.time
            calendar.timeInMillis = expireTime
            val expireDate: Date = calendar.time

            val compareDateResult = expireDate.compareTo(nowDate)

            if (compareDateResult == -1) { // Token has expired
                val result =
                    nonAuthApiService.refreshToken(sharedPreferencesManager.getRefreshToken())
                        .execute()
                if (result.isSuccessful) {
                    // refresh token is successful, we saved new token to storage.
                    // Get token from storage and set header
                    result.body()?.data?.attributes?.let {
                        sharedPreferencesManager.putSignInData(AuthAttributesMapper.transform(it))
                    }

                    // execute failed request again with new access token
                    request = original.newBuilder()
                        .header(
                            Constant.AUTHORIZATION,
                            sharedPreferencesManager.getAuthorization()
                        )
                        .build()
                }
            }
        }

        return chain.proceed(request)
    }
}