package com.example.se7etak_tpa.auth_ui.signup

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.utils.Utils.validateConfirmPassword
import com.example.se7etak_tpa.utils.Utils.validateEmail
import com.example.se7etak_tpa.utils.Utils.validateID
import com.example.se7etak_tpa.utils.Utils.validateName
import com.example.se7etak_tpa.utils.Utils.validatePassword
import com.example.se7etak_tpa.utils.Utils.validatePhone
import com.example.se7etak_tpa.databinding.FragmentSignupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val viewModel: SignupViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        firebaseAnalytics = Firebase.analytics

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),
            R.color.status_bar_color
        )
        WindowInsetsControllerCompat(activity?.window!!, activity?.window?.decorView!!).isAppearanceLightStatusBars = true

        binding.btnLogin.setOnClickListener {
            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.btnSignup.setOnClickListener {
            checkAllFields()

            if (!isAnyErrorExist()) {
                viewModel.signup(
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

        viewModel.signupStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                firebaseAnalytics.logEvent("Signup") {}

                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                val action =
                SignupFragmentDirections.actionSignupFragmentToMobileVerificationFragment(
                    viewModel.user,
                    viewModel.code.value!!
                )
                findNavController().navigate(action)

            } else if (it == StatusObject.ERROR) {
                errorAlertDialogBuilder.setMessage(viewModel.errorMessage).show()
                if (viewModel.errorMessage.contains("email", true)) {
                    binding.ilEmail.isErrorEnabled = true
                    binding.ilEmail.error = viewModel.errorMessage
                } else if (viewModel.errorMessage.contains("PhoneNumber", true)) {
                    binding.ilMobile.isErrorEnabled = true
                    binding.ilMobile.error = viewModel.errorMessage
                } else if (viewModel.errorMessage.contains("id", true)) {
                    binding.ilId.isErrorEnabled = true
                    binding.ilId.error = viewModel.errorMessage
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
        if (!validateID(binding.etId.text?.toString())) {
            binding.ilId.isErrorEnabled = true
            binding.ilId.error = "membership id is required"
        } else {
            binding.ilId.isErrorEnabled = false
        }
    }

    private fun checkMobileNumber() {
        if (!validatePhone(binding.etMobile.text?.toString())) {
            binding.ilMobile.isErrorEnabled = true
            binding.ilMobile.error = "invalid phone number"
        } else {
            binding.ilMobile.isErrorEnabled = false
        }
    }

    private fun checkConfirmPassword() {
        if (!validateConfirmPassword(
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
        if (!validatePassword(binding.etPassword.text?.toString())) {
            binding.ilPassword.isErrorEnabled = true
            binding.ilPassword.error = getString(R.string.password_rules)
        } else {
            binding.ilPassword.isErrorEnabled = false
        }
    }

    private fun checkEmail() {
        if (!validateEmail(binding.etEmail.text?.toString())) {
            binding.ilEmail.isErrorEnabled = true
            binding.ilEmail.error = "invalid email address"
        } else {
            binding.ilEmail.isErrorEnabled = false
        }
    }

    private fun checkName() {
        if (!validateName(binding.etName.text?.toString())) {
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