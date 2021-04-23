package com.example.xplore.ui.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.xplore.databinding.FragmentMapsBinding
import com.example.xplore.model.data.RouteInfo
import com.example.xplore.ui.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.model.PlacesSearchResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ceil

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    private lateinit var placeList: List<PlacesSearchResult>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        return binding.root
    }

    private fun subscribeObservers() {
        mainViewModel.routeInfo.observe(viewLifecycleOwner, { routeInfo ->
            drawRoute(routeInfo)
        })
        mainViewModel.placeInfo.observe(viewLifecycleOwner, { placeInfo ->
            placeInfo.latLngPlace?.let { latLng ->
                drawMarker(latLng, placeInfo.namePlace)
            }
        })
        mainViewModel.placesList.observe(viewLifecycleOwner, {
            placeList = it.results.toList()
        })
    }

    private fun drawRoute(routeInfo: RouteInfo) {
        if (!this::mMap.isInitialized) return

        val polylineOptions = PolylineOptions()
        val polylinePaths = ArrayList<Polyline>()

        val polylineLatLng = routeInfo.polylineLatLng

        polylineOptions.addAll(polylineLatLng).apply {
            geodesic(true)
            width(5f)
            color(Color.BLUE)
        }
        polylinePaths.add(mMap.addPolyline(polylineOptions))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polylineLatLng.first(), 14f))
        drawMarker(polylineLatLng.first(), "Current Location")
        drawMarker(polylineLatLng.last(), routeInfo.placeName)
    }

    private fun drawMarker(position: LatLng, placeName: String) {
        mMap.addMarker(
            MarkerOptions()
                .position(position)
                .title(placeName)

        )
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MapsInitializer.initialize(context)
        binding.mapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap ?: return
        val place = arguments?.get("place") as PlacesSearchResult?
        subscribeObservers()
        if (place != null) {
            mainViewModel.getGoogleDirection(place)
        } else {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                drawCircle(location)
                drawPlacesMarkers()
            }.addOnFailureListener {
                Log.e("Xplore", "Error: ${it.message}")
            }
    }

    private fun drawPlacesMarkers() {
        placeList.forEach { place ->
            drawMarker(LatLng(place.geometry.location.lat, place.geometry.location.lng), place.name)
        }
    }

    private fun drawCircle(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        val nearBy = if (mainViewModel.nearBy != "0") mainViewModel.nearBy.toDouble() else 5.0
        mMap.addCircle(
            CircleOptions()
                .center(currentLocation)
                .radius(ceil(nearBy * 1609.344))
                .strokeColor(Color.RED)
        )
        drawMarker(currentLocation, "Current Location")
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f))
    }

}