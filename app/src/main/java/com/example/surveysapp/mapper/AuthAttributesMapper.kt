package com.example.surveysapp.mapper

import com.example.surveysapp.entity.AuthAttributesEntity
import com.example.surveysapp.model.AuthAttributes

/**
 * @author longtran
 * @since 13/06/2021
 */
object AuthAttributesMapper {
    fun transform(entity: AuthAttributesEntity?): AuthAttributes {
        return AuthAttributes(
            entity?.accessToken,
            entity?.tokenType,
            entity?.expiresIn,
            entity?.refreshToken,
            entity?.createdAt
        )
    }
}