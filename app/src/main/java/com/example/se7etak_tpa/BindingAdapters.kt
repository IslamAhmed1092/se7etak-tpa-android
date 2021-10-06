package com.example.se7etak_tpa

import android.graphics.Color
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.data.MapFilter
import com.example.se7etak_tpa.home_ui.home.RequestsAdapter
import java.text.SimpleDateFormat
import java.util.*

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

@BindingAdapter("backgroundGradient")
fun bindConstraintLayoutBackground(layout: ConstraintLayout, state: String) {
    layout.background = when(state) {
        "Pending" -> ContextCompat.getDrawable(layout.context, R.drawable.gradient_blue)
        "Approved" -> ContextCompat.getDrawable(layout.context, R.drawable.gradient_green)
        "Refused" -> ContextCompat.getDrawable(layout.context, R.drawable.gradient_red)
        else -> ContextCompat.getDrawable(layout.context, R.drawable.gradient_blue)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<HomeRequest>?) {
    val adapter = recyclerView.adapter as RequestsAdapter
    adapter.submitList(data)
}

@BindingAdapter("dateText")
fun bindTextViewDate(textView: TextView, date: String) {
    val datePart = date.substringBefore(" ")
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val output = SimpleDateFormat("dd/MM/yyyy EEEE", Locale.getDefault())
    val dateObject = formatter.parse(datePart)

    dateObject?.let {
        textView.text = output.format(dateObject)
    }

}
@BindingAdapter("timeText")
fun bindTextViewTime(textView: TextView, date: String) {
    textView.text = date.substringAfter(" ")
}