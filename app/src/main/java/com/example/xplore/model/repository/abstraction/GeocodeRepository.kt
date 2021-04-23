package com.example.xplore.model.repository.abstraction

import com.example.xplore.model.data.GeocodeResponse

interface GeocodeRepository {
    suspend fun getZipcodeLatLng(address: String): GeocodeResponse
}