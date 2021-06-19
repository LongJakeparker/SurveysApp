package com.example.surveysapp.view.ui

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


/**
 * @author longtran
 * @since 14/06/2021
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    @Inject
    @ApplicationContext
    lateinit var applicationContext: Context

    private val viewModel by viewModels<HomeViewModel>()
    private val sliderAdapter by lazy { SurveySlidePagerAdapter() }

    companion object {
        private const val DRAWER_WIDTH_PERCENTAGE = 0.65
    }

    // Listener for cover page change event
    private val coverPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // Update current item to bind to view
            val item = sliderAdapter.currentList[position]
            viewModel.setCurrentItem(item)
        }
    }

    // Listener for pull to refresh event
    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        // Only fetch when internet connection is available
        if (Utils.isNetworkAvailable(applicationContext)) {
            viewModel.getSurveyList()
        } else {
            binding.includeHome.refreshLayout.isRefreshing = false
        }
    }

    override val inflaterMethod: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun onBindingReady() {
        binding.apply {
            viewModel = this@HomeFragment.viewModel
            lifecycleOwner = viewLifecycleOwner

            includeHome.apply {
                tvDate.text = Utils.getCurrentDate()
                refreshLayout.setOnRefreshListener(refreshListener)
            }
        }

        if (!Utils.isNetworkAvailable(applicationContext)) {
            viewModel.querySurveyFromLocal()
        } else if (viewModel.surveys.value == null) { // Prevents reload when back from backstack
            viewModel.getSurveyList()
        }

        setupDrawerLayout()
        setupViewpager()

        observeEvents()
    }

    private fun setupDrawerLayout() {
        binding.drawerLayoutRight.setScrimColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black_30
            )
        )

        // Sets width of drawer layout based on screen width
        binding.includeDrawer.clDrawerContainer.apply {
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val widthOfNav = screenWidth * DRAWER_WIDTH_PERCENTAGE
            layoutParams.width = widthOfNav.toInt()
            requestLayout()
        }
    }

    private fun setupViewpager() {
        binding.includeHome.vpgCover.apply {
            adapter = sliderAdapter

            TabLayoutMediator(binding.includeHome.indicatorTabLayout, this) { tab, position ->
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
                        if (!binding.includeHome.refreshLayout.isRefreshing) {
                            setLoadingEnable(true)
                        }
                    }
                    is ViewState.Success -> {
                        binding.includeHome.refreshLayout.isRefreshing = false
                        setLoadingEnable(false)
                        sliderAdapter.submitList(response.value)
                    }
                    is ViewState.Error -> {
                        binding.includeHome.refreshLayout.isRefreshing = false
                        setLoadingEnable(false)
                        // Get local data if fetch from server was failed
                        viewModel.querySurveyFromLocal()
                    }
                }
            }

            eventClickAvatar.observe(viewLifecycleOwner) {
                binding.drawerLayoutRight.openDrawer(GravityCompat.END)
            }

            eventClickDetail.observe(viewLifecycleOwner) {
                navigateToSurveyDetail()
            }

            eventClickLogout.observe(viewLifecycleOwner) {
                Utils.showLogoutMessage(requireContext()) { _, _ ->
                    viewModel.logout()
                    binding.drawerLayoutRight.closeDrawer(GravityCompat.END)
                }
            }

            logoutRequest.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        if (!isEnableLoading.value!!) {
                            binding.includeHome.flProgress.visibility = View.VISIBLE
                        }
                    }

                    is ViewState.Success -> {
                        binding.includeHome.flProgress.visibility = View.GONE
                        LoginActivity.start(requireContext())
                        requireActivity().finish()
                    }

                    is ViewState.Error -> {
                        binding.includeHome.flProgress.visibility = View.GONE
                        Utils.showMessage(requireContext(), response.message)
                    }
                }
            }
        }
    }

    private fun navigateToSurveyDetail() {
        findNavController().navigate(
            HomeFragmentDirections.navigateToSurveyDetailFragment()
        )
    }
}