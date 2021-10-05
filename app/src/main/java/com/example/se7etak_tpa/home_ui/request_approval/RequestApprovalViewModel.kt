package com.example.se7etak_tpa.home_ui.request_approval

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.Utils.GmailSender
import com.example.se7etak_tpa.Utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import java.lang.Exception

class RequestApprovalViewModel : ViewModel() {


    private val _text = MutableLiveData<String>().apply {
        value = "This is request approval Fragment"
    }

    val text: LiveData<String> = _text

    val attachment1Uri = MutableLiveData<Uri>()
    val attachment2Uri = MutableLiveData<Uri>()

    val attachment1Name = MutableLiveData<String>()
    val attachment2Name = MutableLiveData<String>()

    val providerTypeSelectedPosition = MutableLiveData<Int>()

    val providerNameSelectedPosition = MutableLiveData<Int>()


    val comment = MutableLiveData<String>()



    fun sendEmail() {
        try {
            val email = GmailSender.init(
                "se7etak.tpa.eva.pharma@gmail.com",
                "password", "islamyousry16@gmail.com", "subject",
                "body"
            )
            email.createEmailMessage()
            email.sendEmail()
        } catch (e: Exception) {
            print(e.message)
        }
    }

}