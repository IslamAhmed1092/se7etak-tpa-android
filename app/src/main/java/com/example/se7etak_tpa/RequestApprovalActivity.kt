package com.example.se7etak_tpa

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.se7etak_tpa.Utils.Utils
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*
import android.widget.AdapterView

import android.widget.Toast
import androidx.core.view.isEmpty

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.example.se7etak_tpa.Utils.GmailSender
import com.google.android.material.textfield.TextInputEditText
import java.lang.Exception


class RequestApprovalActivity : AppCompatActivity(), View.OnClickListener {

    private var attachmentName: String? = null
    private var attachment1Uri: Uri? = null
    private var attachment2Uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_approval)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        findViewById<ImageButton>(R.id.request_approval_add_attachment_image_button).setOnClickListener(
            this
        )
        findViewById<Button>(R.id.request_approval_submit_button).setOnClickListener(this)

        val list = arrayListOf<String>("AAA", "ABC", "BDC", "dfsd")

        val providerTypeSpinner =
            findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_type_spinner)
        providerTypeSpinner.item = list

        providerTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(this@RequestApprovalActivity, list!![position], Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        val providerNameSpinner =
            findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_name_spinner)
        providerNameSpinner.item = list

        providerNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(this@RequestApprovalActivity, list!![position], Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // get the image selected for Attachment.
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.RC_PHOTO_PICKER || requestCode == Utils.REQUEST_IMAGE_CAPTURE) {
                var uri = data?.data
                if(requestCode == Utils.REQUEST_IMAGE_CAPTURE) uri = Utils.getImageUri(applicationContext, data?.extras?.get("data") as Bitmap)
                attachmentName = Utils.getFileName(this, uri)
                Utils.performCrop(uri, cacheDir, this)
            } else if (requestCode == Utils.RC_PDF_PICKER) { // pdf file
                data?.data?.also { uri ->
                    if(attachment1Uri == null) attachment2Uri = uri
                    else attachment1Uri = uri
                    attachmentName = Utils.getFileName(this, uri)
                    addAttachment(uri)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data);
                val resultUri = result.uri;
                if(attachment1Uri == null) attachment2Uri = resultUri
                else attachment1Uri = resultUri
                addAttachment(resultUri)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "upload failed, try again!", Toast.LENGTH_SHORT).show()
                Log.w(packageName, "OnActivityResult: UCrop.RESULT_ERROR")
            }
        } else super.onActivityResult(requestCode, resultCode, data)

    }

    private fun addAttachment(uri: Uri) {
        val attachment1View = findViewById<View>(R.id.request_approval_attachment1_view)
        val attachment2View = findViewById<View>(R.id.request_approval_attachment2_view)

        if (attachment1View.isGone) {
            showAttachmentView(attachment1View)

        } else if (attachment2View.isGone) {
            showAttachmentView(attachment2View)
        }
    }

    private fun showAttachmentView(view: View) {
        view.visibility = View.VISIBLE
        view.findViewById<TextView>(R.id.request_approval_attachment_name_text_view).text =
            attachmentName
        view.findViewById<ImageButton>(R.id.request_approval_remove_attachment_image_button)
            .setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.request_approval_add_attachment_image_button -> {
                if (attachment1Uri != null && attachment2Uri != null)
                    Toast.makeText(
                        this,
                        "You can't add more than two attachments!",
                        Toast.LENGTH_SHORT
                    ).show()
                else Utils.chooseAttachement(this)
            }
            R.id.request_approval_remove_attachment_image_button -> {
                (view.parent as View).visibility = View.GONE
                val removedFileName = ((view.parent as ViewGroup).getChildAt(0) as TextView).text
                if(removedFileName == Utils.getFileName(this,attachment1Uri)){
                    attachment1Uri = null
                }else attachment2Uri = null
            }
            R.id.request_approval_submit_button -> {
                if(validate()) {
                    val providerType = findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_type_spinner).selectedItem
                    val providerName = findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_name_spinner).selectedItem
                    val comment = findViewById<TextInputEditText>(R.id.request_approval_comment_edit_text).text

                    sendEmail()
                }
            }

        }
    }

    private fun sendEmail() {
        try {
            val email = GmailSender.init(
                "se7etak.tpa.eva.pharma@gmail.com",
                "password", "islamyousry16@gmail.com", "subj",
                "body"
            )
            email.createEmailMessage()
            email.sendEmail()

        } catch (e: Exception) {
            print(e.message)
        }

    }
    
    private fun validate():Boolean{
        val providerTypeSpinner = findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_type_spinner)
        val providerNameSpinner = findViewById<SmartMaterialSpinner<String>>(R.id.request_approval_provider_name_spinner)
        var flag = true
        if(attachment1Uri == null || attachment2Uri == null){
            Toast.makeText(this, "Attachments must not be empty!", Toast.LENGTH_SHORT).show()
            flag = false
        }
        if(providerTypeSpinner.selectedItem == null) {
            providerTypeSpinner.errorText = "You must select an item!"
            flag = false
        }
        if(providerNameSpinner.selectedItem == null){
            providerNameSpinner.errorText = "You must select an item!"
            flag = false
        }
        return flag
    }

}