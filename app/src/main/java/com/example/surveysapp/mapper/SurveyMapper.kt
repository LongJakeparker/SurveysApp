package com.example.surveysapp.mapper

import com.example.surveysapp.entity.SurveyWrapperEntity
import com.example.surveysapp.model.Survey
import com.example.surveysapp.room.SurveyRoomEntity

/**
 * @author longtran
 * @since 14/06/2021
 */
object SurveyMapper {
    // Map from api entity to model
    private fun transform(entity: SurveyWrapperEntity?): Survey {
        return Survey(
            entity?.id ?: "",
            entity?.attributes?.title,
            entity?.attributes?.description,
            entity?.attributes?.coverImageUrl + "l"
        )
    }

    fun transformCollection(entityCollection: List<SurveyWrapperEntity?>?): List<Survey> {
        val result = ArrayList<Survey>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    // Map from model to Room entity
    private fun transform(survey: Survey?): SurveyRoomEntity {
        return SurveyRoomEntity(
            survey?.id ?: "",
            survey?.title,
            survey?.description,
            survey?.coverImageUrl
        )
    }

    fun transformCollectionToRoom(entityCollection: List<Survey?>?): List<SurveyRoomEntity> {
        val result = ArrayList<SurveyRoomEntity>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }

    // Map from Room entity to model
    private fun transform(entity: SurveyRoomEntity?): Survey {
        return Survey(
            entity?.id ?: "",
            entity?.title,
            entity?.description,
            entity?.coverImageUrl
        )
    }

    fun transformCollectionFromRoom(entityCollection: List<SurveyRoomEntity?>?): List<Survey> {
        val result = ArrayList<Survey>()
        if (entityCollection == null)
            return result

        for (entity in entityCollection) {
            entity?.let { result.add(transform(it)) }
        }
        return result
    }
}