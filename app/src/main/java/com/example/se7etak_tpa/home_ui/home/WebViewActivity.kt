package com.example.se7etak_tpa.home_ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.example.se7etak_tpa.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val url = intent.extras?.getString("url")
        url?.let{
            binding.webView.let {
                it.webViewClient = WebViewClient()
                it.settings.setSupportZoom(true)
                it.settings.javaScriptEnabled = true
                it.loadUrl(url)
            }
        }
    }
}