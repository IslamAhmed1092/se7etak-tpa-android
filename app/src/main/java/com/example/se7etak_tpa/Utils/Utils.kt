package com.example.se7etak_tpa.Utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult
import com.theartofdev.edmodo.cropper.CropImage


object Utils {
    const val RC_PHOTO_PICKER = 1
    const val REQUEST_IMAGE_CAPTURE = 2
    const val RC_PDF_PICKER = 3


    // convert Uri to bitmap.
    fun getBitmap(profilePhotoUri: Uri?, contentResolver: ContentResolver?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT > 28) {
                val source = ImageDecoder.createSource(
                    contentResolver!!, profilePhotoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else MediaStore.Images.Media.getBitmap(contentResolver, profilePhotoUri)
        } catch (e: Exception) {
            print(e.cause?.message)
        }
        return bitmap
    }

    // show image in full screen mode.
    fun showPhoto(context: Context, uri: Uri) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, "image/*")
        try {
            context.startActivity(intent)
            Log.d(context.packageName, "showPhoto: success $uri")
        } catch (e: Exception) {
            Toast.makeText(context, "No application can handle this request", Toast.LENGTH_SHORT)
                .show()
            Log.e(context.packageName, "showPhoto: failure " + e.cause)
        }
    }

    // perform circular crop using UCrop library.
    fun performCrop(uri: Uri?, cacheDir: File?, activity: Activity?) {
        CropImage.activity(uri)
            .start(activity!!);
    }

    fun chooseAttachement(context: Context) {
        val options = arrayOf("Choose Pdf File", "Open Gallery", "Open Camera")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Pick pdf file.
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    (context as Activity).startActivityForResult(intent, RC_PDF_PICKER)
                }
                1 -> {
                    // Pick Image from Gallery.
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    (context as Activity).startActivityForResult(
                        Intent.createChooser(
                            intent,
                            "Complete Action using"
                        ), RC_PHOTO_PICKER
                    )
                }
                2 -> {
                    // capture an Image.
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    (context as Activity).startActivityForResult(
                        intent,
                        REQUEST_IMAGE_CAPTURE
                    )
                }
            }
        }
        builder.create().show()
    }

    fun Context.getFileExtension(uri: Uri): String? = when (uri.scheme) {
        // get file extension
        ContentResolver.SCHEME_FILE -> File(uri.path!!).extension
        // get actual name of file
        //ContentResolver.SCHEME_FILE -> File(uri.path!!).name
        ContentResolver.SCHEME_CONTENT -> getCursorContent(uri)
        else -> null
    }

    fun getFileName(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)
            ?.let{ cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                return@getFileName cursor.getString(nameIndex)
            }?:return ""
    }

    private fun Context.getCursorContent(uri: Uri): String? = try {
        contentResolver.query(uri, null, null, null, null)?.let { cursor ->
            cursor.run {
                val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
                if (moveToFirst()) mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
                // case for get actual name of file
                //if (moveToFirst()) getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
                else null
            }.also { cursor.close() }
        }
    } catch (e: Exception) {
        null
    }

}

