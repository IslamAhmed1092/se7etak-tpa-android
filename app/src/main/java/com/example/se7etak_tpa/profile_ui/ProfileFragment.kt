package com.example.se7etak_tpa.profile_ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.AuthActivity
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.Utils.Utils
import com.example.se7etak_tpa.Utils.Utils.saveUserData
import com.example.se7etak_tpa.auth_ui.mobile_verification.MobileVerificationFragmentDirections
import com.example.se7etak_tpa.auth_ui.mobile_verification.StatusObject
import com.example.se7etak_tpa.databinding.FragmentHomeBinding
import com.example.se7etak_tpa.databinding.FragmentProfileBinding
import com.example.se7etak_tpa.home_ui.home.HomeViewModel
import com.example.se7etak_tpa.home_ui.home.RequestsApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.ktx.logEvent

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.editEmail.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.EMAIL, viewModel.user.value!!.email!!) {
                viewModel.setUpdated(true)
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            activity?.let {
                bottomSheet.show(it.supportFragmentManager, "BottomSheetEditEmail")
            }
        }

        binding.editId.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.ID, viewModel.user.value!!.id!!) {
                viewModel.setUpdated(true)
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            activity?.let {
                bottomSheet.show(it.supportFragmentManager, "BottomSheetEditID")
            }
        }

        binding.editName.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.NAME, viewModel.user.value!!.name!!) {
                viewModel.setUpdated(true)
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            activity?.let {
                bottomSheet.show(it.supportFragmentManager, "BottomSheetEditName")
            }
        }

        viewModel.status.observe(viewLifecycleOwner) {
            if (it == ProfileStatus.DONE) {
                saveUserData(requireContext(), viewModel.user.value!!)
            } else if(it == ProfileStatus.UNAUTHORIZED) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Session Expired")
                    .setMessage("Your session has expired. Please log in again.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        Utils.deleteUserData(requireContext())
                        val intent = Intent(activity, AuthActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent)
                    }
                    .show()
            } else if (it == ProfileStatus.ERROR) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("ERROR!")
                    .setMessage(viewModel.errorMessage)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        binding.btnTryAgain.setOnClickListener {
            viewModel.getUserData()
        }

    }
}