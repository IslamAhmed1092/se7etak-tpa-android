package com.example.se7etak_tpa.home_ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.se7etak_tpa.AuthActivity
import com.example.se7etak_tpa.ProfileActivity
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.utils.deleteUserData
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), RequestItemClickListener {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = homeViewModel
        binding.rvRequests.adapter = RequestsAdapter(this)

        binding.ivLogout.setOnClickListener {
            deleteUserData(requireContext())
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        binding.ivProfile.setOnClickListener {
            firebaseAnalytics.logEvent("Profile"){}
            val intent = Intent(context, ProfileActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.status.observe(viewLifecycleOwner) {
            if(it == RequestsApiStatus.UNAUTHORIZED) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Session Expired")
                    .setMessage("Your session has expired. Please log in again.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        deleteUserData(requireContext())
                        val intent = Intent(activity, AuthActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    .show()
            } else if (it == RequestsApiStatus.ERROR) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("ERROR!")
                    .setMessage(homeViewModel.errorMessage.value)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        binding.btnTryAgain.setOnClickListener {
            homeViewModel.getPatientsRequests()
        }


    }

    override fun onClickListener(id: Int) {
        val intent = Intent (activity, RequestDetailsActivity::class.java)
        intent.putExtra("id",id)
        requireActivity().startActivity(intent)
    }
}