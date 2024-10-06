package com.example.openstreetmaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapSettings(private val mapView: MapView, private val myLocationOverlay: MyLocationNewOverlay) {

    fun createMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.isHorizontalMapRepetitionEnabled = false
        mapView.isVerticalMapRepetitionEnabled = false
        mapView.setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude,
            MapView.getTileSystem().minLatitude,
            0
        )
        mapView.setScrollableAreaLimitLongitude(
            MapView.getTileSystem().minLongitude,
            MapView.getTileSystem().maxLongitude,
            0
        )
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        val startPoint = GeoPoint(55.753887, 37.620625)
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(14.0)
    }

    fun userLocation() {
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)
    }

    fun goToUserLocation() {
        mapView.controller.animateTo(myLocationOverlay.myLocation)
    }

    fun currentLocation(context: Context, activity: MainActivity) {
        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
        fusedLocationProvider.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                userLocation()
                val currentLocation = GeoPoint(location.latitude, location.longitude)
                mapView.controller.animateTo(currentLocation)
            } else
                Toast.makeText(
                    context,
                    "Нет доступа к местоположению",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

}