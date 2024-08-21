package com.amory.landmarkremark.viewModel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignInViewModel : ViewModel() {
    private val _authResult = MutableLiveData<Boolean?>()
    val authResult: LiveData<Boolean?> get() = _authResult

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /*Đăng nhập bằng email và mật khẩu*/
    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                val authResult = mAuth.signInWithEmailAndPassword(email, password).await()
                if (authResult.user != null) {
                    _authResult.postValue(true)
                    authResult.user?.uid?.let { addFieldToDocument(it, email) }
                } else {
                    _authResult.postValue(false)
                }
            } catch (e: Exception) {
                _authResult.postValue(false)
            }
        }
    }

    /*Kiểm tra đăng nhập*/
    fun isSignUp(): Boolean {
        return mAuth.currentUser != null
    }

    /*Lưu userId vào firebase*/
    fun pushUserIdToFirebase() {
        viewModelScope.launch {
            val userId = mAuth.currentUser?.uid

            if (userId != null) {
                val userRef = db.collection("locations").document(userId)

                try {
                    val document = withContext(Dispatchers.IO) { userRef.get().await() }
                    if (document.exists()) {
                        Log.d("TAG", "Document already exists")
                    } else {
                        withContext(Dispatchers.IO) {
                            userRef.set(mapOf<String, Any>()).await()
                        }
                        Log.d("TAG", "Document created successfully")
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "Error creating or checking document: $e")
                }
            } else {
                Log.d("TAG", "User is not logged in or UID is null")
            }
        }
    }
    /*Thêm trường email vào document*/
    private suspend fun addFieldToDocument(uid: String, fieldName: String) {
        withContext(Dispatchers.IO) {
            val documentRef = db.collection("locations").document(uid)
            documentRef.set(hashMapOf("email" to fieldName), SetOptions.merge()).await()
        }
    }

}