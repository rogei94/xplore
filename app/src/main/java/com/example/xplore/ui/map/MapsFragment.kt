package com.example.xplore.ui.map

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.xplore.databinding.FragmentMapsBinding
import com.example.xplore.model.data.RouteInfo
import com.example.xplore.ui.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var mMap: GoogleMap

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
        val place = arguments?.getParcelable<AutocompletePrediction>("place")
        place?.let {
            mainViewModel.getGoogleDirection(place)
        }
        subscribeObservers()
    }

}