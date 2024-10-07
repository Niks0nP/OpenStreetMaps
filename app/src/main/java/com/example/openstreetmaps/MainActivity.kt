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
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
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
        mapSettings = MapSettings(mapView,this, myLocationOverlay)
        mapSettings.createMap()
        mapSettings.onClick = { marker ->
            bottomSheetDialog(marker)
            true
        }

        btnUserLocation()
        btnZoomIn()
        btnZoomOut()

        val eventOverlay = MapEventsOverlay(mapSettings)
        mapView.overlays.add(eventOverlay)
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
            myLocationOverlay.enableMyLocation()
            mapSettings.goToUserLocation()
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

    private fun bottomSheetDialog(marker: Marker) {
        val view = layoutInflater.inflate(R.layout.bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
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
                mapSettings.currentLocation(this@MainActivity)
                mapSettings.userLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getInstance().load(
                    applicationContext,
                    sharedPreference
                )
                mapSettings.currentLocation(this@MainActivity)
                mapSettings.userLocation()
            }
            else -> {
                Toast.makeText(this, R.string.location_permission_text, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}