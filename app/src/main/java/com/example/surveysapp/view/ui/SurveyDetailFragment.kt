package com.example.surveysapp.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.surveysapp.databinding.FragmentSurveyDetailBinding
import com.example.surveysapp.view.ui.base.BaseFragment

/**
 * @author longtran
 * @since 15/06/2021
 */
class SurveyDetailFragment : BaseFragment<FragmentSurveyDetailBinding>() {

    override val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSurveyDetailBinding
        get() = FragmentSurveyDetailBinding::inflate

    override fun onBindingReady() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}