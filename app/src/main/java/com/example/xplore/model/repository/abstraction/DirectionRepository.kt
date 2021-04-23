package com.example.xplore.model.repository.abstraction

import com.example.xplore.model.data.Direction

interface DirectionRepository {

    suspend fun getGoogleDirections(
        originLatLng: String,
        destinationLatLng: String
    ): Direction

}