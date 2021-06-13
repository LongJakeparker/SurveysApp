package com.example.surveysapp.view.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.ActivityOptions
import android.content.Intent
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.example.surveysapp.R
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.databinding.ActivitySplashBinding
import com.example.surveysapp.view.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    companion object {
        private const val LOGO_ANIMATION_DURATION = 800L
    }

    override val inflaterMethod: (LayoutInflater) -> ActivitySplashBinding
        get() = ActivitySplashBinding::inflate

    override fun onBindingReady() {
        binding.ivLogo.animate().alpha(1f).setDuration(LOGO_ANIMATION_DURATION)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    // Check whether user have already logged in or not
                    if (sharedPreferencesManager.getAccessToken().isEmpty()) {
                        navigateToLogin()
                    } else {
                        navigateToMain()
                    }
                }
            })
            .setInterpolator(AccelerateInterpolator()).start()
    }

    private fun navigateToMain() {
        MainActivity.start(this)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        // Setup shared element to make the animation with the app logo
        val shareLogoImage =
            Pair<View, String>(
                binding.ivLogo,
                getString(R.string.text_transition_logo)
            )

        val option = ActivityOptions.makeSceneTransitionAnimation(
            this@SplashActivity,
            shareLogoImage
        )

        startActivity(intent, option.toBundle())
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}