package com.example.se7etak_tpa.data

import com.google.android.gms.maps.model.Marker
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Provider (
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("branchType")
    val branchType: String = "",
    @SerializedName("mainSpeciality")
    val mainSpeciality: String = "",
    @SerializedName("secondarySpeciality")
    val secondarySpeciality: String = "",
    @SerializedName("governorate")
    val governorate: String = "",
    @SerializedName("city")
    val city: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("phoneNumber1")
    val phoneNumber1: String = "",
    @SerializedName("phoneNumber2")
    val phoneNumber2: String = "",
    @SerializedName("phoneNumber3")
    val phoneNumber3: String = "",
    @SerializedName("hotline")
    val hotline: String = "",
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @Expose(serialize = false, deserialize = false)
    var marker: Marker? = null
)