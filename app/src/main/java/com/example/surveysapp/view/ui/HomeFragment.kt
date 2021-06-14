package com.example.surveysapp.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.surveysapp.R
import com.example.surveysapp.SurveysApplication
import com.example.surveysapp.databinding.FragmentHomeBinding
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.util.Utils
import com.example.surveysapp.view.adapter.SurveySlidePagerAdapter
import com.example.surveysapp.view.ui.base.BaseFragment
import com.example.surveysapp.viewModel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * @author longtran
 * @since 14/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val viewModel by viewModels<HomeViewModel>()
    private val sliderAdapter by lazy { SurveySlidePagerAdapter() }

    // Listener for cover page change event
    private val coverPageChangeListener = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // Update current item to bind to view
            val item = sliderAdapter.currentList[position]
            viewModel.setCurrentItem(item)
        }
    }

    // Listener for pull to refresh event
    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        // Only fetch when internet connection is available
        if (Utils.isNetworkAvailable(SurveysApplication.instance.applicationContext)) {
            viewModel.getSurveyList()
        } else {
            binding.refreshLayout.isRefreshing = false
        }
    }

    override val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun onBindingReady() {
        binding.apply {
            viewModel = this@HomeFragment.viewModel
            lifecycleOwner = viewLifecycleOwner

            tvDate.text = Utils.getCurrentDate()
            refreshLayout.setOnRefreshListener(refreshListener)
        }

        // Prevents reload when back from backstack
        if (viewModel.surveys.value == null) {
            viewModel.getSurveyList()
        }

        setupViewpager()

        observeEvents()
    }

    private fun setupViewpager() {
        binding.vpgCover.apply {
            adapter = sliderAdapter

            TabLayoutMediator(binding.indicatorTabLayout, this) { tab, position ->
                tab.view.isClickable = false
            }.attach()

            registerOnPageChangeCallback(coverPageChangeListener)
        }
    }

    private fun observeEvents() {
        viewModel.apply {
            surveys.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        if (!binding.refreshLayout.isRefreshing) {
                            setLoadingEnable(true)
                        }
                    }
                    is ViewState.Success -> {
                        binding.refreshLayout.isRefreshing = false
                        setLoadingEnable(false)
                        sliderAdapter.submitList(response.value)
                    }
                    is ViewState.Error -> {
                        binding.refreshLayout.isRefreshing = false
                        setLoadingEnable(false)
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}