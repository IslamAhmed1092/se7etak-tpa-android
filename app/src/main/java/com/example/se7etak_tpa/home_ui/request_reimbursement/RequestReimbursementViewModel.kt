package com.example.se7etak_tpa.home_ui.request_reimbursement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestReimbursementViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is request reimbursement Fragment"
    }
    val text: LiveData<String> = _text
}