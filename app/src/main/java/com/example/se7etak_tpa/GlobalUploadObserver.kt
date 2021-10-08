package com.example.se7etak_tpa

import android.content.Context
import android.util.Log
import android.widget.Toast
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate

class GlobalUploadObserver : RequestObserverDelegate {
    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
        Log.e("RECEIVER", "Progress: $uploadInfo")
    }

    override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
        Toast.makeText(context, "submission sent successfully", Toast.LENGTH_SHORT).show()
        Log.e("RECEIVER", "Success: $serverResponse")
    }

    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
        when (exception) {
            is UserCancelledUploadException -> {
                Log.e("RECEIVER", "Error, user cancelled upload: $uploadInfo")
            }

            is UploadError -> {
                Toast.makeText(context, exception.serverResponse.toString(), Toast.LENGTH_SHORT).show()
                Log.e("RECEIVER", "Error, upload error: ${exception.serverResponse}")
            }

            else -> {
                Toast.makeText(context, uploadInfo.toString(), Toast.LENGTH_SHORT).show()
                Log.e("RECEIVER", "Error: $uploadInfo", exception)

            }
        }
    }

    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
        hideLoadingDialog()
        Log.e("RECEIVER", "Completed: $uploadInfo")
    }

    override fun onCompletedWhileNotObserving() {
        Log.e("RECEIVER", "Completed while not observing")
    }
}
