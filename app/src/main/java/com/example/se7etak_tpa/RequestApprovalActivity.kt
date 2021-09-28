package com.example.se7etak_tpa

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.graphics.component1
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.se7etak_tpa.Utils.Utils
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

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
            } else if (requestCode == UCrop.REQUEST_CROP) {
                addAttachment(UCrop.getOutput(data!!)!!)
//                findViewById<CircleImageView>(R.id.sign_up_profile_image_view).setImageBitmap(ImageUtils.getBitmap(
//                    UCrop.getOutput(data!!), contentResolver))
            } else if (requestCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "upload failed, try again!", Toast.LENGTH_SHORT).show()
                Log.w(packageName, "OnActivityResult: UCrop.RESULT_ERROR", UCrop.getError(data!!))
            }
        } else super.onActivityResult(requestCode, resultCode, data)

    }

    private fun addAttachment(uri: Uri){
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
                if (findViewById<View>(R.id.request_approval_attachment1_view).isVisible && findViewById<View>(R.id.request_approval_attachment2_view).isVisible)
                    Toast.makeText(this, "You can't add more than two attachments!", Toast.LENGTH_SHORT).show()
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