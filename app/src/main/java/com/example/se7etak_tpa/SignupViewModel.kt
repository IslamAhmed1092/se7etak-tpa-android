package com.example.se7etak_tpa

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignupViewModel: ViewModel() {

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _timerMinutes = MutableLiveData(0)
    val timerMinutes: LiveData<Int> get() = _timerMinutes

    private val _timerSeconds = MutableLiveData(0)
    val timerSeconds: LiveData<Int> get() = _timerSeconds

    private val _timerFinished = MutableLiveData(false)
    val timerFinished: LiveData<Boolean> get() = _timerFinished

    fun setPhone(inputPhone: String?) {
        _phone.value = inputPhone
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

    fun validatePassword(inputPassword: String?) =
        !inputPassword.isNullOrEmpty() && inputPassword.length >= 8

    fun validateConfirmPassword(inputPassword: String?, inputConfirmPassword: String?)
        = !inputPassword.isNullOrEmpty() && !inputPassword.isNullOrEmpty()
            && inputPassword == inputConfirmPassword

    fun validateID(inputID: String?) = !inputID.isNullOrEmpty()

    fun isCodeCorrect(code: String?): Boolean {
        return !code.isNullOrEmpty() && code == "1234"
    }
}