package com.example.xplore.model.repository.implementation

import com.example.xplore.BuildConfig.MAPS_API_KEY
import com.example.xplore.model.api.DirectionService
import com.example.xplore.model.data.Direction
import com.example.xplore.model.repository.abstraction.DirectionRepository
import javax.inject.Inject


class DirectionRepositoryImpl
@Inject
constructor(
    private val directionService: DirectionService
) : DirectionRepository {
    override suspend fun getGoogleDirections(
        originLatLng: String,
        destinationLatLng: String,
    ): Direction {
        return directionService.getGoogleDirections(
            originLatLng,
            destinationLatLng,
            MAPS_API_KEY
        )
    }
}