package com.example.se7etak_tpa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.se7etak_tpa.Utils.Utils
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*
import android.widget.AdapterView

import android.widget.Toast


import com.chivorn.smartmaterialspinner.SmartMaterialSpinner


class RequestApprovalActivity : AppCompatActivity(), View.OnClickListener {

    private var attachmentName: String? = null

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
                attachmentName = Utils.getFileName(this, data?.data!!)
                Utils.performCrop(Objects.requireNonNull(data)!!.data, cacheDir, this)
            } else if (requestCode == Utils.RC_PDF_PICKER) { // pdf file
                data?.data?.also { uri ->
                    attachmentName = Utils.getFileName(this, uri)
                    addAttachment(uri)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data);
                val resultUri = result.uri;
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
                if (findViewById<View>(R.id.request_approval_attachment1_view).isVisible && findViewById<View>(
                        R.id.request_approval_attachment2_view
                    ).isVisible
                )
                    Toast.makeText(
                        this,
                        "You can't add more than two attachments!",
                        Toast.LENGTH_SHORT
                    ).show()
                else Utils.chooseAttachement(this)
            }
            R.id.request_approval_remove_attachment_image_button -> {
                (view.parent as View).visibility = View.GONE
            }
            R.id.request_approval_submit_button -> {

            }

        }
    }
}