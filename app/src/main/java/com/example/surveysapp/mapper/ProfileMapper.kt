package com.example.surveysapp.mapper

import com.example.surveysapp.entity.ProfileWrapperEntity
import com.example.surveysapp.model.Profile

/**
 * @author longtran
 * @since 14/06/2021
 */
object ProfileMapper {
    fun transform(entity: ProfileWrapperEntity?): Profile {
        return Profile(
            entity?.id ?: "",
            entity?.attributes?.email,
            entity?.attributes?.avatarUrl
        )
    }
}