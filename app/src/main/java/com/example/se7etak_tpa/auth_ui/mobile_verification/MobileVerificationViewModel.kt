package com.example.se7etak_tpa.auth_ui.mobile_verification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.network.Api
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class StatusObject {INITIAL, LOADING, ERROR, DONE }

class MobileVerificationViewModel: ViewModel() {

    var user = User()

    private val _verificationStatus = MutableLiveData(StatusObject.INITIAL)
    val verificationStatus: LiveData<StatusObject> get() = _verificationStatus

    private val _sendCodeStatus = MutableLiveData(StatusObject.INITIAL)
    val sendCodeStatus: LiveData<StatusObject> get() = _sendCodeStatus

    var errorMessage = ""

    private val _timerMinutes = MutableLiveData(0)
    val timerMinutes: LiveData<Int> get() = _timerMinutes

    private val _timerSeconds = MutableLiveData(0)
    val timerSeconds: LiveData<Int> get() = _timerSeconds

    private val _timerFinished = MutableLiveData(false)
    val timerFinished: LiveData<Boolean> get() = _timerFinished

    private val _hideSendAgainTimerFinished = MutableLiveData(false)
    val hideSendAgainTimerFinished: LiveData<Boolean> get() = _hideSendAgainTimerFinished

    /**
     * for debugging only
     */
    private var _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code


    fun setCode(code: String) {
        _code.value = code
    }

    fun setTimer(millis: Long) {
        _timerMinutes.value = (millis / 1000 / 60).toInt()
        _timerSeconds.value = (millis / 1000 % 60).toInt()
    }

    fun setTimerFinished(isFinished: Boolean) {
        _timerFinished.value = isFinished
    }

    fun setHideSendCodeTimerFinished(isFinished: Boolean) {
        _hideSendAgainTimerFinished.value = isFinished
    }

    fun verifyCode(code: String?){
        if (code.isNullOrEmpty()) {
            errorMessage = "Code isn't correct, Please try again."
            _verificationStatus.value = StatusObject.ERROR
            return
        }

        val params = mapOf("email" to user.email!!, "code" to code)

        val callResponse = Api.retrofitService.verifyCode(params)
        _verificationStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    user.token = response.body()?.get("token")?.asString
                    _verificationStatus.value = StatusObject.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500) {
                    errorMessage = "There is a problem in the server, Please try again later."
                    _verificationStatus.value = StatusObject.ERROR
                } else {
                    errorMessage = "Code isn't correct, Please try again."
                    _verificationStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: $errorMessage" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                errorMessage = "Please check your internet connection and try again"
                _verificationStatus.value = StatusObject.ERROR
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })


    }

    fun sendCode(){
        val callResponse = Api.retrofitService.sendCode(user.email!!)

        _sendCodeStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    _code.value = response.body()?.get("code")?.asString
                    _sendCodeStatus.value = StatusObject.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    _sendCodeStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _sendCodeStatus.value = StatusObject.ERROR
//                _errorMessage = "Please check your internet connection and try again"
                Log.i("TAG", "onFailure: ${t.message}")
            }

        })


    }

    fun resetData() {
        _verificationStatus.value = StatusObject.INITIAL
        _sendCodeStatus.value = StatusObject.INITIAL
        errorMessage = ""
        _timerMinutes.value = 0
        _timerSeconds.value = 0
        _timerFinished.value = false
        _hideSendAgainTimerFinished.value = false

    }

}