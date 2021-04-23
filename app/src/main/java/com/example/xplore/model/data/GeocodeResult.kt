package com.example.xplore.model.data

import com.squareup.moshi.Json

data class GeocodeResult(
    @Json(name = "geometry")
    val geometry: ZipLocation
)
