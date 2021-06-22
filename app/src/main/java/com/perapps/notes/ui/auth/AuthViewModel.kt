package com.perapps.notes.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.perapps.notes.other.Constants.NO_EMAIL
import com.perapps.notes.other.Constants.NO_PASSWORD
import com.perapps.notes.other.Resource
import com.perapps.notes.prefstore.PrefsStore
import com.perapps.notes.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    @Inject
    lateinit var prefsStore: PrefsStore

    private val _registrationStatus = MutableLiveData<Resource<String>>()
    val registrationStatus: LiveData<Resource<String>> = _registrationStatus

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    fun register(email: String, password: String, confirmPass: String) {
        _registrationStatus.postValue(Resource.loading(null))
        if (password != confirmPass) {
            _registrationStatus.postValue(Resource.error("Password do not match", null))
            return
        }
        viewModelScope.launch {
            val result = repository.register(email, password)
            _registrationStatus.postValue(result)
        }
    }

    fun login(email: String, password: String) {
        _registrationStatus.postValue(Resource.loading(null))

        viewModelScope.launch {
            val result = repository.login(email, password)
            _registrationStatus.value = result
        }
    }

    fun saveLoggedInUser(email: String, password: String){
        viewModelScope.launch {
            prefsStore.setLoggedInUser(email, password)
        }
    }
}