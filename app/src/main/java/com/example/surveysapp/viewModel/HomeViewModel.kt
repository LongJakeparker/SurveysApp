package com.example.surveysapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.mapper.ProfileMapper
import com.example.surveysapp.mapper.SurveyMapper
import com.example.surveysapp.model.Profile
import com.example.surveysapp.model.Survey
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.ProfileRepository
import com.example.surveysapp.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * @author longtran
 * @since 14/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val surveyRepository: SurveyRepository,
    private val profileRepository: ProfileRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    //The data flows
    private val _surveys by lazy { MutableLiveData<ViewState<List<Survey>>>() }
    val surveys: LiveData<ViewState<List<Survey>>> = _surveys

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    private val _profile by lazy { MutableLiveData<Profile>() }
    val profile: LiveData<Profile> = _profile

    private val _currentItem by lazy { MutableLiveData<Survey>() }
    val currentItem: LiveData<Survey> = _currentItem

    fun getSurveyList() = viewModelScope.launch {
        // Start loading
        _surveys.postValue(ViewState.Loading())
        try {
            val response = surveyRepository.getSurveyList()

            // Get profile from local
            // Only fetch if null to reduce the requests call to server
            val localProfile = sharedPreferencesManager.getProfile()
            if (localProfile == null) {
                val profileResponse = profileRepository.getProfile()
                if (profileResponse.data != null) {
                    val responseData = ProfileMapper.transform(profileResponse.data)
                    _profile.postValue(responseData)
                    sharedPreferencesManager.putProfile(responseData)
                }
            } else {
                _profile.postValue(localProfile)
            }

            // Check if there is any error occur
            if (response.data != null) {
                // Parse data and post value to view
                val responseData = SurveyMapper.transformCollection(response.data)
                _surveys.postValue(ViewState.Success(responseData))
            } else {
                // Handles error
                _surveys.postValue(ViewState.Error(response.error?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _surveys.postValue(ViewState.Error(e.message))
        }
    }

    fun setCurrentItem(survey: Survey) {
        _currentItem.value = survey
    }

    fun setLoadingEnable(enable: Boolean) {
        _isEnableLoading.value = enable
    }

}