package com.example.xplore.model.data

import com.squareup.moshi.Json

data class OverviewPolyline(
    @Json(name = "points")
    val points: String?
)