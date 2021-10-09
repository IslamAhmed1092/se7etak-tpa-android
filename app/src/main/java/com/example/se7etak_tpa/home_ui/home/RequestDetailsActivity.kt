package com.example.se7etak_tpa.home_ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.se7etak_tpa.data.ProviderNameWithId
import com.example.se7etak_tpa.databinding.ActivityRequestDetailsBinding
import com.example.se7etak_tpa.network.Api
import com.example.se7etak_tpa.utils.loadUserData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class RequestDetailsActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityRequestDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent?.extras?.getInt("id")
        Log.d("id", "$id")

        val userToken = loadUserData(this).token
        userToken?.let {
            Api.retrofitService.getRequestDetails(userToken, id!!).enqueue(object : Callback,
                retrofit2.Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    response.isSuccessful.let {
                        Log.d("yes", "$response")
                    }
                }


                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("RequestDetailsCallApi", t.message.toString())
                }
            }
            )
        }
    }
}