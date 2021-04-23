package com.example.xplore.ui.results

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xplore.R
import com.example.xplore.databinding.FragmentResultsBinding
import com.example.xplore.ui.MainViewModel
import com.example.xplore.util.DataState
import com.google.android.libraries.places.api.model.AutocompletePrediction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultsFragment : Fragment(), ResultsListAdapter.ResultsListAdapterInteractor {

    private lateinit var binding: FragmentResultsBinding
    private val searchViewModel by activityViewModels<MainViewModel>()
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
        searchViewModel.placesList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    initRecyclerView(dataState.response)
                }
            }
        })
    }

    private fun initRecyclerView(listAutocompletePrediction: List<AutocompletePrediction>) {
        binding.recyclerViewPlaces.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resultsListAdapter
        }
        resultsListAdapter.submitList(listAutocompletePrediction)
    }


    override fun onShowRouteClicked() {
        view?.findNavController()?.navigate(R.id.mapFragment)
    }

}