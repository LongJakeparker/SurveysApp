package com.example.surveysapp.other

import androidx.viewpager2.widget.ViewPager2
import com.example.surveysapp.view.adapter.SurveySlidePagerAdapter

/**
 * Inherits ViewPager2.OnPageChangeCallback to make list keep loading more data when reach bottom
 * @author longtran
 * @since 20/06/2021
 */
abstract class EndlessScrollListener(val adapter: SurveySlidePagerAdapter) :
    ViewPager2.OnPageChangeCallback() {

    // Flag to determine is fetching more data or not
    var isLoading = false
        private set

    // Number of prefetch items before reach at bottom
    private val visibleThreshold = 2

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        if (!isLoading && adapter.itemCount > 0 && (position + visibleThreshold) >= adapter.itemCount) {
            onLoadMore()
            isLoading = true
        }
    }

    /**
     * Sets flag to false after finished fetch data
     */
    fun setLoadingFinished() {
        isLoading = false
    }

    /**
     * Abstract function to notify the list need to load more items
     */
    abstract fun onLoadMore()
}