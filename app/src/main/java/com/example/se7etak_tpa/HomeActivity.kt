package com.example.se7etak_tpa

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.se7etak_tpa.databinding.ActivityHomeBinding
import com.example.se7etak_tpa.utils.GPSUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.example.se7etak_tpa.utils.DEFAULTS.GPS_CODE
import java.lang.Exception
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.se7etak_tpa.home_ui.check_network.CheckNetworkViewModel


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val viewModel: CheckNetworkViewModel by viewModels()


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

//        GPSUtils(this).turnOnGPS()
        buildLoadingDialog(this)
    }

    fun turnOnGPS() {
        GPSUtils(this).turnOnGPS()
    }

    //Integrate the onActivityResult function for the better output
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_CODE) {
                viewModel.isGPSOpened.value = true
            }
        }
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