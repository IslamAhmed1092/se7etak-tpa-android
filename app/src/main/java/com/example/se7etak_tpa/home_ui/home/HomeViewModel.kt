package com.example.se7etak_tpa.home_ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.StatusObject
import com.example.se7etak_tpa.TAG
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.network.Api
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    fun getPatientsRequests(token: String) {
        val callResponse = Api.retrofitService.getPatientRequests("Bearer $token")

        callResponse.enqueue(object : Callback<List<HomeRequest>> {
            override fun onResponse(
                call: Call<List<HomeRequest>>,
                response: Response<List<HomeRequest>>
            ) {
                if(response.code() == 200) {
                    val list = response.body()
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<List<HomeRequest>>, t: Throwable) {
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })

    }
}