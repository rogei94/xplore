package com.example.xplore.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.xplore.R
import com.example.xplore.databinding.FragmentSearchBinding
import com.example.xplore.ui.MainViewModel
import com.example.xplore.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val searchViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel.placesList.observe(this, { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    Toast.makeText(context, "Getting places...", Toast.LENGTH_SHORT).show()
                }
                is DataState.Success -> {
                    view?.findNavController()?.navigate(R.id.resultsFragment)
                }
                is DataState.Error -> {
                    Toast.makeText(context, "There is an error...", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchViewModel = searchViewModel
        return binding.root
    }

}