package com.example.xplore.model.data

import com.squareup.moshi.Json


data class Direction(
    @Json(name = "routes")
    val routes: List<Routes>
)
