package com.example.surveysapp.view.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.example.surveysapp.databinding.ActivityMainBinding
import com.example.surveysapp.view.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override val inflaterMethod: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun onBindingReady() {

    }

}