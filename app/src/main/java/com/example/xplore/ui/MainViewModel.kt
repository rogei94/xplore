package com.example.xplore.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xplore.BuildConfig.MAPS_API_KEY
import com.example.xplore.model.data.PlaceInfo
import com.example.xplore.model.data.RouteInfo
import com.example.xplore.model.repository.abstraction.DirectionRepository
import com.example.xplore.model.repository.abstraction.GeocodeRepository
import com.example.xplore.util.DataStatus
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import com.google.maps.model.PlacesSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.cos

typealias OnLocation = (location: LatLng) -> Unit

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val directionRepository: DirectionRepository,
    private val geocodeRepository: GeocodeRepository
) : ViewModel() {

    private val _placesList = MutableLiveData<PlacesSearchResponse>()
    val placesList: LiveData<PlacesSearchResponse>
        get() = _placesList

    private val _errorPlaces = MutableLiveData<String>()
    val errorPlaces: LiveData<String> get() = _errorPlaces

    private val _dataStatus = MutableLiveData(DataStatus.DEFAULT)
    val dataStatus: LiveData<DataStatus> get() = _dataStatus

    private val _routeInfo = MutableLiveData<RouteInfo>()
    val routeInfo: LiveData<RouteInfo> get() = _routeInfo

    private val _placeInfo = MutableLiveData<PlaceInfo>()
    val placeInfo: LiveData<PlaceInfo> get() = _placeInfo

    private val _latLngZipcode = MutableLiveData<LatLng>()
    val latLngZipcode: LiveData<LatLng> get() = _latLngZipcode

    var place = ""
    var zipCode = ""
    var nearBy = "0"

    fun clearStatus() {
        _dataStatus.value = DataStatus.DEFAULT
    }

    fun onSearchButtonClicked() {
        if (validateFields()) {
            if (zipCode != "") {
                viewModelScope.launch {
                    val zipCodeLatLng = geocodeRepository.getZipcodeLatLng(zipCode)
                    searchPlaces(
                        LatLng(
                            zipCodeLatLng.results.first().geometry.zipLocation.zipLat,
                            zipCodeLatLng.results.first().geometry.zipLocation.zipLng
                        )
                    )
                }
            } else {
                requestLocation {
                    searchPlaces(it)
                }
            }
        } else {
            Log.e("XPLORE", "You need to enter schools, restaurants, etc")
        }
    }

    private fun searchPlaces(location: LatLng) {
        val distanceInMeters = if (nearBy != "0") nearBy.toInt() * 1609.344 else 5.0 * 1609.344

        val geoApiContext = GeoApiContext.Builder().apiKey(MAPS_API_KEY).build()
        try {
            val places = PlacesApi.nearbySearchQuery(
                geoApiContext, com.google.maps.model.LatLng(location.latitude, location.longitude)
            ).keyword(place)
                .radius(distanceInMeters.toInt())
                .await()
            _placesList.value = places
            _dataStatus.value = DataStatus.SUCCESS
        } catch (e: Exception) {
            _errorPlaces.value = e.message
        }
    }

    private fun validateFields(): Boolean {
        return place != ""
    }

    fun validateZipcode(place: PlacesSearchResult) {
        if (zipCode != "") {
            viewModelScope.launch {
                val zipCodeLatLng = getLatLngFromZipcode()
                getGoogleDirection(
                    "${zipCodeLatLng.latitude},${zipCodeLatLng.longitude}",
                    "${place.geometry.location.lat},${place.geometry.location.lng}",
                    place.name
                )
            }
        } else {
            requestLocation {
                viewModelScope.launch {
                    getGoogleDirection(
                        "${it.latitude},${it.longitude}",
                        "${place.geometry.location.lat},${place.geometry.location.lng}",
                        place.name
                    )
                }
            }
        }
    }

    private suspend fun getLatLngFromZipcode(): LatLng {
        val zipCodeLatLng = geocodeRepository.getZipcodeLatLng(zipCode)
        return LatLng(
            zipCodeLatLng.results.first().geometry.zipLocation.zipLat,
            zipCodeLatLng.results.first().geometry.zipLocation.zipLng
        )
    }

    fun getLocationFromZipCode() {
        viewModelScope.launch {
            _latLngZipcode.value = getLatLngFromZipcode()
        }
    }

    private suspend fun getGoogleDirection(origin: String, destination: String, placeName: String) {
        val direction = directionRepository.getGoogleDirections(
            origin,
            destination
        )
        val polyline = direction.routes[0].overviewPolyline.points
        polyline?.let {
            val polylineLatLng = polylineToLatLng(it)
            _routeInfo.value =
                RouteInfo(polylineLatLng, placeName)
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

}