package com.example.openstreetmaps

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.openstreetmaps.databinding.ActivityMainBinding
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration.*

class MainActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMainBinding
    private lateinit var mapSettings : MapSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreference = this.getSharedPreferences("map_preferences", Context.MODE_PRIVATE)
        getInstance().load(
            applicationContext,
            sharedPreference
        )

        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        mapView = binding.mapView
        mapSettings = MapSettings(mapView)
        mapSettings.createMap()

        val buttonUserLocation = binding.currentUserLocation
        buttonUserLocation.setOnClickListener {
            mapSettings.goToUserLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private val locationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                mapSettings.userLocation(this)
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
            else -> {
                Toast.makeText(this, "Нет доступа к местоположению", Toast.LENGTH_SHORT).show()
            }
        }
    }
}