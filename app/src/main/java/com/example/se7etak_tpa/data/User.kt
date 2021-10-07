package com.example.se7etak_tpa.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("se7etakID")
    var id: String? = "",
    @Expose(serialize = false, deserialize = false)
    var token: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("phoneNumber")
    var phoneNumber: String? = "" 
): Serializable
