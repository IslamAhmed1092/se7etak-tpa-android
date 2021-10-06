package com.example.se7etak_tpa.home_ui.check_network

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.MapFilter
import com.example.se7etak_tpa.databinding.MapFilterItemBinding

class FiltersListAdapter(private val filtersList: List<MutableLiveData<MapFilter>>,
                         private val viewLifecycleOwner: LifecycleOwner,
                         private val onFilterClicked: (MapFilter) -> Unit) :
    RecyclerView.Adapter<FiltersListAdapter.FiltersListViewHolder>() {



    class FiltersListViewHolder(var binding: MapFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun getItemCount(): Int {
        return filtersList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersListViewHolder {
        return FiltersListViewHolder(
            MapFilterItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FiltersListViewHolder, position: Int) {
        val filter = filtersList[position]
        holder.binding.filter = filter
        holder.binding.lifecycleOwner = viewLifecycleOwner
        holder.binding.btnFilter.setOnClickListener {
            if(position == 0) {
                filtersList.forEach { filter -> filter.value = filter.value?.copy(isEnabled = true) }
            } else {
                for (i in filtersList.indices) {
                    if (i == position)
                        filtersList[i].value = filtersList[i].value?.copy(isEnabled = true)
                    else
                        filtersList[i].value = filtersList[i].value?.copy(isEnabled = false)
                }
            }
            onFilterClicked(filter.value!!)
        }
    }

}