package com.example.se7etak_tpa

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.se7etak_tpa.databinding.ActivityHomeBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    private val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when(destination.id) {
            R.id.navigation_request_approval -> firebaseAnalytics.logEvent("Request approval"){}
            R.id.navigation_request_reimbursement -> firebaseAnalytics.logEvent("Request Re-imbursement"){}
            R.id.navigation_check_network -> firebaseAnalytics.logEvent("Check Network"){}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = Firebase.analytics
        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home)

        navView.setupWithNavController(navController)


    }


    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        navController.removeOnDestinationChangedListener(listener)
        super.onPause()
    }

}