package com.example.xplore.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.xplore.util.DataState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.cos

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val placesClient: PlacesClient,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : ViewModel() {

    private val _placesList = MutableLiveData<DataState<List<AutocompletePrediction>>>()
    val placesList: LiveData<DataState<List<AutocompletePrediction>>>
        get() = _placesList

    var place = ""
    var zipCode = ""
    var nearBy = ""

    fun onSearchButtonClicked() {
        if (validateFields()) {
            requestLocation()
        } else {
            Log.e("XPLORE", "Campo vacio")
        }
    }

    private fun searchPlaces(location: LatLng) {

        val bounds = if (nearBy != "") getRectBoundsByRadius(location, nearBy.toInt())
        else getRectBoundsByRadius(location, 200)

        val request = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(bounds)
            .setOrigin(location)
            .setCountries("US")
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setQuery(place)
            .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _placesList.value = DataState.Success(response.autocompletePredictions)
            }.addOnFailureListener { exception ->
                _placesList.value = exception.message?.let { DataState.Error(it) }
            }
    }

    private fun validateFields(): Boolean {
        return place != "" || zipCode != "" || nearBy != ""
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                searchPlaces(LatLng(location.latitude, location.longitude))
            }.addOnFailureListener { exception ->
                Log.e("XPLORE", "no encuentra ubicacion usuario: ${exception.message}")
            }
    }

    private fun getRectBoundsByRadius(location: LatLng, mDistanceInMeters: Int): LocationBias {
        val latRadian = Math.toRadians(location.latitude)
        val degLatKm = 110.574235
        val degLongKm = 110.572833 * cos(latRadian)
        val deltaLat = mDistanceInMeters / 1000.0 / degLatKm
        val deltaLong = mDistanceInMeters / 1000.0 / degLongKm
        val minLat: Double = location.latitude - deltaLat
        val minLong: Double = location.longitude - deltaLong
        val maxLat: Double = location.latitude + deltaLat
        val maxLong: Double = location.longitude + deltaLong
        return RectangularBounds.newInstance(LatLng(minLat, minLong), LatLng(maxLat, maxLong))
    }

}