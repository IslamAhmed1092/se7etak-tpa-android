package com.example.se7etak_tpa.auth_ui.login

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
import com.example.se7etak_tpa.Utils.Utils.saveUserData
import com.example.se7etak_tpa.Utils.Utils.validateEmail
import com.example.se7etak_tpa.Utils.Utils.validatePassword
import com.example.se7etak_tpa.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        firebaseAnalytics = Firebase.analytics

        binding.buttonForgotPassword.setOnClickListener {
            firebaseAnalytics.logEvent("Forgot Password"){}
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }

        binding.btnSignup.setOnClickListener {
            firebaseAnalytics.logEvent("Create Account"){}
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }

        binding.btnSkip.setOnClickListener {
            firebaseAnalytics.logEvent("Skip"){}
            val action = LoginFragmentDirections.actionLoginFragmentToHomeActivity()
            findNavController().navigate(action)
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
            }
        }

        binding.etPassword.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE) {
                binding.buttonLogin.performClick()
            }
            false
        }

        val errorAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("ERROR!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        viewModel.loginStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                firebaseAnalytics.logEvent("Sign in"){}
                Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                saveUserData(requireContext(), viewModel.user)
                val action =
                    LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                findNavController().navigate(action)
                activity?.finish()
            } else if (it == StatusObject.ERROR) {
                if (!viewModel.code.value.isNullOrEmpty()) {
                    firebaseAnalytics.logEvent("Sign in"){}
                    val action = LoginFragmentDirections.actionLoginFragmentToMobileVerificationFragment(viewModel.user, viewModel.code.value!!)
                    findNavController().navigate(action)
                } else {
                    errorAlertDialogBuilder.setMessage(viewModel.errorMessage).show()
                }
            }
        })


        binding.buttonLogin.setOnClickListener {
            checkAllFields()

            if (!isAnyErrorExist()) {
                viewModel.login(
                    binding.etEmail.text.toString(),
                    binding.etPassword.text.toString(),
                )
            }
        }

    }

    private fun checkAllFields() {
        checkEmail()
        checkPassword()
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

    private fun isAnyErrorExist() = binding.run {
        ilEmail.isErrorEnabled || ilPassword.isErrorEnabled
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),
            R.color.purple_dark
        )
        WindowInsetsControllerCompat(activity?.window!!, activity?.window?.decorView!!).isAppearanceLightStatusBars = false
    }
}