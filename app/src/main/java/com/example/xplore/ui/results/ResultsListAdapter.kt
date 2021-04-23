package com.example.xplore.ui.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xplore.databinding.LayoutResultItemBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.model.PlacesSearchResult

class ResultsListAdapter(
    private val mInteractor: ResultsListAdapterInteractor
) : ListAdapter<PlacesSearchResult, ResultsListAdapter.ResultsViewHolder>(ResultsDiff()) {


    interface ResultsListAdapterInteractor {
        fun onShowRouteClicked(placesSearchResult: PlacesSearchResult)
    }

    inner class ResultsViewHolder(
        private val itemBinding: LayoutResultItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(placesSearchResult: PlacesSearchResult) {
            itemBinding.textPlaceTitle.text = placesSearchResult.name
            itemBinding.root.setOnClickListener {
                mInteractor.onShowRouteClicked(placesSearchResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsViewHolder {
        return ResultsViewHolder(
            LayoutResultItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ResultsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ResultsDiff : DiffUtil.ItemCallback<PlacesSearchResult>() {
    override fun areItemsTheSame(
        oldItem: PlacesSearchResult,
        newItem: PlacesSearchResult
    ): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(
        oldItem: PlacesSearchResult,
        newItem: PlacesSearchResult
    ): Boolean {
        return oldItem.toString() == newItem.toString()
    }

}