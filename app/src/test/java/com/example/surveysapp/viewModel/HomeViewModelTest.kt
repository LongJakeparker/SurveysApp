package com.example.surveysapp.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.entity.*
import com.example.surveysapp.model.Profile
import com.example.surveysapp.model.Survey
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.AuthRepository
import com.example.surveysapp.repository.ProfileRepository
import com.example.surveysapp.repository.SurveyRepository
import com.example.surveysapp.utils.TestCoroutineRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.isNull
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenever

/**
 * @author longtran
 * @since 21/06/2021
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: HomeViewModel

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var surveyRepository: SurveyRepository

    @Mock
    private lateinit var profileRepository: ProfileRepository

    @Mock
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Mock
    private lateinit var listSurveyResponseObserver: Observer<ViewState<List<Survey>>>

    @Mock
    private lateinit var profileResponseObserver: Observer<Profile>

    @Mock
    private lateinit var logoutResponseObserver: Observer<ViewState<Boolean>>

    @Before
    fun setUp() {
        viewModel = HomeViewModel(
            authRepository,
            surveyRepository,
            profileRepository,
            sharedPreferencesManager
        )
    }

    @After
    fun tearDown() {
        viewModel.surveys.removeObserver(listSurveyResponseObserver)
        viewModel.profile.removeObserver(profileResponseObserver)
        viewModel.logoutRequest.removeObserver(logoutResponseObserver)
    }

    @Test
    fun `when fetching results ok then return a list successfully and fetch profile when local storage empty`() =
        testCoroutineRule.runBlockingTest {
            val emptyList = listOf<SurveyWrapperEntity>()
            viewModel.surveys.observeForever(listSurveyResponseObserver)
            viewModel.profile.observeForever(profileResponseObserver)

            val profile = Profile("id", "email", "url")
            val profileWrapperEntity = ProfileWrapperEntity("id", "email", ProfileEntity("email", "url"))

            // Return data fetch survey list
            whenever(
                surveyRepository.getSurveyList(
                    anyInt(),
                    isNull()
                )
            ).thenAnswer {
                BaseEntity(emptyList)
            }

            // Return data fetch profile
            whenever(
                profileRepository.getProfile()
            ).thenAnswer {
                BaseEntity(profileWrapperEntity)
            }
            viewModel.getSurveyList()

            assertThat(viewModel.profile.value).isNotNull()
            assertThat(viewModel.profile.value).isEqualTo(profile)

            assertThat(viewModel.surveys.value).isNotNull()
            assertThat(viewModel.surveys.value).isInstanceOf(ViewState.Success::class.java)
        }


    @Test
    fun `when fetching results fails then return an error and get profile from local when already fetched`() =
        testCoroutineRule.runBlockingTest {
            viewModel.surveys.observeForever(listSurveyResponseObserver)
            viewModel.profile.observeForever(profileResponseObserver)

            val profile = Profile("id", "email", "url")

            // Return data fetch survey list
            whenever(
                surveyRepository.getSurveyList(
                    anyInt(),
                    isNull()
                )
            ).thenAnswer {
                BaseEntity<List<SurveyWrapperEntity>>(error = ErrorEntity())
            }

            // Return data profile from local
            whenever(
                sharedPreferencesManager.getProfile()
            ).thenAnswer {
                profile
            }
            viewModel.getSurveyList()

            assertThat(viewModel.profile.value).isNotNull()
            assertThat(viewModel.profile.value).isEqualTo(profile)

            assertThat(viewModel.surveys.value).isNotNull()
            assertThat(viewModel.surveys.value).isInstanceOf(ViewState.Error::class.java)
        }

    @Test
    fun `when logout ok then return success`() =
        testCoroutineRule.runBlockingTest {
            viewModel.logoutRequest.observeForever(logoutResponseObserver)

            // Return logout result
            whenever(
                authRepository.logout()
            ).thenAnswer {
                BaseEntity(true)
            }

            viewModel.logout()
            assertThat(viewModel.logoutRequest.value).isNotNull()
            assertThat(viewModel.logoutRequest.value).isInstanceOf(ViewState.Success::class.java)
        }

    @Test
    fun `when logout fails then return an error`() =
        testCoroutineRule.runBlockingTest {
            viewModel.logoutRequest.observeForever(logoutResponseObserver)

            // Return logout result
            whenever(
                authRepository.logout()
            ).thenAnswer {
                BaseEntity<Boolean>(error = ErrorEntity())
            }

            viewModel.logout()
            assertThat(viewModel.logoutRequest.value).isNotNull()
            assertThat(viewModel.logoutRequest.value).isInstanceOf(ViewState.Error::class.java)
        }
}