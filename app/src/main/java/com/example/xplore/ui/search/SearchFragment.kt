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
import com.example.xplore.util.DataStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribeObservers()
    }

    private fun subscribeObservers() {
        mainViewModel.dataStatus.observe(this, { status ->
            when (status) {
                DataStatus.SUCCESS -> {
                    view?.findNavController()?.navigate(R.id.resultsFragment)
                }
                DataStatus.LOADING -> {
                    Toast.makeText(context, "Getting places...", Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        })
        mainViewModel.errorPlaces.observe(this, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchViewModel = mainViewModel
        return binding.root
    }

}