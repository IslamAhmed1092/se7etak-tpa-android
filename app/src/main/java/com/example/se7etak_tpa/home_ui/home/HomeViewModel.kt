package com.example.se7etak_tpa.home_ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.se7etak_tpa.Utils.Utils.loadUserData
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.network.Api
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class RequestsApiStatus { LOADING, NO_CONNECTION, UNAUTHORIZED, EMPTY, DONE, ERROR}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _requests = MutableLiveData<List<HomeRequest>>()

    val requests: LiveData<List<HomeRequest>> = _requests

    private val _status = MutableLiveData<RequestsApiStatus>()

    val status: LiveData<RequestsApiStatus> = _status

    private val _errorMessage = MutableLiveData<String>()

    val errorMessage: LiveData<String> = _errorMessage

    private fun getToken(): String? {
        return loadUserData(getApplication<Application>().applicationContext).token
    }

    private var userToken:String? = ""

    init {
        userToken = getToken()
        if(!userToken.isNullOrEmpty())
            getPatientsRequests()
    }


    fun getPatientsRequests() {

        val callResponse = Api.retrofitService.getPatientRequests("Bearer $userToken")

        _status.value = RequestsApiStatus.LOADING
        callResponse.enqueue(object : Callback<List<HomeRequest>> {
            override fun onResponse(
                call: Call<List<HomeRequest>>,
                response: Response<List<HomeRequest>>
            ) {
                if(response.code() == 200) {
                    _requests.value = response.body()
                    if(_requests.value.isNullOrEmpty())
                        _status.value = RequestsApiStatus.EMPTY
                    else
                        _status.value = RequestsApiStatus.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500){
                    _errorMessage.value = "There is a problem in the server."
                    _status.value = RequestsApiStatus.ERROR
                } else {
                    try {
                        _errorMessage.value = JSONObject(response.errorBody()?.string()!!).optString("message")
                        _status.value = RequestsApiStatus.ERROR
                    } catch (e: Exception) {
                        _errorMessage.value = response.message()
                        if (_errorMessage.value == "Unauthorized")
                            _status.value = RequestsApiStatus.UNAUTHORIZED
                    }
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<List<HomeRequest>>, t: Throwable) {
                _errorMessage.value = "Please check your internet connection."
                _status.value = RequestsApiStatus.NO_CONNECTION
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })

    }
}