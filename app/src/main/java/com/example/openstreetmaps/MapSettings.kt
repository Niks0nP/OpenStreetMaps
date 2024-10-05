package com.example.openstreetmaps

import android.content.Context
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapSettings(private val mapView: MapView) {
    private lateinit var myLocationOverlay: MyLocationNewOverlay

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

        val startPoint = GeoPoint(55.753887, 37.620625)
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(14.0)
    }

    fun userLocation(context: Context) {
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)
    }

    fun goToUserLocation() {
        mapView.controller.animateTo(myLocationOverlay.myLocation)
    }

}