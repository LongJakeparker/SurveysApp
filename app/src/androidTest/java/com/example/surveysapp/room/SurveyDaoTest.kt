package com.example.surveysapp.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

/**
 * @author longtran
 * @since 21/06/2021
 */

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class SurveyDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: SurveyDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).setTransactionExecutor(Executors.newSingleThreadExecutor()).allowMainThreadQueries()
            .build()
        dao = database.surveyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSurveyItem() = runBlocking {
        val surveyItem = SurveyRoomEntity("id", "title", "description", "url")
        dao.insertSurveys(listOf(surveyItem))

        val allSurveyItems = dao.getAllSurveys()

        assertThat(allSurveyItems).contains(surveyItem)
    }

    @Test
    fun insertSurveyItemPaging() = runBlocking {
        val surveyItem = SurveyRoomEntity("id", "title", "description", "url")
        dao.insertSurveys(listOf(surveyItem))

        val surveyItemPaging =
            SurveyRoomEntity("idPaging", "titlePaging", "descriptionPaging", "urlPaging")
        dao.insertSurveys(listOf(surveyItemPaging), true)

        val allSurveyItems = dao.getAllSurveys()

        assertThat(allSurveyItems).isEqualTo(listOf(surveyItem, surveyItemPaging))
    }

    @Test
    fun removeSurveyAllItems() = runBlocking {
        val surveyItem = SurveyRoomEntity("id", "title", "description", "url")
        val surveyItemTwo = SurveyRoomEntity("idTwo", "titleTwo", "descriptionTwo", "urlTwo")
        dao.insertSurveys(listOf(surveyItem, surveyItemTwo))
        dao.removeAll()

        val allSurveyItems = dao.getAllSurveys()

        assertThat(allSurveyItems).doesNotContain(surveyItem)
    }
}