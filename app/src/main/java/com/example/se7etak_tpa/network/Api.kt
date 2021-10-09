package com.example.se7etak_tpa.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://kerols3489-001-site1.btempurl.com/"

private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(100, TimeUnit.SECONDS)
    .readTimeout(100,TimeUnit.SECONDS)
    .writeTimeout(100,TimeUnit.SECONDS).build();

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
    .build()

/**
 * object to be used for api calls
 * example: Api.retrofitService.login(...)
 **/
object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}