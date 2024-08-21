package com.amory.landmarkremark.viewModel

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amory.landmarkremark.activity.SignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel:ViewModel() {
    private val _resultSignUp = MutableLiveData<Boolean?>()
    val resultSignUp : LiveData<Boolean?> get() = _resultSignUp

    private val firebaseAuth = FirebaseAuth.getInstance()

    /*Đăng ký tài khoản*/
    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                if (authResult.user != null) {
                    _resultSignUp.postValue(true)
                } else {
                    _resultSignUp.postValue(false)
                }
            } catch (e: Exception) {
                _resultSignUp.postValue(false)
            }
        }
    }
}