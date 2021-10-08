package com.example.se7etak_tpa.network

import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.data.Provider
import com.example.se7etak_tpa.data.User
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

/**
 * here we add functions for api requests
 */

interface ApiService {

    @POST("api/Account/login")
    fun login (
        @Body user: Map<String, String>
    ): Call<JsonObject>

    @POST("api/Account/Register")
    fun register (
        @Body user: Map<String, String>
    ): Call<JsonObject>

    @POST("api/Account/VerifyCode")
    fun verifyCode (
        @Body body: Map<String, String>
    ): Call<JsonObject>

    @GET("api/Account/SendCode")
    fun sendCode (
        @Query("email") email: String
    ): Call<JsonObject>

    @GET("api/Provider/GetProviders")
    fun getProviders(
        @Query("TileNumber") tile: Long
    ): Call<List<Provider>>

    @GET("/api/Provider/GetPatientRequests")
    fun getPatientRequests(
        @Header("Authorization") authHeader: String     //should send "Bearer" + token
    ): Call<List<HomeRequest>>

    @POST("/api/Account/ChangePhoneNumber")
    fun changePhoneNumber(
        @Body body: Map<String, String>
    ): Call<JsonObject>

    @GET("/api/Account/GetUserInfo")
    fun getUser(
        @Header("Authorization") authHeader: String     //should send "Bearer" + token
    ): Call<User>
}

