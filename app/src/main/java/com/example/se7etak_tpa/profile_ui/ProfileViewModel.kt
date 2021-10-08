package com.example.se7etak_tpa.profile_ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.Utils.Utils
import com.example.se7etak_tpa.data.HomeRequest
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.home_ui.home.RequestsApiStatus
import com.example.se7etak_tpa.network.Api
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class ProfileStatus { INITIAL, LOADING, NO_CONNECTION, UNAUTHORIZED, DONE, ERROR}

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val _user = MutableLiveData(User())
    val user: LiveData<User> get() = _user

    private val _status = MutableLiveData(ProfileStatus.INITIAL)
    val status: LiveData<ProfileStatus> get() = _status

    private val _isUpdated = MutableLiveData(false)
    val isUpdated: LiveData<Boolean> get() = _isUpdated

    var errorMessage = ""

    private var userToken:String? = ""

    private fun getToken(): String? {
        return Utils.loadUserData(getApplication<Application>().applicationContext).token
    }

    fun setUpdated(isUpdated: Boolean) {
        _isUpdated.value = isUpdated
    }

    init {
        userToken = getToken()
        if(!userToken.isNullOrEmpty())
            getUserData()
    }

    fun getUserData() {
        val callResponse = Api.retrofitService.getUser("Bearer $userToken")

        _status.value = ProfileStatus.LOADING
        callResponse.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if(response.code() == 200) {
                    _user.value = response.body()
                    _user.value!!.token = userToken
                    _status.value = ProfileStatus.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500){
                    errorMessage = "There is a problem in the server."
                    _status.value = ProfileStatus.ERROR
                } else {
                    try {
                        errorMessage = JSONObject(response.errorBody()?.string()!!).optString("message")
                        _status.value = ProfileStatus.ERROR
                    } catch (e: Exception) {
                        errorMessage = response.message()
                        if (errorMessage == "Unauthorized")
                            _status.value = ProfileStatus.UNAUTHORIZED
                        else
                            _status.value = ProfileStatus.ERROR
                    }
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                errorMessage = "Please check your internet connection."
                _status.value = ProfileStatus.NO_CONNECTION
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })
    }

}