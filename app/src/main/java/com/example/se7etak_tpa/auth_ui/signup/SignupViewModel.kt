package com.example.se7etak_tpa.auth_ui.signup

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.network.Api
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class StatusObject {INITIAL, LOADING, ERROR, DONE }

class SignupViewModel: ViewModel() {

    var user = User()

    private val _signupStatus = MutableLiveData(StatusObject.INITIAL)
    val signupStatus: LiveData<StatusObject> get() = _signupStatus


    var errorMessage = ""


    /**
     * for debugging only
     */
    private var _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code


    fun signup(name: String, email: String, password: String, number: String, id: String) {

        val userParams = mapOf("se7etakID" to id, "name" to name, "email" to email,
            "phoneNumber" to number, "password" to password)

        val callResponse = Api.retrofitService.register(userParams)
        _signupStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    user.id = id
                    user.name = name
                    user.phoneNumber = number
                    user.email = email

                    _code.value = response.body()?.get("code")?.asString
                    _signupStatus.value = StatusObject.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500) {
                    errorMessage = "There is a problem in the server, Please try again later."
                    _signupStatus.value = StatusObject.ERROR
                } else {
                    response.errorBody()?.let {
                        errorMessage = JSONObject(it.string()).optString("message")
                    }
                    _signupStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                errorMessage = "Please check your internet connection and try again"
                _signupStatus.value = StatusObject.ERROR
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })

    }

}