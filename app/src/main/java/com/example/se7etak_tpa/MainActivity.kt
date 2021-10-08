package com.example.se7etak_tpa

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        //Animation
        val topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        //Hooks
        val image = findViewById<ImageView>(R.id.imageView3)
        val slogan = findViewById<TextView>(R.id.textView7)


        image.animation = topAnim
        slogan.animation = bottomAnim


        Handler().postDelayed({
            val intent: Intent
            val user = SignupViewModel.loadUserData(this)
            if(user.token.isNullOrEmpty()) {
                intent = Intent(this@MainActivity, AuthActivity::class.java)
            } else {
                intent = Intent(this@MainActivity, HomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        },3000)

    }
}