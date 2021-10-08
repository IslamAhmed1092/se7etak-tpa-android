package com.example.se7etak_tpa.home_ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.AuthActivity
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.SignupViewModel
import com.example.se7etak_tpa.Utils.loadUserData
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.databinding.FragmentHomeBinding
import com.example.se7etak_tpa.databinding.FragmentSignupBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var user: User
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        user = loadUserData(requireContext())
        if(!user.token.isNullOrEmpty()) homeViewModel.getPatientsRequests(user.token!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding.ivLogout.setOnClickListener {
            SignupViewModel.deleteUserData(requireContext())
            //TODO: send request to the server here
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.ivProfile.setOnClickListener {
            firebaseAnalytics.logEvent("Profile"){}
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

}