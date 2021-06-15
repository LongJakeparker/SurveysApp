package com.example.surveysapp.viewModel

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveysapp.other.SingleEventLiveData
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author longtran
 * @since 13/06/2021
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    //The data flows
    private val _resetDataFlow by lazy { MutableLiveData<ViewState<Boolean>>() }
    val resetDataFlow: LiveData<ViewState<Boolean>>
        get() = _resetDataFlow

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    //The single LiveData represents for click events
    private val _eventClickReset by lazy { SingleEventLiveData<Int>() }
    val eventClickReset: LiveData<Int> = _eventClickReset

    private val _eventClickBack by lazy { SingleEventLiveData<Int>() }
    val eventClickBack: LiveData<Int> = _eventClickBack

    private val _eventTextFieldChanged by lazy { SingleEventLiveData<Int>() }
    val eventTextFieldChanged: LiveData<Int> = _eventTextFieldChanged

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val clickResetListener = View.OnClickListener {
        _eventClickReset.setValue(1)
    }

    val clickBackListener = View.OnClickListener {
        _eventClickBack.setValue(1)
    }

    val emailTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _eventTextFieldChanged.setValue(1)
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun reset(email: String) = viewModelScope.launch {
        // Start loading
        _resetDataFlow.postValue(ViewState.Loading())
        _isEnableLoading.postValue(true)
        try {
            val response = repository.forgotPassword(email)

            _isEnableLoading.postValue(false)
            // Check if there is any error occur
            if (response.data != null) {
                // Parse data and post value to view
                _resetDataFlow.postValue(ViewState.Success(response.data))
            } else {
                // Handles error
                _resetDataFlow.postValue(ViewState.Error(response.error?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _isEnableLoading.postValue(false)
            _resetDataFlow.postValue(ViewState.Error(e.message))
        }
    }
}