package com.example.se7etak_tpa

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.databinding.FragmentMobileVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MobileVerificationFragment : Fragment() {

    private lateinit var binding: FragmentMobileVerificationBinding
    private val signupViewModel: SignupViewModel by activityViewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning!")
            .setMessage("Do you want to return before verifying your phone number?")
            .setPositiveButton("RETURN") { _, _ ->
                signupViewModel.resetSignupData()

                val action = MobileVerificationFragmentDirections.actionMobileVerificationFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    dialogBuilder.show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val failedAlertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Failed")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val timer = object: CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                signupViewModel.setTimer(millisUntilFinished)
            }

            override fun onFinish() {
                signupViewModel.setTimerFinished(true)
            }
        }
        timer.start()


        val hideSendAgainTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                signupViewModel.setHideSendCodeTimerFinished(true)
            }
        }
        hideSendAgainTimer.start()


        binding.btnSendAgain.setOnClickListener {
            signupViewModel.sendCode()

        }

        signupViewModel.sendCodeStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                timer.cancel()
                timer.start()
                signupViewModel.setTimerFinished(false)
                hideSendAgainTimer.cancel()
                hideSendAgainTimer.start()
                signupViewModel.setHideSendCodeTimerFinished(false)
            } else if (it == StatusObject.ERROR){
                failedAlertDialog.setMessage("Please check your internet connection and try again").show()
            }
        })
        binding.btnConfirm.setOnClickListener {
            signupViewModel.verifyCode(binding.etCode.text?.toString())
        }

        signupViewModel.verificationStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                firebaseAnalytics.logEvent("submit"){}
                Toast.makeText(context, "Code verified successfully!", Toast.LENGTH_SHORT).show()
                SignupViewModel.saveUserData(requireContext(), signupViewModel.user)
                val action =
                    MobileVerificationFragmentDirections.actionMobileVerificationFragmentToHomeActivity()
                findNavController().navigate(action)
                activity?.finish()
            } else if (it == StatusObject.ERROR){
                failedAlertDialog.setMessage(signupViewModel.errorMessage).show()
            }
        })


        binding.etCode.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE){
                binding.btnConfirm.performClick()
            }
            false
        }
    }



}