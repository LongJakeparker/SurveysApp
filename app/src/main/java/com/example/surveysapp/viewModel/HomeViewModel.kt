package com.example.surveysapp.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.mapper.ProfileMapper
import com.example.surveysapp.mapper.SurveyMapper
import com.example.surveysapp.model.Profile
import com.example.surveysapp.model.Survey
import com.example.surveysapp.other.SingleEventLiveData
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.AuthRepository
import com.example.surveysapp.repository.ProfileRepository
import com.example.surveysapp.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author longtran
 * @since 14/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val surveyRepository: SurveyRepository,
    private val profileRepository: ProfileRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    //The data flows
    private val _surveys by lazy { MutableLiveData<ViewState<List<Survey>>>() }
    val surveys: LiveData<ViewState<List<Survey>>> = _surveys

    private val _logoutRequest by lazy { MutableLiveData<ViewState<Boolean>>() }
    val logoutRequest: LiveData<ViewState<Boolean>> = _logoutRequest

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    private val _profile by lazy { MutableLiveData<Profile>() }
    val profile: LiveData<Profile> = _profile

    private val _currentItem by lazy { MutableLiveData<Survey>() }
    val currentItem: LiveData<Survey> = _currentItem

    private val _eventClickAvatar by lazy { SingleEventLiveData<Int>() }
    val eventClickAvatar: LiveData<Int> = _eventClickAvatar

    val onClickAvatarListener = View.OnClickListener {
        _eventClickAvatar.setValue(1)
    }

    private val _eventClickLogout by lazy { SingleEventLiveData<Int>() }
    val eventClickLogout: LiveData<Int> = _eventClickLogout

    val onClickLogoutListener = View.OnClickListener {
        _eventClickLogout.setValue(1)
    }

    private val _eventClickDetail by lazy { SingleEventLiveData<Int>() }
    val eventClickDetail: LiveData<Int> = _eventClickDetail

    val onClickDetailListener = View.OnClickListener {
        _eventClickDetail.setValue(1)
    }


    fun getSurveyList() = viewModelScope.launch {
        // Start loading
        _surveys.postValue(ViewState.Loading())
        try {
            val response = surveyRepository.getSurveyList()

            // Gets profile and binds
            getProfile()

            // Checks if there is any error occur
            if (response.data != null) {
                // Parses data and posts value to view
                val responseData = SurveyMapper.transformCollection(response.data)
                _surveys.postValue(ViewState.Success(responseData))
                updateToRoom(responseData)
            } else {
                // Handles error
                _surveys.postValue(ViewState.Error(response.error?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _surveys.postValue(ViewState.Error(e.message))
        }
    }

    private suspend fun updateToRoom(responseData: List<Survey>) {
        if (surveyRepository.getLocalSurveys().isNotEmpty()) {
            surveyRepository.removeSurveys()
        }

        surveyRepository.insertSurveys(SurveyMapper.transformCollectionToRoom(responseData))
    }

    private suspend fun getProfile() {
        // Only fetch if local data is null to reduce the requests call to server
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
    }

    /**
     * Queries survey list from Room DB
     */
    fun querySurveyFromLocal() = viewModelScope.launch {
        try {
            val response =
                SurveyMapper.transformCollectionFromRoom(surveyRepository.getLocalSurveys())
            _surveys.postValue(ViewState.Success(response))
        } catch (e: Exception) {
        }
    }

    fun setCurrentItem(survey: Survey) {
        _currentItem.value = survey
    }

    fun setLoadingEnable(enable: Boolean) {
        _isEnableLoading.value = enable
    }

    fun logout() = viewModelScope.launch {
        // Start loading
        _logoutRequest.postValue(ViewState.Loading())
        try {
            val response = authRepository.logout()

            // Checks if there is any error occur
            if (response.data != null) {
                // Removes all data and notify login to view
                sharedPreferencesManager.clearSignInData()
                surveyRepository.removeSurveys()
                _logoutRequest.postValue(ViewState.Success(response.data))
            } else {
                // Handles error
                _logoutRequest.postValue(ViewState.Error(response.error?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _logoutRequest.postValue(ViewState.Error(e.message))
        }
    }
}