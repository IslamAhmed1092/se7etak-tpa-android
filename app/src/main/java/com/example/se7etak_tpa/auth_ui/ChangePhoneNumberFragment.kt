package com.example.se7etak_tpa.auth_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.databinding.FragmentChangePhoneNumberBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ChangePhoneNumberFragment : Fragment() {

    private lateinit var user: User
    private val signupViewModel: SignupViewModel by activityViewModels()
    private lateinit var binding: FragmentChangePhoneNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.get("user") as User
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_change_phone_number, container, false)
        signupViewModel.user = user
        signupViewModel.resetMobileData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilPhone.isErrorEnabled = false
            } else {
                checkMobileNumber()
            }
        }

        binding.etPhone.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE) {
                binding.btnContinue.performClick()
            }
            false
        }

        binding.btnContinue.setOnClickListener {
            checkMobileNumber()

            if (!binding.ilPhone.isErrorEnabled) {
                signupViewModel.changePhoneNumber(
                    binding.etPhone.text.toString()
                )
            }
        }

        val errorAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("ERROR!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        signupViewModel.changePhoneStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                Toast.makeText(context, "Phone number changed successfully!", Toast.LENGTH_SHORT).show()
                val action = ChangePhoneNumberFragmentDirections.actionChangePhoneNumberFragmentToMobileVerificationFragment()
                findNavController().navigate(action)
            } else if (it == StatusObject.ERROR) {
                errorAlertDialogBuilder.setMessage(signupViewModel.errorMessage).show()
            }
        })
    }

    private fun checkMobileNumber() {
        if (!signupViewModel.validatePhone(binding.etPhone.text?.toString())) {
            binding.ilPhone.isErrorEnabled = true
            binding.ilPhone.error = "invalid phone number"
        } else {
            binding.ilPhone.isErrorEnabled = false
        }
    }
}