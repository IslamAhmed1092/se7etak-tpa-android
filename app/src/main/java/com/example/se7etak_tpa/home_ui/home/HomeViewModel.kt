package com.example.se7etak_tpa.home_ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.SignupViewModel
import com.example.se7etak_tpa.StatusObject
import com.example.se7etak_tpa.TAG
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.network.Api
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class RequestsApiStatus { LOADING, ERROR, DONE }

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _requests = MutableLiveData<List<HomeRequest>>()

    val requests: LiveData<List<HomeRequest>> = _requests

    private val _status = MutableLiveData<RequestsApiStatus>()

    val status: LiveData<RequestsApiStatus> = _status

    private val _errorMessage = MutableLiveData<String>()

    val errorMessage: LiveData<String> = _errorMessage

    private fun getToken(): String? {
        return SignupViewModel.loadUserData(getApplication<Application>().applicationContext).token
    }

    private var token:String? = ""

    init {
        token = getToken()
        if(!token.isNullOrEmpty()) {
            getPatientsRequests(token!!)
        }
    }


    private fun getPatientsRequests(token: String) {
        val callResponse = Api.retrofitService.getPatientRequests("Bearer $token")

        _status.value = RequestsApiStatus.LOADING
        callResponse.enqueue(object : Callback<List<HomeRequest>> {
            override fun onResponse(
                call: Call<List<HomeRequest>>,
                response: Response<List<HomeRequest>>
            ) {
                if(response.code() == 200) {
                    _requests.value = response.body()
                    _status.value = RequestsApiStatus.DONE
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500){
                    _errorMessage.value = "There is a problem in the server."
                    _status.value = RequestsApiStatus.ERROR
                } else {
                    response.errorBody()?.let {
                        _errorMessage.value = JSONObject(it.string()).optString("message")
                    }

                    _status.value = RequestsApiStatus.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<List<HomeRequest>>, t: Throwable) {
                _errorMessage.value = "Please check your internet connection."
                _status.value = RequestsApiStatus.ERROR
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })

    }
}