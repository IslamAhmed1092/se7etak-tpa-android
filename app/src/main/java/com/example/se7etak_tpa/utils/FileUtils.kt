package com.example.se7etak_tpa.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.*

fun copyDirectoryOneLocationToAnotherLocation(
    sourceLocation: Uri,
    path: String,
    activity: Activity
) {

    if (!activity.cacheDir.exists()) {
        activity.cacheDir.mkdir()
    }
    val input: InputStream = activity.contentResolver.openInputStream(sourceLocation)!!
    val out: OutputStream = FileOutputStream(path)

    // Copy the bits from instream to outstream
    val buf = ByteArray(1024)
    var len: Int
    while (input.read(buf).also { len = it } > 0) {
        out.write(buf, 0, len)
    }
    input.close()
    out.flush()
    out.close()
}


fun getFileName(context: Context, uri: Uri?): String {
    uri?.let {
        context.contentResolver.query(uri, null, null, null, null)
            ?.let { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                return@getFileName cursor.getString(nameIndex)
            } ?: return@getFileName ""
    }
    return ""
}

fun deleteFile(path: String) {
    val file = File(path)
    if (file.exists()) file.absoluteFile.delete()
}

fun getPath(context: Context, fileName: String): String {
    return "${context.cacheDir.absolutePath}/'$fileName'"
}

// delete all file in cache directory.
fun deleteAllImages(context: Context) {
    val imageExtensions = arrayListOf("jpg", "png", "gif", "jpeg")
    for (child in context.cacheDir.listFiles()){
        for(extension in imageExtensions){
            if(child.name.lowercase().endsWith(extension)){
                child.delete()
                break
            }
        }
    }
}