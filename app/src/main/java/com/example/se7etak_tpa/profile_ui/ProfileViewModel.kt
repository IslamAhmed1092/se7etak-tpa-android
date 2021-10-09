package com.example.se7etak_tpa.profile_ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.network.Api
import com.example.se7etak_tpa.utils.loadUserData
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class ProfileStatus { INITIAL, LOADING, NO_CONNECTION, UNAUTHORIZED, DONE, ERROR}

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    var oldUser = User()

    private val _user = MutableLiveData(User())
    val user: LiveData<User> get() = _user

    private val _status = MutableLiveData(ProfileStatus.INITIAL)
    val status: LiveData<ProfileStatus> get() = _status

    private val _updateDataStatus = MutableLiveData(ProfileStatus.INITIAL)
    val updateDataStatus: LiveData<ProfileStatus> get() = _updateDataStatus

    val isNameUpdated = MutableLiveData(false)
    val isEmailUpdated =  MutableLiveData(false)
    val isIdUpdated = MutableLiveData(false)

    var errorMessage = ""

    private var userToken:String? = ""

    private fun getToken(): String? {
        return loadUserData(getApplication<Application>().applicationContext).token
    }

    fun setUserEmail(email: String) {
        _user.value = _user.value?.copy(email = email)
    }

    fun setUserName(name: String) {
        _user.value = _user.value?.copy(name = name)
    }

    fun setUserID(id: String) {
        _user.value = _user.value?.copy(id = id)
    }

    init {
        userToken = getToken()
        if(!userToken.isNullOrEmpty())
            getUserData()
//        _user.value = User("566893", "", "Islam", "eslam1092@hotmail.com", "01118300285")
//        oldUser = user.value!!
//        _status.value = ProfileStatus.DONE
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
                    oldUser = response.body()!!
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

    fun updateUser() {
        val body: MutableMap<String, String> = mutableMapOf()
        if(isNameUpdated.value!!)
            body["name"] = user.value!!.name!!

        if(isEmailUpdated.value!!)
            body["email"] = user.value!!.email!!

        if(isIdUpdated.value!!)
            body["Se7etakID"] = user.value!!.id!!

        val callResponse = Api.retrofitService.updateUser("Bearer $userToken", body)

        _updateDataStatus.value = ProfileStatus.LOADING
        callResponse.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.code() == 200) {
                    response.body()?.let {
                        _user.value?.token = it.get("token")?.asString
                        userToken = user.value?.token
                    }
                    oldUser = user.value!!
                    isEmailUpdated.value = false
                    isNameUpdated.value = false
                    isIdUpdated.value = false

                    _updateDataStatus.value = ProfileStatus.DONE
                    Log.i("TAG", "onResponse ${response.code()}: ${response.message()}" )
                } else if(response.code() == 500) {
                    errorMessage = "There is a problem in the server, Please try again later."
                    _updateDataStatus.value = ProfileStatus.ERROR
                } else {
                    try {
                        errorMessage = JSONObject(response.errorBody()?.string()!!).optString("message")
                        _user.value = oldUser
                        isEmailUpdated.value = false
                        isNameUpdated.value = false
                        isIdUpdated.value = false
                        _updateDataStatus.value = ProfileStatus.ERROR
                    } catch (e: Exception) {
                        errorMessage = response.message()
                        if (errorMessage == "Unauthorized")
                            _status.value = ProfileStatus.UNAUTHORIZED
                    }
                    Log.i("TAG", "onResponse ${response.code()}: ${errorMessage}" )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                errorMessage = "Please check your internet connection and try again"
                _updateDataStatus.value = ProfileStatus.NO_CONNECTION
                Log.i("TAG", "onFailure: ${t.message}")
            }
        })

    }

}