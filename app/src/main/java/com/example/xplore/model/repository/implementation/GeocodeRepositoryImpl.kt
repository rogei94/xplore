package com.example.xplore.model.repository.implementation

import com.example.xplore.model.api.GoogleService
import com.example.xplore.model.data.GeocodeResponse
import com.example.xplore.model.repository.abstraction.GeocodeRepository
import javax.inject.Inject

class GeocodeRepositoryImpl
@Inject
constructor(
    private val googleService: GoogleService
) : GeocodeRepository {

    override suspend fun getZipcodeLatLng(address: String): GeocodeResponse {
        return googleService.getZipcodeLatLng(address)
    }

}