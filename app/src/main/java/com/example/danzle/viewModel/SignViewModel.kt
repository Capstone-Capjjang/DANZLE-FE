package com.example.danzle.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.danzle.data.remote.request.auth.SignInRequest
import com.example.danzle.data.remote.response.auth.CreateAccountResponse
import com.example.danzle.data.remote.response.auth.SignInResponse

class SignViewModel(application: Application): AndroidViewModel(application){
    private val _signInRequest = MutableLiveData<SignInResponse>()
    val signInRequest: LiveData<SignInResponse>
        get() = _signInRequest

    private val _createAccountRequest = MutableLiveData<CreateAccountResponse>()
    val createAccountRequest: LiveData<CreateAccountResponse>
        get() = _createAccountRequest


}