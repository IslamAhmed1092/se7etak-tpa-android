package com.example.se7etak_tpa

import android.graphics.Color
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.data.MapFilter

@BindingAdapter("btnStyle")
fun bindButton(btn: AppCompatButton, filter: MapFilter) {
    if(filter.isEnabled){
        btn.backgroundTintList = ContextCompat.getColorStateList(btn.context, filter.backgroundColorID)
        btn.setTextColor(filter.textColorID)
    } else {
        btn.backgroundTintList = null
        btn.setTextColor(Color.BLACK)
    }
}