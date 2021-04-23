package com.example.xplore.model.data

import com.squareup.moshi.Json

data class ZipLatLng(
    @Json(name = "lat")
    val zipLat: Double,
    @Json(name = "lng")
    val zipLng: Double
)