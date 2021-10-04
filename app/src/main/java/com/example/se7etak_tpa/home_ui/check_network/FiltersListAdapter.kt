package com.example.se7etak_tpa.home_ui.check_network

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.MapFilter
import com.example.se7etak_tpa.databinding.MapFilterItemBinding

class FiltersListAdapter(private val context: Context, private val onFilterClicked: (MapFilter) -> Unit) :
    RecyclerView.Adapter<FiltersListAdapter.FiltersListViewHolder>() {

    private val filtersList = listOf(
        MapFilter(context.getString(R.string.مستشفيات), R.color.مستشفيات),
        MapFilter(context.getString(R.string.صيدليات), R.color.صيدليات),
        MapFilter(context.getString(R.string.عيادات), R.color.عيادات),
        MapFilter(context.getString(R.string.مجمع_عيادات), R.color.مجمع_عيادات),
        MapFilter(context.getString(R.string.اسنان), R.color.اسنان),
        MapFilter(context.getString(R.string.معامل), R.color.معامل, Color.GRAY),
        MapFilter(context.getString(R.string.مراكز_متخصصة), R.color.مراكز_متخصصة, Color.GRAY),
        MapFilter(context.getString(R.string.مراكز_علاج_طبيعي), R.color.مراكز_علاج_طبيعي, Color.GRAY),
        MapFilter(context.getString(R.string.مراكز_اشعة), R.color.مراكز_اشعة),
        MapFilter(context.getString(R.string.مركز_بصريات), R.color.مركز_بصريات)
    )

    class FiltersListViewHolder(private var binding: MapFilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: MapFilter, context: Context, onFilterClicked: (MapFilter) -> Unit) {
            binding.filter = filter
            binding.btnFilter.setOnClickListener {
                filter.isEnabled = !filter.isEnabled
                if(filter.isEnabled) {
                    binding.btnFilter.backgroundTintList = ContextCompat.getColorStateList(context, filter.backgroundColorID)
                    binding.btnFilter.setTextColor(filter.textColorID)
                } else {
                    binding.btnFilter.backgroundTintList = null
                    binding.btnFilter.setTextColor(Color.BLACK)
                }

                onFilterClicked(filter)
            }
            if(filter.isEnabled) {
                binding.btnFilter.backgroundTintList = ContextCompat.getColorStateList(context, filter.backgroundColorID)
                binding.btnFilter.setTextColor(filter.textColorID)
            } else {
                binding.btnFilter.backgroundTintList = null
                binding.btnFilter.setTextColor(Color.BLACK)
            }
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

    override fun onBindViewHolder(holder: FiltersListViewHolder, position: Int) {
        holder.bind(filtersList[position], context, onFilterClicked)
    }

}