package com.example.surveysapp.view.ui

import android.view.LayoutInflater
import com.example.surveysapp.databinding.ActivityMainBinding
import com.example.surveysapp.view.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val inflaterMethod: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun onBindingReady() {

    }

}