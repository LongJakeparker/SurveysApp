package com.example.surveysapp.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.entity.AuthEntity
import com.example.surveysapp.entity.BaseEntity
import com.example.surveysapp.entity.ErrorEntity
import com.example.surveysapp.model.AuthAttributes
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
class LoginViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: LoginViewModel

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Mock
    private lateinit var loginResponseObserver: Observer<ViewState<AuthAttributes>>


    @Before
    fun setUp() {
        viewModel = LoginViewModel(
            authRepository,
            sharedPreferencesManager
        )
    }

    @After
    fun tearDown() {
        viewModel.loginDataFlow.removeObserver(loginResponseObserver)
    }

    @Test
    fun `when login with correct email and password then return success`() =
        testCoroutineRule.runBlockingTest {
            viewModel.loginDataFlow.observeForever(loginResponseObserver)

            // Return login result
            whenever(
                authRepository.login(anyString(), anyString())
            ).thenAnswer {
                BaseEntity(AuthEntity())
            }

            viewModel.login("email", "password")
            assertThat(viewModel.loginDataFlow.value).isNotNull()
            assertThat(viewModel.loginDataFlow.value).isInstanceOf(ViewState.Success::class.java)
        }

    @Test
    fun `when login with incorrect data then return error`() =
        testCoroutineRule.runBlockingTest {
            viewModel.loginDataFlow.observeForever(loginResponseObserver)

            // Return login result
            whenever(
                authRepository.login(anyString(), anyString())
            ).thenAnswer {
                BaseEntity<AuthEntity>(error = ErrorEntity())
            }

            viewModel.login("email", "password")
            assertThat(viewModel.loginDataFlow.value).isNotNull()
            assertThat(viewModel.loginDataFlow.value).isInstanceOf(ViewState.Error::class.java)
        }

    @Test
    fun `validate with correct email and password then return true`() {
        val email = "abc@def.com"
        val password = "123456"
        val result = viewModel.validateData(email, password)

        assertThat(result).isTrue()
    }

    @Test
    fun `validate with incorrect email then return false`() {
        val email = "abc@def"
        val password = "123456"
        val result = viewModel.validateData(email, password)

        assertThat(result).isFalse()
    }

    @Test
    fun `validate with empty email then return false`() {
        val email = ""
        val password = "123456"
        val result = viewModel.validateData(email, password)

        assertThat(result).isFalse()
    }

    @Test
    fun `validate with empty password then return false`() {
        val email = "abc@def.com"
        val password = ""
        val result = viewModel.validateData(email, password)

        assertThat(result).isFalse()
    }
}