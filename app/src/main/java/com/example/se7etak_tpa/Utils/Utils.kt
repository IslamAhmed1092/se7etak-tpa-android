package com.example.se7etak_tpa.Utils

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import java.io.OutputStream
import android.content.Intent

import androidx.core.app.ActivityCompat.startActivityForResult





object Utils {
    const val RC_PHOTO_PICKER = 1
    const val REQUEST_IMAGE_CAPTURE = 2
    const val RC_PDF_PICKER = 3
    const val WRITE_EXTERNAL_PERMISSION_CODE = 4



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

    fun getImageUri(context: Context, image: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            image,
            "Image",
            null
        )
        return Uri.parse(path)
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


    fun chooseAttachment(fragment: Fragment) {


        val options = arrayOf("Choose Pdf File", "Open Gallery", "Open Camera")
        val builder = AlertDialog.Builder(fragment.requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    pickPDF(fragment)
                    }
                1 -> {
                    // Pick Image from Gallery.
                    val intent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    (fragment).startActivityForResult(
                        Intent.createChooser(
                            intent,
                            "Complete Action using"
                        ), RC_PHOTO_PICKER
                    )
                }
                2 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (fragment.requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // capture an Image.
                            captureImage(fragment)
                        } else {
                            fragment.requestPermissions(
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                WRITE_EXTERNAL_PERMISSION_CODE
                            )
                        }
                    } else {
                        captureImage(fragment)
                    }
                }
            }
        }
        builder.create().show()
    }

    fun captureImage(fragment: Fragment){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(
            intent,
            REQUEST_IMAGE_CAPTURE
        )
    }

    fun pickPDF(fragment: Fragment){
        // Pick pdf file.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        fragment.startActivityForResult(intent, RC_PDF_PICKER)

    }

    fun Context.getFileExtension(uri: Uri): String? = when (uri.scheme) {
        // get file extension
        ContentResolver.SCHEME_FILE -> File(uri.path!!).extension
        // get actual name of file
        //ContentResolver.SCHEME_FILE -> File(uri.path!!).name
        ContentResolver.SCHEME_CONTENT -> getCursorContent(uri)
        else -> null
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

