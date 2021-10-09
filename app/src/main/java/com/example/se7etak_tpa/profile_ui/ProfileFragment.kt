package com.example.se7etak_tpa.profile_ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.AuthActivity
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.auth_ui.mobile_verification.MobileVerificationFragmentDirections
import com.example.se7etak_tpa.databinding.FragmentProfileBinding
import com.example.se7etak_tpa.utils.deleteUserData
import com.example.se7etak_tpa.utils.saveUserData
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning!")
            .setMessage("Do you want to return without saving updates?")
            .setPositiveButton("RETURN") { _, _ ->
                activity?.finish()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(binding.btnSave.isEnabled) {
                    dialogBuilder.show()
                } else {
                    activity?.finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.backIcon.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.editEmail.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.EMAIL, viewModel.user.value!!.email!!) {
                viewModel.setUserEmail(it)
                viewModel.isEmailUpdated.value = (viewModel.oldUser.email != viewModel.user.value!!.email)
            }
            activity?.let {
                bottomSheet.show(it.supportFragmentManager, "BottomSheetEditEmail")
            }
        }

        binding.editId.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.ID, viewModel.user.value!!.id!!) {
                viewModel.setUserID(it)
                viewModel.isIdUpdated.value = (viewModel.oldUser.id != viewModel.user.value!!.id)
            }
            activity?.let {
                bottomSheet.show(it.supportFragmentManager, "BottomSheetEditID")
            }
        }

        binding.editName.setOnClickListener {
            val bottomSheet = BottomSheetEdit(BottomSheetEdit.NAME, viewModel.user.value!!.name!!) {
                viewModel.setUserName(it)
                viewModel.isNameUpdated.value = (viewModel.oldUser.name != viewModel.user.value!!.name)
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
                        deleteUserData(requireContext())
                        val intent = Intent(activity, AuthActivity::class.java)
                        startActivity(intent)
                        activity?.finishAffinity()
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


        viewModel.updateDataStatus.observe(viewLifecycleOwner) {
            if (it == ProfileStatus.DONE) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                saveUserData(requireContext(), viewModel.user.value!!)
            } else if(it == ProfileStatus.UNAUTHORIZED) {
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
                        activity?.finishAffinity()
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

        binding.btnSave.setOnClickListener {
            viewModel.updateUser()
        }

        binding.btnTryAgain.setOnClickListener {
            if(viewModel.status.value == ProfileStatus.NO_CONNECTION)
                viewModel.getUserData()
            else
                viewModel.updateUser()
        }

    }
}