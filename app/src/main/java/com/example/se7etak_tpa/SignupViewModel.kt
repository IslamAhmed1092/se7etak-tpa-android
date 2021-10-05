package com.example.se7etak_tpa

import android.content.Context
import android.content.SharedPreferences
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

const val TAG = "SignupViewModel"

class SignupViewModel: ViewModel() {

    private val _loginStatus = MutableLiveData(StatusObject.INITIAL)
    val loginStatus: LiveData<StatusObject> get() = _loginStatus

    private val _signupStatus = MutableLiveData(StatusObject.INITIAL)
    val signupStatus: LiveData<StatusObject> get() = _signupStatus

    private val _verificationStatus = MutableLiveData(StatusObject.INITIAL)
    val verificationStatus: LiveData<StatusObject> get() = _verificationStatus

    private val _sendCodeStatus = MutableLiveData(StatusObject.INITIAL)
    val sendCodeStatus: LiveData<StatusObject> get() = _sendCodeStatus


    private var _errorMessage: String = ""
    val errorMessage: String get() = _errorMessage

    var user = User()

    /**
     * for debugging only
     */
    private var _code = MutableLiveData<String>()
    val code: LiveData<String> get() = _code


    private val _timerMinutes = MutableLiveData(0)
    val timerMinutes: LiveData<Int> get() = _timerMinutes

    private val _timerSeconds = MutableLiveData(0)
    val timerSeconds: LiveData<Int> get() = _timerSeconds

    private val _timerFinished = MutableLiveData(false)
    val timerFinished: LiveData<Boolean> get() = _timerFinished

    private val _hideSendAgainTimerFinished = MutableLiveData(false)
    val hideSendAgainTimerFinished: LiveData<Boolean> get() = _hideSendAgainTimerFinished


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


    fun validateName(inputName: String?) = !inputName.isNullOrEmpty()

    fun validateEmail(inputEmail: String?) =
        !inputEmail.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()

    fun validatePhone(inputPhone: String?) =
        !inputPhone.isNullOrEmpty() && inputPhone[0] == '0' && inputPhone.length == 11

    fun validatePassword(inputPassword: String?): Boolean{
        inputPassword?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~\$^+=<>])(?=\\S+$).{8,}$"
            val passwordMatcher = Regex(passwordPattern)


            return passwordMatcher.find(inputPassword) != null
        } ?: return false
    }

    fun validateConfirmPassword(inputPassword: String?, inputConfirmPassword: String?)
        = !inputPassword.isNullOrEmpty() && !inputPassword.isNullOrEmpty()
            && inputPassword == inputConfirmPassword

    fun validateID(inputID: String?) = !inputID.isNullOrEmpty() && inputID.length == 6


    fun login(email: String, password: String) {

        val userParams = mapOf("email" to email, "password" to password)

        val callResponse = Api.retrofitService.login(userParams)
        _loginStatus.value = StatusObject.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    user.email = email
                    user.token = response.body()?.get("token")?.asString
                    _loginStatus.value = StatusObject.DONE
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    val errorJson = JSONObject(response.errorBody()?.string() ?:"{\"message\":\"\"}")
                    _errorMessage = errorJson.optString("message")
                    _code.value = errorJson.optString("code")
                    _loginStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${_errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _errorMessage = "Please check your internet connection and try again"
                _loginStatus.value = StatusObject.ERROR
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })

    }

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
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    _errorMessage = JSONObject(response.errorBody()?.string() ?:"{\"message\":\"\"}").optString("message")
                    _signupStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: ${_errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _errorMessage = "Please check your internet connection and try again"
                _signupStatus.value = StatusObject.ERROR
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })

    }

    fun verifyCode(code: String?){
        if (code.isNullOrEmpty()) {
            _errorMessage = "Code isn't correct, Please try again."
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
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    _errorMessage = "Code isn't correct, Please try again."
                    _verificationStatus.value = StatusObject.ERROR
                    Log.i("TAG", "onResponse ${response.code()}: $_errorMessage" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _errorMessage = "Please check your internet connection and try again"
                _verificationStatus.value = StatusObject.ERROR
                Log.i(TAG, "onFailure: ${t.message}")
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
                    Log.i(TAG, "onResponse ${response.code()}: ${response.message()}" )
                } else {
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _sendCodeStatus.value = StatusObject.ERROR
//                _errorMessage = "Please check your internet connection and try again"
                Log.i(TAG, "onFailure: ${t.message}")
            }

        })


    }

    fun resetSignupData() {
        _loginStatus.value = StatusObject.INITIAL
        _signupStatus.value = StatusObject.INITIAL
        _verificationStatus.value = StatusObject.INITIAL
        _sendCodeStatus.value = StatusObject.INITIAL
        _errorMessage = ""
        user = User()
        _timerMinutes.value = 0
        _timerSeconds.value = 0
        _timerFinished.value = false
        _hideSendAgainTimerFinished.value = false

    }

    companion object {
        fun saveUserData(context: Context, user: User) {
            val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            pref.edit()
                .putString("USER", Gson().toJson(user))
                .apply()
        }

        fun loadUserData(context: Context): User {
            val emptyJson = "{\"email\":\"\",\"id\":\"\",\"name\":\"\",\"phoneNumber\":\"\",\"token\":\"\"}"
            val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            return Gson().fromJson(pref.getString("USER", emptyJson), User::class.java)
        }

        fun deleteUserData(context: Context) {
            val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
            pref.edit().remove("USER").apply()
        }
    }
}