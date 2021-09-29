package com.example.se7etak_tpa.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * here we add functions for api requests
 */

interface ApiService {

    @POST("Register")
    fun register(
        @Body user: Map<String, String>
    ): Call<JsonObject>

}

