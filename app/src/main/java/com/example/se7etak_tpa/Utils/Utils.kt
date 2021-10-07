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
import android.util.Patterns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import com.example.se7etak_tpa.data.User
import com.google.gson.Gson
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
                    // Pick pdf file.
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    fragment.startActivityForResult(intent, RC_PDF_PICKER)
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
                    // capture an Image.
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    fragment.startActivityForResult(
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

    fun getFileName(context: Context, uri: Uri?): String {
        uri?.let {
            context.contentResolver.query(uri, null, null, null, null)
                ?.let { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    return@getFileName cursor.getString(nameIndex)
                } ?:return@getFileName ""
        }
        return ""
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

    fun validateName(inputName: String?) = !inputName.isNullOrEmpty()

    fun validateEmail(inputEmail: String?) =
        !inputEmail.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()

    fun validatePhone(inputPhone: String?) =
        !inputPhone.isNullOrEmpty() && inputPhone[0] == '0' && inputPhone.length == 11

    fun validatePassword(inputPassword: String?): Boolean{
        inputPassword?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~\$^+=<>])(?=\\S+$).{8,}$"
            val passwordMatcher = Regex(passwordPattern)


            return passwordMatcher.find(inputPassword) != null
        } ?: return false
    }

    fun validateConfirmPassword(inputPassword: String?, inputConfirmPassword: String?)
            = !inputPassword.isNullOrEmpty() && !inputPassword.isNullOrEmpty()
            && inputPassword == inputConfirmPassword

    fun validateID(inputID: String?) = !inputID.isNullOrEmpty() && inputID.length == 6

    fun saveUserData(context: Context, user: User) {
        val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        pref.edit()
            .putString("USER", Gson().toJson(user))
            .apply()
    }

    fun loadUserData(context: Context): User {
        val emptyJson = "{\"email\":\"\",\"id\":\"\",\"name\":\"\",\"phoneNumber\":\"\",\"token\":\"\"}"
        val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        return Gson().fromJson(pref.getString("USER", emptyJson), User::class.java)
    }

    fun deleteUserData(context: Context) {
        val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        pref.edit().remove("USER").apply()
    }
}

