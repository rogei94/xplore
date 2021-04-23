package com.example.xplore.model.data

import com.squareup.moshi.Json

data class GeocodeResponse(
    @Json(name = "results")
    val results: List<GeocodeResult>
)
