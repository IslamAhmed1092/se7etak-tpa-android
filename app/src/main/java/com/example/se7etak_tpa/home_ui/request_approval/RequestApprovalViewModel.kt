package com.example.se7etak_tpa.home_ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestApprovalViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is request approval Fragment"
    }
    val text: LiveData<String> = _text
}