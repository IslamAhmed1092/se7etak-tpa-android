package com.example.se7etak_tpa.auth_ui.login

import android.util.Log
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


class LoginViewModel: ViewModel() {

    var user = User()

    private val _loginStatus = MutableLiveData(StatusObject.INITIAL)
    val loginStatus: LiveData<StatusObject> get() = _loginStatus

    var errorMessage = ""

    /**
     * for debugging only
     */
    private var _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code


    fun login(email: String, password: String) {

        val userParams = mapOf("email" to email, "password" to password)

        val callResponse = Api.retrofitService.login(userParams)
        _loginStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    response.body()?.let {
                        val userString = it.get("user")
                        userString?.let { json ->
                            user = Gson().fromJson(json, User::class.java)
                        }
                        user.token = it.get("token")?.asString
                    }

                    _loginStatus.value = StatusObject.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500) {
                    errorMessage = "There is a problem in the server, Please try again later."
                    _loginStatus.value = StatusObject.ERROR
                } else {
                    response.errorBody()?.let {
                        val errorJson = JSONObject(it.string())
                        errorMessage = errorJson.optString("message")
                        errorJson.optJSONObject("user")?.let { jsonObject ->
                            user = Gson().fromJson(jsonObject.toString(), User::class.java)
                        }
                        _code.value = errorJson.optString("code")
                    }
                    _loginStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: $errorMessage" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                errorMessage = "Please check your internet connection and try again"
                _loginStatus.value = StatusObject.ERROR
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })

    }
}