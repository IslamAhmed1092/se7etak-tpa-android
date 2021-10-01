package com.example.se7etak_tpa.home_ui.check_network

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.MapFilter
import com.example.se7etak_tpa.databinding.MapFilterItemBinding

class FiltersListAdapter :
    RecyclerView.Adapter<FiltersListAdapter.FiltersListViewHolder>() {

    private val filtersList = listOf(
        MapFilter("Restaurants", R.drawable.ic_hospital),
        MapFilter("Coffee", R.drawable.ic_hospital),
        MapFilter("Shopping", R.drawable.ic_hospital),
        MapFilter("Gas", R.drawable.ic_hospital),
        MapFilter("Hospitals", R.drawable.ic_hospital)
    )

    class FiltersListViewHolder(private var binding: MapFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: MapFilter) {
            binding.filter = filter
        }
    }

    override fun getItemCount(): Int {
        return filtersList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersListViewHolder {
        return FiltersListViewHolder(
            MapFilterItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: FiltersListAdapter.FiltersListViewHolder, position: Int) {
        holder.bind(filtersList[position])
    }

}