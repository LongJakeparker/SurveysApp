package com.example.surveysapp.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.ErrorEntity
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.AuthRepository
import com.example.surveysapp.utils.TestCoroutineRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as whenever

/**
 * @author longtran
 * @since 21/06/2021
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ForgotPasswordViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: ForgotPasswordViewModel

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var resetResponseObserver: Observer<ViewState<Boolean>>


    @Before
    fun setUp() {
        viewModel = ForgotPasswordViewModel(
            authRepository
        )
    }

    @After
    fun tearDown() {
        viewModel.resetDataFlow.removeObserver(resetResponseObserver)
    }

    @Test
    fun `when reset ok then return success`() =
        testCoroutineRule.runBlockingTest {
            viewModel.resetDataFlow.observeForever(resetResponseObserver)

            // Return login result
            whenever(
                authRepository.forgotPassword(anyString())
            ).thenAnswer {
                BaseEntity(true)
            }

            viewModel.reset("email")
            assertThat(viewModel.resetDataFlow.value).isNotNull()
            assertThat(viewModel.resetDataFlow.value).isInstanceOf(ViewState.Success::class.java)
        }

    @Test
    fun `when reset with incorrect data then return error`() =
        testCoroutineRule.runBlockingTest {
            viewModel.resetDataFlow.observeForever(resetResponseObserver)

            // Return login result
            whenever(
                authRepository.forgotPassword(anyString())
            ).thenAnswer {
                BaseEntity<Boolean>(error = ErrorEntity())
            }

            viewModel.reset("email")
            assertThat(viewModel.resetDataFlow.value).isNotNull()
            assertThat(viewModel.resetDataFlow.value).isInstanceOf(ViewState.Error::class.java)
        }
}