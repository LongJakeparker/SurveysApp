package com.example.surveysapp.entity

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * @author longtran
 * @since 13/06/2021
 */
data class BaseEntity<T>(
    @SerializedName("data")
    val data: T? = null,
    val error: ErrorEntity? = null
)

data class BaseException(
    @SerializedName("errors")
    val errors: List<ErrorEntity>? = null
)

/**
 * Extensions convert from retrofit Response to BaseEntity model
 */
fun <T> Response<BaseEntity<T>>.result(): BaseEntity<T> = if (isSuccessful) {
    body()!!
} else {
    // Convert errorBody to ErrorEntity for shorter access
    val baseException =
        Gson().fromJson(errorBody()?.string(), BaseException::class.java)
    BaseEntity(null, baseException.errors?.get(0))
}

fun Response<ResponseBody>.resultBoolean(): BaseEntity<Boolean> = if (isSuccessful) {
    BaseEntity(true, null)
} else {
    // Convert errorBody to ErrorEntity for shorter access
    val baseException =
        Gson().fromJson(errorBody()?.string(), BaseException::class.java)
    BaseEntity(null, baseException.errors?.get(0))
}
