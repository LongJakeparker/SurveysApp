package com.example.surveysapp.view.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.example.surveysapp.R
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.databinding.ActivityLoginBinding
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.other.isValidEmail
import com.example.surveysapp.util.Utils
import com.example.surveysapp.view.ui.base.BaseActivity
import com.example.surveysapp.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author longtran
 * @since 13/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel by viewModels<LoginViewModel>()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override val inflaterMethod: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun onBindingReady() {
        binding.apply {
            viewModel = this@LoginActivity.viewModel
            lifecycleOwner = this@LoginActivity
        }

        observeEvents()
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeEvents() {
        viewModel.apply {
            eventClickLogIn.observe(this@LoginActivity) {
                // Start login
                login(
                    binding.etEmail.text.toString().trim(),
                    binding.etPassword.text.toString().trim()
                )
            }

            loginDataFlow.observe(this@LoginActivity) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        Utils.hideKeyboardFrom(this@LoginActivity, binding.root)
                    }
                    is ViewState.Success -> {
                        // Navigate to MainActivity after login succeeded and finish this activity
                        MainActivity.start(this@LoginActivity)
                        finish()
                    }
                    is ViewState.Error -> {
                        Utils.showMessage(this@LoginActivity, getString(R.string.text_login_fail))
                    }
                }
            }

            eventTextFieldChanged.observe(this@LoginActivity) {
                // Validate data to enable/disable whenever email or password field is changed
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                binding.btnLogin.isEnabled = email.isValidEmail() && password.isNotEmpty()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}