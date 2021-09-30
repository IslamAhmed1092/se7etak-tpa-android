package com.example.se7etak_tpa.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

/**
 * here we add functions for api requests
 */

interface ApiService {

    @POST("Register")
    fun register (
        @Body user: Map<String, String>
    ): Call<JsonObject>

    @POST("VerifyCode")
    fun verifyCode (
        @Body body: Map<String, String>
    ): Call<JsonObject>

    @GET("SendCode")
    fun sendCode (
        @Query("email") email: String
    ): Call<JsonObject>
}

