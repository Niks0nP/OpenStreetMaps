package com.example.openstreetmaps

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.openstreetmaps.databinding.ActivityMainBinding
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mapView: MapView
    private lateinit var mapSettings : MapSettings
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreference = this.getSharedPreferences("map_preferences", Context.MODE_PRIVATE)

        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        mapView = binding.mapView
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        mapSettings = MapSettings(mapView, myLocationOverlay)
        mapSettings.createMap()

        btnUserLocation()
        btnZoomIn()
        btnZoomOut()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        myLocationOverlay.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        myLocationOverlay.disableMyLocation()
    }

    private fun btnUserLocation() {
        val buttonUserLocation = binding.currentUserLocation
        buttonUserLocation.setOnClickListener {
            mapSettings.goToUserLocation()
            myLocationOverlay.enableMyLocation()
        }
    }

    private fun btnZoomIn() {
        val btnZoomIn = binding.plusZoomBtn
        btnZoomIn.setOnClickListener {
            mapView.controller.zoomIn()
        }
    }

    private fun btnZoomOut() {
        val btnZoomOut = binding.minusZoomBtn
        btnZoomOut.setOnClickListener {
            mapView.controller.zoomOut()
        }
    }

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                getInstance().load(
                    applicationContext,
                    sharedPreference
                )
                mapSettings.currentLocation(this, this@MainActivity)
                mapSettings.userLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                mapSettings.currentLocation(this, this@MainActivity)
            }
            else -> {
                Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_SHORT).show()
            }
        }
    }
}