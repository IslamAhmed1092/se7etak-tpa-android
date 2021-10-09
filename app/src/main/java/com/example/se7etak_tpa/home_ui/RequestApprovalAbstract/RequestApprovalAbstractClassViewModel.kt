package com.example.se7etak_tpa.home_ui.RequestApprovalAbstract

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.se7etak_tpa.utils.GmailSender
import com.example.se7etak_tpa.utils.getPath
import com.example.se7etak_tpa.data.ProviderNameWithId
import com.example.se7etak_tpa.network.Api
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import javax.security.auth.callback.Callback


abstract class RequestApprovalAbstractClassViewModel
    : ViewModel() {
    protected var API_SUBMISSION_URL = ""
    val providerTypeList = MutableLiveData<List<String>>()
    val providerNameWithIdList = MutableLiveData<List<ProviderNameWithId>>()

    init {
        Api.retrofitService.getProviderType().enqueue(object : Callback,
            retrofit2.Callback<List<String>> {
            override fun onResponse(
                call: Call<List<String>>,
                response: Response<List<String>>
            ) {
                providerTypeList.value = response.body()
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("getProviderTypeCallApi", "onFailure:" + t.message)
            }
        })
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is request approval Fragment"
    }

    val text: LiveData<String> = _text


    val attachment1Name = MutableLiveData<String>()
    val attachment2Name = MutableLiveData<String>()
    val attachment1Exists = MutableLiveData<Boolean>().apply { value = false }
    val attachment2Exists = MutableLiveData<Boolean>().apply { value = false }

    val providerTypeSelectedPosition = MutableLiveData<Int>()
    val providerNameSelectedPosition = MutableLiveData<Int>()
    var comment: String = ""


    private fun getEmailBody(): String {
        var body = ""
        body += "provider type: ${providerTypeList.value?.get(providerTypeSelectedPosition.value!!)}\n"
        body += "provider name: ${providerNameWithIdList.value?.get(providerNameSelectedPosition.value!!)?.providerName}\n"
        body += "comment: $comment\n"
        return body
    }

    fun sendEmail(userId: String?, context: Context) {
        try {
            val email = GmailSender.init(
                "se7etak.tpa.eva.pharma@gmail.com",
                "googlestupid", "islamyousry16@gmail.com", userId,
                getEmailBody(),
                attachment1Name.value,
                attachment2Name.value,
                context.cacheDir.absolutePath
            )
            email.createEmailMessage()
            email.sendEmail()
        } catch (e: Exception) {
            print(e.message)
        }
    }

    fun getProviderName(providerType: String) {
        Api.retrofitService.getProviderName(providerType).enqueue(object : Callback,
            retrofit2.Callback<List<ProviderNameWithId>> {
            override fun onResponse(
                call: Call<List<ProviderNameWithId>>,
                response: Response<List<ProviderNameWithId>>
            ) {
                response.isSuccessful.let {
                    providerNameWithIdList.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<ProviderNameWithId>>, t: Throwable) {
                Log.e("ProviderNameCallApi", t.message.toString())
            }

        })
    }


    fun submitRequest(token: String?, context: Context) {
        val providerId =
            providerNameWithIdList.value?.get(providerNameSelectedPosition.value!!)?.id

        val task =
            MultipartUploadRequest(
                context,
                serverUrl = API_SUBMISSION_URL
            )
                .setMethod("POST")
                .addHeader("Authorization", "Bearer $token")
                .addParameter("Comment", comment)
                .addParameter("ProviderId", providerId.toString())


        if(attachment1Exists.value!!){
            task.addFileToUpload( getPath(context,attachment1Name.value!!),"files")
        }
        if(attachment2Exists.value!!){
            task.addFileToUpload( getPath(context,attachment2Name.value!!),"files")
        }
        task.startUpload()
    }
}