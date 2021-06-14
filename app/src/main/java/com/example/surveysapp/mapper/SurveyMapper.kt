package com.example.surveysapp.mapper

import com.example.surveysapp.entity.SurveyWrapperEntity
import com.example.surveysapp.model.Survey

/**
 * @author longtran
 * @since 14/06/2021
 */
object SurveyMapper {
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
}