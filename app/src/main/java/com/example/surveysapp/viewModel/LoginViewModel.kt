package com.example.surveysapp.viewModel

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.surveysapp.SharedPreferencesManager
import com.example.surveysapp.entity.toModel
import com.example.surveysapp.model.AuthAttributes
import com.example.surveysapp.other.SingleEventLiveData
import com.example.surveysapp.other.ViewState
import com.example.surveysapp.other.isValidEmail
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
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    //The data flows
    private val _loginDataFlow by lazy { MutableLiveData<ViewState<AuthAttributes>>() }
    val loginDataFlow: LiveData<ViewState<AuthAttributes>>
        get() = _loginDataFlow

    private val _isEnableLoading by lazy { MutableLiveData(false) }
    val isEnableLoading: LiveData<Boolean> = _isEnableLoading

    //The single LiveData represents for click events
    private val _eventClickLogIn by lazy { SingleEventLiveData<Unit>() }
    val eventClickLogIn: LiveData<Unit> = _eventClickLogIn

    private val _eventTextFieldChanged by lazy { SingleEventLiveData<Unit>() }
    val eventTextFieldChanged: LiveData<Unit> = _eventTextFieldChanged

    private val _eventClickReset by lazy { SingleEventLiveData<Unit>() }
    val eventClickReset: LiveData<Unit> = _eventClickReset

    //On-click events are set to view through data-binding
    //Sets LiveData value to notify to Fragment/Activity
    val clickLoginListener = View.OnClickListener {
        _eventClickLogIn.setValue(Unit)
    }

    val clickResetListener = View.OnClickListener {
        _eventClickReset.setValue(Unit)
    }

    val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _eventTextFieldChanged.setValue(Unit)
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun validateData(email: String, password: String) = email.isValidEmail() && password.isNotEmpty()

    fun login(email: String, password: String) = viewModelScope.launch {
        // Start loading
        _loginDataFlow.postValue(ViewState.Loading())
        _isEnableLoading.postValue(true)
        try {
            val response = repository.login(email, password)

            _isEnableLoading.postValue(false)
            // Check if there is any error occur
            if (response.data != null) {
                // Parse data and post value to view
                val data = response.data.attributes.toModel()
                _loginDataFlow.postValue(ViewState.Success(data))

                // Save data to local
                sharedPreferencesManager.putSignInData(data)
            } else {
                // Handles error
                _loginDataFlow.postValue(ViewState.Error(response.error?.message))
            }
        } catch (e: Exception) {
            // Handles exception
            _isEnableLoading.postValue(false)
            _loginDataFlow.postValue(ViewState.Error(e.message))
        }
    }
}