package com.example.se7etak_tpa.home_ui.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.se7etak_tpa.data.RequestDetailsItem
import com.example.se7etak_tpa.databinding.ActivityRequestDetailsBinding
import com.example.se7etak_tpa.network.Api
import com.example.se7etak_tpa.utils.loadUserData
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.file.Paths
import javax.security.auth.callback.Callback

class RequestDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        binding.attachment1TextView.visibility = View.GONE
//        binding.attachment2TextView.visibility = View.GONE

        val id = intent?.extras?.getInt("id")
        Log.d("id", "$id")

        val userToken = loadUserData(this).token
        userToken?.let {
            Api.retrofitService.getRequestDetails("Bearer $userToken", id!!).enqueue(object : Callback,
                retrofit2.Callback<RequestDetailsItem> {
                override fun onResponse(
                    call: Call<RequestDetailsItem>,
                    response: Response<RequestDetailsItem>
                ) {
                    response.isSuccessful.let {
                        Log.d("RequestDetailsSuccess", "${response.body()}")
                        response.body()?.let {setData(it)}
                    }
                }

                override fun onFailure(call: Call<RequestDetailsItem>, t: Throwable) {
                    Log.e("RequestDetailsCallApi", t.message.toString())
                }
            }
            )
        }
    }

    fun setData(requestDetailsItem: RequestDetailsItem){
        binding.statusTextView.text = requestDetailsItem.state
        binding.dateTextView.text = requestDetailsItem.date
        binding.requestTypeTextView.text = requestDetailsItem.type
        binding.commentTextView.text = requestDetailsItem.comment
        binding.providerTypeTextView.text = requestDetailsItem.providerType
        binding.providerNameTextView.text = requestDetailsItem.providerName

//        val filesUrl = requestDetailsItem.listOfDocuments
//        for(fileUrl in filesUrl){
//            if(binding.attachment1TextView.visibility == View.GONE) setFileData(fileUrl,binding.attachment1TextView)
//            else setFileData(fileUrl,binding.attachment2TextView)
//        }
    }

//    private fun setFileData(fileUrl: String, view: TextView) {
//        view.visibility = View.VISIBLE
//        val encodedUrl = URLEncoder.encode(fileUrl, "UTF-8");
//        view.setOnClickListener(View.OnClickListener {
//            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$fileUrl"))
//            startActivity(browserIntent)
//        })
//    }
}