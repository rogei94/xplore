package com.example.xplore.model.data

import com.google.android.gms.maps.model.LatLng

data class RouteInfo(
    val polylineLatLng: List<LatLng>,
    val placeName: String
)
