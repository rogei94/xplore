package com.example.xplore.model.api

import com.example.xplore.model.data.Direction
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionService {

    @GET("json?")
    suspend fun getGoogleDirections(
        @Query("origin") originAddress: String,
        @Query("destination") destinationAddress: String,
        @Query("key") apiKey: String
    ): Direction
}