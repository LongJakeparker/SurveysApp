package com.example.surveysapp.view.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.example.surveysapp.R
import com.example.surveysapp.databinding.ActivityForgotPasswordBinding
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.other.isValidEmail
import com.example.surveysapp.util.Utils
import com.example.surveysapp.view.ui.base.BaseActivity
import com.example.surveysapp.viewModel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author longtran
 * @since 15/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {
    private val viewModel by viewModels<ForgotPasswordViewModel>()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ForgotPasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override val inflaterMethod: (LayoutInflater) -> ActivityForgotPasswordBinding
        get() = ActivityForgotPasswordBinding::inflate

    override fun onBindingReady() {
        binding.apply {
            viewModel = this@ForgotPasswordActivity.viewModel
            lifecycleOwner = this@ForgotPasswordActivity
        }

        observeEvents()
    }

    /**
     * Observes all livedata in viewModel
     */
    private fun observeEvents() {
        viewModel.apply {
            eventClickReset.observe(this@ForgotPasswordActivity) {
                // Start login
                reset(binding.etEmail.text.toString().trim())
            }

            resetDataFlow.observe(this@ForgotPasswordActivity) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        Utils.hideKeyboardFrom(this@ForgotPasswordActivity, binding.root)
                    }
                    is ViewState.Success -> {
                        // Navigate to MainActivity after login succeeded and finish this activity
                        Toast.makeText(this@ForgotPasswordActivity, R.string.reset_email_sent, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    is ViewState.Error -> {
                        Utils.showMessage(
                            this@ForgotPasswordActivity,
                            getString(R.string.label_something_went_wrong)
                        )
                    }
                }
            }

            eventTextFieldChanged.observe(this@ForgotPasswordActivity) {
                // Validate data to enable/disable whenever email field is changed
                val email = binding.etEmail.text.toString().trim()
                binding.btnReset.isEnabled = email.isValidEmail()
            }

            eventClickBack.observe(this@ForgotPasswordActivity) {
                finish()
            }
        }
    }
}