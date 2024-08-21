package com.amory.landmarkremark.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import com.amory.landmarkremark.R
import com.amory.landmarkremark.adapter.CustomShowMark
import com.amory.landmarkremark.databinding.ActivityMainBinding
import com.amory.landmarkremark.databinding.AddMarkLocationBinding
import com.amory.landmarkremark.model.Landmark
import com.amory.landmarkremark.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private val markers = mutableMapOf<String, Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel.getAllMarkLocationAllUser()
        observerMark()
        getCurrentLocation()
        onClickListener()
    }

    private fun onClickListener() {
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
        binding.btnAdd.setOnClickListener {
            val lagLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            showAddMarkDiaLog(mMap, lagLng)
        }
        binding.btnZoomIn.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomIn())
        }
        binding.btnZoomOut.setOnClickListener {
            mMap.animateCamera(CameraUpdateFactory.zoomOut())
        }
        binding.btnSearch.setOnClickListener {
            val keyWord = binding.searchET.text.toString()
            searchMarkByTitle(keyWord)
        }
        binding.btnLayer.setOnClickListener {
            showOptionMenu()
        }
        binding.btnTarget.setOnClickListener {
            val lagLng = LatLng(currentLocation.latitude, currentLocation.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lagLng, 20f))
        }
    }
    /*Tạo menu lựa chọn loại bản đồ*/
    private fun showOptionMenu() {
        val popupMenu = PopupMenu(this, binding.btnLayer)
        val meniInflater = popupMenu.menuInflater
        meniInflater.inflate(R.menu.option_menu_layer, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.layer_1 -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }
                R.id.layer_2 -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    true
                }
                R.id.layer_3 -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }
                R.id.layer_4 -> {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }
                else -> false
            }

        }
        popupMenu.show()
    }
    /*Tìm kiếm điểm đánh dấu theo tiêu đề*/
    private fun searchMarkByTitle(keyWord: String) {
            val marker = markers[keyWord]
            if (marker != null) {
                val latLng = marker.position
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
            }
        }

    private fun observerMark() {
        viewModel.landMarks.observe(this) { listLandMark ->
            if (listLandMark != null) {
                for (landmark in listLandMark) {
                    addMarkerToMap(landmark)
                }
            }
        }
    }
    /*Thêm điểm đã đánh dấu lên bản đồ*/
    private fun addMarkerToMap(landmark: Landmark) {
        val location = LatLng(landmark.latitude, landmark.longitude)
        val market = mMap.addMarker(
            MarkerOptions().position(location)
                .title(landmark.title)
                .snippet(landmark.description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
        market?.tag = landmark.email
        markers[landmark.title] = market!!

        mMap.setOnMarkerClickListener { clickedMarker ->
            clickedMarker.showInfoWindow()
            true
        }
        mMap.setOnMapLoadedCallback {
            market.showInfoWindow()
        }
    }

    private fun getCurrentLocation() {
        /*Kiểm tra quyền truy cập vị trí*/
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }

        val getLocation =
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    Toast.makeText(
                        this,
                        currentLocation.latitude.toString() + "" + currentLocation.longitude.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(currentLatLng).title("I am here!")
        p0.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        p0.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 20f))
        p0.addMarker(markerOptions)
        p0.setInfoWindowAdapter(CustomShowMark(LayoutInflater.from(this)))

    }

    /*Tạo dialog thêm điểm đánh dấu tại vị trí hiện tại*/
    private fun showAddMarkDiaLog(p0: GoogleMap, latLng: LatLng) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val builder = AlertDialog.Builder(this, R.style.ThemeCustom)
            val dialogBinding = AddMarkLocationBinding.inflate(layoutInflater)
            builder.setView(dialogBinding.root)
            val dialog = builder.create()
            dialog.show()
            dialogBinding.btnAdd.setOnClickListener {
                val title = dialogBinding.titleEt.text.toString()
                val description = dialogBinding.descriptionEt.text.toString()
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    val markerOptions =
                        MarkerOptions().position(latLng).title(title).snippet(description)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    p0.addMarker(markerOptions)

                    viewModel.saveLocationToFirebase(latLng, title, description,
                        onSuccess = {
                            Log.d("TAG", "Location saved to Firebase")
                            Toast.makeText(this, "Location saved to Firebase", Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        },
                        onFailure = {
                            Log.d("TAG", it)
                            Toast.makeText(
                                this,
                                "Failed to save location to Firebase",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            dialog.dismiss()
                        })
                }
            }


        }
    }
}