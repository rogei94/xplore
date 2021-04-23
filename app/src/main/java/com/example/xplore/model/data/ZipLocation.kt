package com.example.xplore.model.data

import com.squareup.moshi.Json

data class ZipLocation(
    @Json(name = "location")
    val zipLocation: ZipLatLng

)
