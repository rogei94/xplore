package com.example.xplore.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xplore.model.data.RouteInfo
import com.example.xplore.model.repository.abstraction.DirectionRepository
import com.example.xplore.util.DataStatus
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.cos

typealias OnLocation = (location: LatLng) -> Unit

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val placesClient: PlacesClient,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val directionRepository: DirectionRepository
) : ViewModel() {

    private val _placesList = MutableLiveData<List<AutocompletePrediction>>()
    val placesList: LiveData<List<AutocompletePrediction>>
        get() = _placesList

    private val _errorPlaces = MutableLiveData<String>()
    val errorPlaces: LiveData<String> get() = _errorPlaces

    private val _dataStatus = MutableLiveData(DataStatus.DEFAULT)
    val dataStatus: LiveData<DataStatus> get() = _dataStatus

    private val _routeInfo = MutableLiveData<RouteInfo>()
    val routeInfo: LiveData<RouteInfo> get() = _routeInfo

    var place = ""
    var zipCode = ""
    var nearBy = ""

    fun clearStatus() {
        _dataStatus.value = DataStatus.DEFAULT
    }

    fun onSearchButtonClicked() {
        if (validateFields()) {
            requestLocation {
                searchPlaces(it)
            }
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
                _placesList.value = response.autocompletePredictions
                _dataStatus.value = DataStatus.SUCCESS
            }.addOnFailureListener { exception ->
                _errorPlaces.value = exception.message
            }
    }

    private fun validateFields(): Boolean {
        return place != "" || zipCode != "" || nearBy != ""
    }

    fun getGoogleDirection(place: AutocompletePrediction) {
        requestLocation {
            val placeFields = listOf(Place.Field.LAT_LNG)
            val request = FetchPlaceRequest.newInstance(place.placeId, placeFields)

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    viewModelScope.launch {
                        val direction = directionRepository.getGoogleDirections(
                            "${it.latitude},${it.longitude}",
                            "${response.place.latLng?.latitude},${response.place.latLng?.longitude}"
                        )
                        val polyline = direction.routes[0].overviewPolyline.points
                        polyline?.let {
                            val polylineLatLng = polylineToLatLng(it)
                            _routeInfo.value = RouteInfo(polylineLatLng, place.getPrimaryText(null).toString())
                        }
                    }
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Log.e(TAG, "Place not found: ${exception.message}")
                    }
                }
        }
    }

    private fun polylineToLatLng(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded: MutableList<LatLng> = ArrayList()
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            decoded.add(
                LatLng(
                    lat / 100000.0, lng / 100000.0
                )
            )
        }
        return decoded
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(someFunction: OnLocation) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                someFunction(LatLng(location.latitude, location.longitude))
            }.addOnFailureListener {
                Log.e("Xplore", "Error: ${it.message}")
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