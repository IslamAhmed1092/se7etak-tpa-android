package com.example.se7etak_tpa.network

import android.net.Uri
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.data.Provider
import com.example.se7etak_tpa.data.ProviderNameWithId
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * here we add functions for api requests
 */

interface ApiService {

    @POST("api/Account/login")
    fun login(
        @Body user: Map<String, String>
    ): Call<JsonObject>

    @POST("api/Account/Register")
    fun register(
        @Body user: Map<String, String>
    ): Call<JsonObject>

    @POST("api/Account/VerifyCode")
    fun verifyCode(
        @Body body: Map<String, String>
    ): Call<JsonObject>

    @GET("api/Account/SendCode")
    fun sendCode(
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


    @GET("/api/Provider/GetProviderType")
    fun getProviderType(): Call<List<String>>


    @GET("/api/Provider/GetProviderName")
    fun getProviderName(@Query("providerType") providerType: String): Call<List<ProviderNameWithId>>


    @POST("/api/Account/ChangePhoneNumber")
    fun changePhoneNumber(
        @Body body: Map<String, String>
    ): Call<JsonObject>


    @GET("/api/Provider/GetRequestDetails")
    fun getRequestDetails(@Query("id") requestId: Int): Call<JsonObject>

}

