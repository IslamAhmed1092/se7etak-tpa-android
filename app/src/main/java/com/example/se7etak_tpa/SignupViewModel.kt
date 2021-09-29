package com.example.se7etak_tpa

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se7etak_tpa.network.Api
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class SignupStatus { LOADING, ERROR, DONE }

const val TAG = "SignupViewModel"

class SignupViewModel: ViewModel() {

    private val _status = MutableLiveData<SignupStatus>()
    val status: LiveData<SignupStatus> get() = _status

    private var _errorMessage: String = ""
    val errorMessage: String get() = _errorMessage

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _timerMinutes = MutableLiveData(0)
    val timerMinutes: LiveData<Int> get() = _timerMinutes

    private val _timerSeconds = MutableLiveData(0)
    val timerSeconds: LiveData<Int> get() = _timerSeconds

    private val _timerFinished = MutableLiveData(false)
    val timerFinished: LiveData<Boolean> get() = _timerFinished

    fun setPhone(inputPhone: String?) {
        _phone.value = inputPhone!!
    }

    fun setTimer(millis: Long) {
        _timerMinutes.value = (millis / 1000 / 60).toInt()
        _timerSeconds.value = (millis / 1000 % 60).toInt()
    }

    fun setTimerFinished(isFinished: Boolean) {
        _timerFinished.value = isFinished
    }

    fun validateName(inputName: String?) = !inputName.isNullOrEmpty()

    fun validateEmail(inputEmail: String?) =
        !inputEmail.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()

    fun validatePhone(inputPhone: String?) =
        !inputPhone.isNullOrEmpty() && inputPhone[0] == '0' && inputPhone.length == 11

    fun validatePassword(inputPassword: String?): Boolean{
        inputPassword?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(inputPassword) != null
        } ?: return false
    }

    fun validateConfirmPassword(inputPassword: String?, inputConfirmPassword: String?)
        = !inputPassword.isNullOrEmpty() && !inputPassword.isNullOrEmpty()
            && inputPassword == inputConfirmPassword

    fun validateID(inputID: String?) = !inputID.isNullOrEmpty() && inputID.length == 6

    fun signup(name: String, email: String, password: String, number: String, id: String) {

        val user = mapOf<String, String>("se7etakID" to id, "name" to name, "email" to email,
            "phoneNumber" to number, "password" to password)

        val callResponse = Api.retrofitService.register(user)
        _status.value = SignupStatus.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    _phone.value = number
                    _email.value = email
                    _status.value = SignupStatus.DONE
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    _errorMessage = JSONObject(response.errorBody()?.string() ?:"{}").optString("message")
                    _status.value = SignupStatus.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${_errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _errorMessage = "Please check your internet connection and try again"
                _status.value = SignupStatus.ERROR
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })

    }

    fun isCodeCorrect(code: String?): Boolean {
        return !code.isNullOrEmpty() && code == "123456"
    }



}