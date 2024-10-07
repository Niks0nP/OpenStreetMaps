package com.example.openstreetmaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapSettings(private val mapView: MapView,
                  private val context: Context,
                  private val myLocationOverlay: MyLocationNewOverlay)
    : MapEventsReceiver {

        var onClick: ((Marker) -> Boolean)? = null

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

    fun currentLocation(activity: MainActivity) {
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
                val currentLocation = GeoPoint(location.latitude, location.longitude)
                mapView.controller.animateTo(currentLocation)
            } else
                Toast.makeText(
                    context,
                    R.string.no_access_gps,
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        Toast.makeText(context, R.string.info_tap_on_map, Toast.LENGTH_SHORT).show()
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        showPoint(p)
        return true
    }

    private fun showPoint(position: GeoPoint?) {
        val marker = Marker(mapView)
        marker.position = position
        marker.icon = ContextCompat.getDrawable(context, R.drawable.icon_marker)
        marker.title = context.getString(R.string.name)
        marker.setOnMarkerClickListener { marker, _ ->
            onClick?.invoke(marker) ?: false
        }
        mapView.overlays.add(marker)
        mapView.invalidate()
    }
}