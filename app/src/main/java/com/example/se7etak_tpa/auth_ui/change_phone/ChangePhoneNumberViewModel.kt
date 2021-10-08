package com.example.se7etak_tpa.auth_ui.change_phone

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.network.Api
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class StatusObject {INITIAL, LOADING, ERROR, DONE }

class ChangePhoneNumberViewModel: ViewModel() {
    var user = User()

    private val _changePhoneStatus = MutableLiveData(StatusObject.INITIAL)
    val changePhoneStatus: LiveData<StatusObject> get() = _changePhoneStatus

    /**
     * for debugging only
     */
    private var _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code

    var errorMessage = ""

    fun changePhoneNumber(phoneNumber: String) {
        val body = mapOf("se7etakID" to user.id!!, "email" to user.email!!,
            "phoneNumber" to phoneNumber)

        val callResponse = Api.retrofitService.changePhoneNumber(body)
        _changePhoneStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    user.phoneNumber = phoneNumber
                    _code.value = response.body()?.get("code")?.asString
                    _changePhoneStatus.value = StatusObject.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500) {
                    errorMessage = "There is a problem in the server, Please try again later."
                    _changePhoneStatus.value = StatusObject.ERROR
                } else {
                    response.errorBody()?.let {
                        errorMessage = JSONObject(it.string()).optString("message")
                    }
                    _changePhoneStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                errorMessage = "Please check your internet connection and try again"
                _changePhoneStatus.value = StatusObject.ERROR
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })
    }
}