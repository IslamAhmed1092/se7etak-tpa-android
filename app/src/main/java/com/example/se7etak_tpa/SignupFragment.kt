package com.example.se7etak_tpa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.databinding.FragmentSignupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        firebaseAnalytics = Firebase.analytics
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_bar_color)
        WindowInsetsControllerCompat(activity?.window!!, activity?.window?.decorView!!).isAppearanceLightStatusBars = true

        signupViewModel.resetSignupData()

        binding.btnLogin.setOnClickListener {
            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.btnSignup.setOnClickListener {
            checkAllFields()

            if (!isAnyErrorExist()) {
                signupViewModel.signup(
                    binding.etName.text.toString(),
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etMobile.text.toString(),
                    binding.etId.text.toString()
                )
            }
        }

        val errorAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("ERROR!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        signupViewModel.signupStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {}

                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                val action =
                SignupFragmentDirections.actionSignupFragmentToMobileVerificationFragment()
                findNavController().navigate(action)

            } else if (it == StatusObject.ERROR) {
                errorAlertDialogBuilder.setMessage(signupViewModel.errorMessage).show()
                if (signupViewModel.errorMessage.contains("email", true)) {
                    binding.ilEmail.isErrorEnabled = true
                    binding.ilEmail.error = signupViewModel.errorMessage
                } else if (signupViewModel.errorMessage.contains("PhoneNumber", true)) {
                    binding.ilMobile.isErrorEnabled = true
                    binding.ilMobile.error = signupViewModel.errorMessage
                } else if (signupViewModel.errorMessage.contains("id", true)) {
                    binding.ilId.isErrorEnabled = true
                    binding.ilId.error = signupViewModel.errorMessage
                }
            }
        })

        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilName.isErrorEnabled = false
            } else {
                checkName()
            }
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilEmail.isErrorEnabled = false
            } else {
                checkEmail()
            }
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilPassword.isErrorEnabled = false
            } else {
                checkPassword()
                if(!binding.etConfirmPassword.text?.toString().isNullOrEmpty()) checkConfirmPassword()
            }
        }

        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilConfirmPassword.isErrorEnabled = false
            } else {
                if(!binding.etPassword.text?.toString().isNullOrEmpty()
                    && !binding.etConfirmPassword.text?.toString().isNullOrEmpty()) checkConfirmPassword()
            }
        }

        binding.etMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilMobile.isErrorEnabled = false
            } else {
                checkMobileNumber()
            }
        }

        binding.etId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.ilId.isErrorEnabled = false
            } else {
                checkID()
            }
        }

        binding.etId.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE) {
                binding.btnSignup.performClick()
            }
            false
        }

    }

    private fun checkAllFields() {

        checkName()
        checkEmail()
        checkPassword()
        checkConfirmPassword()
        checkMobileNumber()
        checkID()

    }

    private fun checkID() {
        if (!signupViewModel.validateID(binding.etId.text?.toString())) {
            binding.ilId.isErrorEnabled = true
            binding.ilId.error = "membership id is required"
        } else {
            binding.ilId.isErrorEnabled = false
        }
    }

    private fun checkMobileNumber() {
        if (!signupViewModel.validatePhone(binding.etMobile.text?.toString())) {
            binding.ilMobile.isErrorEnabled = true
            binding.ilMobile.error = "invalid phone number"
        } else {
            binding.ilMobile.isErrorEnabled = false
        }
    }

    private fun checkConfirmPassword() {
        if (!signupViewModel.validateConfirmPassword(
                binding.etPassword.text?.toString(), binding.etConfirmPassword.text?.toString()
            )
        ) {
            binding.ilConfirmPassword.isErrorEnabled = true
            binding.ilConfirmPassword.error = "passwords doesn't match"
        } else {
            binding.ilConfirmPassword.isErrorEnabled = false
        }
    }

    private fun checkPassword() {
        if (!signupViewModel.validatePassword(binding.etPassword.text?.toString())) {
            binding.ilPassword.isErrorEnabled = true
            binding.ilPassword.error = getString(R.string.password_rules)
        } else {
            binding.ilPassword.isErrorEnabled = false
        }
    }

    private fun checkEmail() {
        if (!signupViewModel.validateEmail(binding.etEmail.text?.toString())) {
            binding.ilEmail.isErrorEnabled = true
            binding.ilEmail.error = "invalid email address"
        } else {
            binding.ilEmail.isErrorEnabled = false
        }
    }

    private fun checkName() {
        if (!signupViewModel.validateName(binding.etName.text?.toString())) {
            binding.ilName.isErrorEnabled = true
            binding.ilName.error = "Name is required"
        } else {
            binding.ilName.isErrorEnabled = false
        }
    }

    private fun isAnyErrorExist() = binding.run {
        ilName.isErrorEnabled || ilEmail.isErrorEnabled || ilPassword.isErrorEnabled ||
                ilConfirmPassword.isErrorEnabled || ilMobile.isErrorEnabled || ilId.isErrorEnabled
    }

}