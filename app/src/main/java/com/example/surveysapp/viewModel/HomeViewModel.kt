package com.example.surveysapp.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.entity.toModel
import com.example.surveysapp.entity.toModelList
import com.example.surveysapp.model.Profile
import com.example.surveysapp.model.Survey
import com.example.surveysapp.model.toRoomEntityList
import com.example.surveysapp.other.SingleEventLiveData
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.AuthRepository
import com.example.surveysapp.repository.ProfileRepository
import com.example.surveysapp.repository.SurveyRepository
import com.example.surveysapp.room.toModelList
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

    private var currentPage = 0
    private var currentList = ArrayList<Survey>()
    val isLoadMore
        get() = currentPage > 1

    //The data flows
    private val _surveys by lazy { MutableLiveData<ViewState<List<Survey>>>() }
    val surveys: LiveData<ViewState<List<Survey>>> = _surveys

    private val _logoutRequest by lazy { MutableLiveData<ViewState<Boolean>>() }
    val logoutRequest: LiveData<ViewState<Boolean>> = _logoutRequest

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    private val _profile by lazy { MutableLiveData<Profile>() }
    val profile: LiveData<Profile> = _profile

    private val _hasNextPage by lazy { MutableLiveData(false) }
    val hasNextPage: LiveData<Boolean>
        get() = _hasNextPage

    private val _currentItem by lazy { MutableLiveData<Survey>() }
    val currentItem: LiveData<Survey> = _currentItem

    private val _eventClickAvatar by lazy { SingleEventLiveData<Unit>() }
    val eventClickAvatar: LiveData<Unit> = _eventClickAvatar

    val onClickAvatarListener = View.OnClickListener {
        _eventClickAvatar.setValue(Unit)
    }

    private val _eventClickLogout by lazy { SingleEventLiveData<Unit>() }
    val eventClickLogout: LiveData<Unit> = _eventClickLogout

    val onClickLogoutListener = View.OnClickListener {
        _eventClickLogout.setValue(Unit)
    }

    private val _eventClickDetail by lazy { SingleEventLiveData<Unit>() }
    val eventClickDetail: LiveData<Unit> = _eventClickDetail

    val onClickDetailListener = View.OnClickListener {
        _eventClickDetail.setValue(Unit)
    }

    fun onRefresh() {
        currentPage = 0
        getSurveyList()
    }

    fun getSurveyList() = viewModelScope.launch {
        // Start loading
        _surveys.postValue(ViewState.Loading())
        try {
            val response = surveyRepository.getSurveyList(++currentPage)

            // Gets profile and binds
            getProfile()

            // Checks if there is any error occur
            if (response.data != null) {
                // Keep data served for paging
                response.meta?.apply {
                    page?.let { currentPage = it }
                    pages?.let { _hasNextPage.postValue(currentPage < it) }
                }

                // Parses data and posts value to view
                val responseData = response.data.toModelList()

                if (!isLoadMore) {
                    currentList = ArrayList(responseData)
                } else {
                    currentList.addAll(responseData)
                }

                _surveys.postValue(ViewState.Success(ArrayList(currentList)))
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
        surveyRepository.insertSurveys(responseData.toRoomEntityList(), isLoadMore)
    }

    private suspend fun getProfile() {
        // Only fetch if local data is null to reduce the requests call to server
        val localProfile = sharedPreferencesManager.getProfile()
        if (localProfile == null) {
            val profileResponse = profileRepository.getProfile()
            if (profileResponse.data != null) {
                val responseData = profileResponse.data.toModel()
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
            val response = surveyRepository.getLocalSurveys().toModelList()
            _surveys.postValue(ViewState.Success(response))
        } catch (e: Exception) {
        }
    }

    fun getProfileFromLocal() = sharedPreferencesManager.getProfile()?.let {
        _profile.postValue(it)
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