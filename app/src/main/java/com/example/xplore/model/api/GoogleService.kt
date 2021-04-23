package com.example.xplore.model.api

import com.example.xplore.model.data.Direction
import com.example.xplore.model.data.GeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleService {

    @GET("directions/json?")
    suspend fun getGoogleDirections(
        @Query("origin") originAddress: String,
        @Query("destination") destinationAddress: String,
        @Query("key") apiKey: String
    ): Direction

    @GET("geocode/json?")
    suspend fun getZipcodeLatLng(
        @Query("address") zipcode: String,
        @Query("key") apiKey: String
    ) : GeocodeResponse

    //@GET("")
}