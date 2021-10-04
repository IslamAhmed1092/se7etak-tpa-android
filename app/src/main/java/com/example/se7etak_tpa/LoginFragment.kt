package com.example.se7etak_tpa

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.se7etak_tpa.databinding.FragmentLoginBinding
import com.example.se7etak_tpa.databinding.FragmentSignupBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val signupViewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        signupViewModel.resetSignupData()

        binding.btnSignup.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
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

        signupViewModel.loginStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                SignupViewModel.saveUserData(requireContext(), signupViewModel.user)
                val action =
                    LoginFragmentDirections.actionLoginFragmentToHomeActivity()
                findNavController().navigate(action)
                activity?.finish()
            } else if (it == StatusObject.ERROR) {
                errorAlertDialogBuilder.setMessage(signupViewModel.errorMessage).show()
            }
        })


        binding.buttonLogin.setOnClickListener {
            checkAllFields()

            if (!isAnyErrorExist()) {
                signupViewModel.login(
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

    private fun isAnyErrorExist() = binding.run {
        ilEmail.isErrorEnabled || ilPassword.isErrorEnabled
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.purple_dark)
        WindowInsetsControllerCompat(activity?.window!!, activity?.window?.decorView!!).isAppearanceLightStatusBars = false
    }
}