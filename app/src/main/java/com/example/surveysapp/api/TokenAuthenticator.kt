package com.example.surveysapp.api

import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.mapper.AuthAttributesMapper
import com.example.surveysapp.other.Constant
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author longtran
 * @since 18/06/2021
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val nonAuthApiService: NonAuthApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) : Authenticator {
    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = sharedPreferencesManager.getAuthorization()

        if (response.request.header(Constant.AUTHORIZATION) != sharedPreferencesManager.getAuthorization()) {
            return response.request.newBuilder()
                .header(Constant.AUTHORIZATION, currentToken)
                .build()
        }

        val result =
            nonAuthApiService.refreshToken(sharedPreferencesManager.getRefreshToken()).execute()

        return if (result.isSuccessful) {
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