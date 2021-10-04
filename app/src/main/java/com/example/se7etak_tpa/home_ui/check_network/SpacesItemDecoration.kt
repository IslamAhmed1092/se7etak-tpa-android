package com.example.se7etak_tpa.home_ui.check_network

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.bottom = 0
        outRect.top = 0

        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.right = space
        } else {
            outRect.right = 0
        }
    }
}