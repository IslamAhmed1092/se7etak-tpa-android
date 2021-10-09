package com.example.se7etak_tpa.home_ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.databinding.HomeRequestItemBinding

class RequestsAdapter(private val listener: RequestItemClickListener): ListAdapter<HomeRequest, RequestsAdapter.RequestsViewHolder>(DiffCallback) {

    class RequestsViewHolder(private var binding: HomeRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(request: HomeRequest) {
            binding.request = request
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestsViewHolder {
        return RequestsViewHolder(
            HomeRequestItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val request = getItem(position)
        holder.itemView.setOnClickListener{listener.onClickListener(request.id)}
        holder.bind(request)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<HomeRequest>() {
        override fun areItemsTheSame(oldItem: HomeRequest, newItem: HomeRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomeRequest, newItem: HomeRequest): Boolean {
            return (oldItem.type == newItem.type) && (oldItem.date == newItem.date) && (oldItem.state == newItem.state)
        }
    }
}