package com.example.surveysapp

import android.content.Context
import com.example.surveysapp.model.AuthAttributes
import com.example.surveysapp.util.AESCrypt
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author longtran
 * @since 13/06/2021
 */
@Singleton
class SharedPreferencesManager @Inject constructor(@ApplicationContext context : Context) {
    companion object {
        private const val PREF_NAME = BuildConfig.APPLICATION_ID + "app.local.Preferences"
        private const val TOKEN_CRYPT_KEY = "TOKEN_CRYPT_KEY_" // length must be 16, 24 or 32
        private const val PREF_KEY_TOKEN_TYPE = "pref_key_token_type"
        private const val PREF_KEY_ENCRYPTED_ACCESS_TOKEN = "pref_key_encrypted_access_token"
        private const val PREF_KEY_ENCRYPTED_REFRESH_TOKEN = "pref_key_encrypted_refresh_token"
        private const val PREF_KEY_EXPIRED_TIME = "pref_key_expired_time"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Puts user's sign in data
     * Puts:
     *  - token type
     *  - access token
     *  - refresh token
     * @param authAttributes
     */
    fun putSignInData(authAttributes: AuthAttributes) {
        authAttributes.tokenType?.let { putTokenType(it) }
        authAttributes.accessToken?.let { putToken(PREF_KEY_ENCRYPTED_ACCESS_TOKEN, it) }
        authAttributes.refreshToken?.let { putToken(PREF_KEY_ENCRYPTED_REFRESH_TOKEN, it) }
        authAttributes.expiresIn?.let { putExpireTime(it) }
    }

    /**
     * Clears user's sign in data
     * Clears:
     *  - token type
     *  - access token
     *  - refresh token
     */
    fun clearSignInData() {
        removeToken(PREF_KEY_ENCRYPTED_ACCESS_TOKEN)
        removeToken(PREF_KEY_ENCRYPTED_REFRESH_TOKEN)
        removeTokenType()
    }

    /**
     * Gets authorization string {{user_token_type}} {{user_access_token}}
     */
    fun getAuthorization(): String {
        return "${getTokenType()} ${getAccessToken()}"
    }

    /**
     * Puts token type
     * @param tokenType
     */
    private fun putTokenType(tokenType: String) {
        val editor = prefs.edit()
        editor.putString(PREF_KEY_TOKEN_TYPE, tokenType)
        editor.apply()
    }

    /**
     * Gets token type
     */
    private fun getTokenType(): String? {
        return prefs.getString(PREF_KEY_TOKEN_TYPE, "")
    }

    /**
     * Puts expired time
     * @param expiredTime
     */
    private fun putExpireTime(expiredTime: Long) {
        val editor = prefs.edit()
        editor.putLong(PREF_KEY_EXPIRED_TIME, expiredTime)
        editor.apply()
    }

    /**
     * Gets expired time
     */
    fun getExpireTime(): Long {
        return prefs.getLong(PREF_KEY_EXPIRED_TIME, 0)
    }

    /**
     * Removes token type
     */
    private fun removeTokenType() {
        val editor = prefs.edit()
        editor.remove(PREF_KEY_TOKEN_TYPE)
        editor.apply()
    }

    /**
     * Encrypts and puts user's sensitive token
     * @param key
     * @param token
     */
    private fun putToken(key: String, token: String) {
        val editor = prefs.edit()
        editor.putString(key, AESCrypt.encrypt(token, TOKEN_CRYPT_KEY))
        editor.apply()
    }

    /**
     * Removes user's sensitive token
     * @param key
     */
    private fun removeToken(key: String) {
        val editor = prefs.edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * Gets and decrypts user's access token
     * @return String
     */
    fun getAccessToken(): String {
        val encryptedToken = prefs.getString(PREF_KEY_ENCRYPTED_ACCESS_TOKEN, "")
        return AESCrypt.decrypt(encryptedToken ?: "", TOKEN_CRYPT_KEY)
    }

    /**
     * Gets and decrypts user's refresh token
     * @return String
     */
    fun getRefreshToken(): String {
        val encryptedToken = prefs.getString(PREF_KEY_ENCRYPTED_REFRESH_TOKEN, "")
        return AESCrypt.decrypt(encryptedToken ?: "", TOKEN_CRYPT_KEY)
    }
}