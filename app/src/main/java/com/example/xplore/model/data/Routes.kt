package com.example.xplore.model.data

import com.squareup.moshi.Json

data class Routes(
    @Json(name = "overview_polyline")
    val overviewPolyline : OverviewPolyline
)
