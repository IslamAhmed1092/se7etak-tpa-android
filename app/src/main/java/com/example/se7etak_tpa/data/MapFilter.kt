package com.example.se7etak_tpa.data

import android.graphics.Color

data class MapFilter(
    val name: String,
    val backgroundColorID: Int,
    val textColorID: Int = Color.WHITE,
    var isEnabled: Boolean = false
)