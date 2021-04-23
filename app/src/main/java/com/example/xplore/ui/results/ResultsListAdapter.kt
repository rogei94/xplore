package com.example.xplore.ui.results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.xplore.databinding.LayoutResultItemBinding
import com.google.android.libraries.places.api.model.AutocompletePrediction

class ResultsListAdapter(
    private val mInteractor: ResultsListAdapterInteractor
) : ListAdapter<AutocompletePrediction, ResultsListAdapter.ResultsViewHolder>(ResultsDiff()) {


    interface ResultsListAdapterInteractor {
        fun onShowRouteClicked(autocompletePrediction: AutocompletePrediction)
    }

    inner class ResultsViewHolder(
        private val itemBinding: LayoutResultItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(autocompletePrediction: AutocompletePrediction) {
            itemBinding.textPlaceTitle.text = autocompletePrediction.getPrimaryText(null).toString()
            itemBinding.root.setOnClickListener {
                mInteractor.onShowRouteClicked(autocompletePrediction)
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

class ResultsDiff : DiffUtil.ItemCallback<AutocompletePrediction>() {
    override fun areItemsTheSame(
        oldItem: AutocompletePrediction,
        newItem: AutocompletePrediction
    ): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(
        oldItem: AutocompletePrediction,
        newItem: AutocompletePrediction
    ): Boolean {
        return oldItem.toString() == newItem.toString()
    }

}