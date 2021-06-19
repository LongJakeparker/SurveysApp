package com.example.surveysapp.view.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * Inherits AppCompatActivity and supports fast generic-binding
 * @author longtran
 * @since 13/06/2021
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    protected lateinit var binding: T

    abstract val inflaterMethod: (LayoutInflater) -> T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflaterMethod.invoke(layoutInflater)
        setContentView(binding.root)
        onBindingReady()
    }

    /**
     * Invoke binding object after object is created, before returning binding.root.
     */
    abstract fun onBindingReady()
}