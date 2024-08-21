package com.amory.landmarkremark.viewModel

import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amory.landmarkremark.model.Landmark
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainViewModel:ViewModel() {
    private val _landMarks = MutableLiveData<List<Landmark>?>()
    val landMarks: LiveData<List<Landmark>?> get() = _landMarks

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /*Lưu điểm đánh dấu vào Firebase*/
    fun saveLocationToFirebase(
        latLng: LatLng,
        title: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val user = mAuth.currentUser
            val uid = user?.uid ?: return@launch onFailure("User not authenticated")
            val ref = db.collection("locations").document(uid).collection("locations")

            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    ref.whereEqualTo("latitude", latLng.latitude)
                        .whereEqualTo("longitude", latLng.longitude)
                        .get().await()
                }

                if (!querySnapshot.isEmpty) {
                    onFailure("Location already exists")
                } else {
                    val newLocationRef = ref.document()
                    val location = mapOf(
                        "latitude" to latLng.latitude,
                        "longitude" to latLng.longitude,
                        "title" to title,
                        "description" to description
                    )

                    withContext(Dispatchers.IO) {
                        newLocationRef.set(location).await()
                    }
                    onSuccess()
                }
            } catch (e: Exception) {
                onFailure("Failed to save location: ${e.message}")
            }
        }
    }

    /*Lấy tất cả điểm đánh dấu của tất cả người dùng*/
    fun getAllMarkLocationAllUser() {
        viewModelScope.launch {
            val listLandMark = mutableListOf<Landmark>()
            val ref = db.collection("locations")

            try {
                val userSnapshot = withContext(Dispatchers.IO) { ref.get().await() }
                for (userDocument in userSnapshot.documents) {
                    val uid = userDocument.id
                    val email = userDocument.getString("email")

                    val userRef = ref.document(uid).collection("locations")
                    val locationSnapshot = withContext(Dispatchers.IO) { userRef.get().await() }

                    for (document in locationSnapshot.documents) {
                        val latitude = document.getDouble("latitude")
                        val longitude = document.getDouble("longitude")
                        val title = document.getString("title")
                        val description = document.getString("description")

                        if (latitude != null && longitude != null && title != null && description != null) {
                            val landmark = Landmark(email ?: "", title, description, latitude, longitude)
                            listLandMark.add(landmark)
                        }
                    }
                }
                _landMarks.postValue(listLandMark)
            } catch (e: Exception) {
                _landMarks.postValue(null)
            }
        }
    }
}