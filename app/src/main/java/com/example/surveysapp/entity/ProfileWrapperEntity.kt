package com.example.surveysapp.entity

import com.example.surveysapp.model.Profile
import com.google.gson.annotations.SerializedName

/**
 * @author longtran
 * @since 14/06/2021
 */
data class ProfileWrapperEntity(
    @SerializedName("id")
    val id: String? = "",

    @SerializedName("type")
    val type: String? = "",

    @SerializedName("attributes")
    val attributes: ProfileEntity? = null
)

/**
 * Extension converts from entity to Profile model
 */
fun ProfileWrapperEntity?.toModel() = (this?.run {
    Profile(
        id.orEmpty(),
        attributes?.email,
        attributes?.avatarUrl
    )
} ?: Profile())