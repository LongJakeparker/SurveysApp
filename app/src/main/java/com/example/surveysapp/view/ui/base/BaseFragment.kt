package com.example.surveysapp.view.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Inherits Fragment and supports fast generic-binding
 * @author longtran
 * @since 13/06/2021
 */
abstract class BaseFragment<T : ViewBinding> : Fragment() {
    protected lateinit var binding: T

    abstract val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflaterMethod.invoke(inflater, container, false)
        onBindingReady()
        return binding.root
    }

    /**
     * Invoke binding object after object is created, before returning binding.root.
     */
    abstract fun onBindingReady()
}