package com.example.xplore.ui.results

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xplore.R
import com.example.xplore.databinding.FragmentResultsBinding
import com.example.xplore.ui.MainViewModel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.model.PlacesSearchResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultsFragment : Fragment(), ResultsListAdapter.ResultsListAdapterInteractor {

    private lateinit var binding: FragmentResultsBinding
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val resultsListAdapter = ResultsListAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.clearStatus()
        mainViewModel.placesList.observe(viewLifecycleOwner, { list ->
            list?.let {
                initRecyclerView(it.results.toList())
            }
        })
    }

    private fun initRecyclerView(listPlacesSearchResult: List<PlacesSearchResult>) {
        binding.recyclerViewPlaces.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultsListAdapter
        }
        resultsListAdapter.submitList(listPlacesSearchResult)
    }


    override fun onShowRouteClicked(placesSearchResult: PlacesSearchResult) {
        val place = bundleOf("place" to placesSearchResult)
        view?.findNavController()?.navigate(R.id.mapFragment, place)
    }

}