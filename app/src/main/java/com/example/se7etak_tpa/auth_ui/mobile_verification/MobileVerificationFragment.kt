package com.example.se7etak_tpa.auth_ui.mobile_verification

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.Utils.saveUserData
import com.example.se7etak_tpa.data.User
import com.example.se7etak_tpa.databinding.FragmentMobileVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class MobileVerificationFragment : Fragment() {

    private lateinit var user: User
    private lateinit var code: String
    private lateinit var binding: FragmentMobileVerificationBinding
    private val viewModel: MobileVerificationViewModel by viewModels()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var timer: CountDownTimer
    private lateinit var hideSendAgainTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.get("user") as User
            code = it.get("code") as String
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning!")
            .setMessage("Do you want to return before verifying your phone number?")
            .setPositiveButton("RETURN") { _, _ ->
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

        viewModel.user = user
        viewModel.setCode(code)

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_mobile_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val failedAlertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Failed")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        timer = object: CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                viewModel.setTimer(millisUntilFinished)
            }

            override fun onFinish() {
                viewModel.setTimerFinished(true)
            }
        }
        timer.start()


        hideSendAgainTimer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                viewModel.setHideSendCodeTimerFinished(true)
            }
        }
        hideSendAgainTimer.start()


        binding.btnSendAgain.setOnClickListener {
            viewModel.sendCode()

        }

        viewModel.sendCodeStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                timer.cancel()
                timer.start()
                viewModel.setTimerFinished(false)
                hideSendAgainTimer.cancel()
                hideSendAgainTimer.start()
                viewModel.setHideSendCodeTimerFinished(false)
            } else if (it == StatusObject.ERROR){
                failedAlertDialog.setMessage("Please check your internet connection and try again").show()
            }
        })
        binding.btnConfirm.setOnClickListener {
            viewModel.verifyCode(binding.etCode.text?.toString())
        }

        viewModel.verificationStatus.observe(viewLifecycleOwner, {
            if (it == StatusObject.DONE) {
                firebaseAnalytics.logEvent("Signup-OTP"){}
                Toast.makeText(context, "Code verified successfully!", Toast.LENGTH_SHORT).show()
                saveUserData(requireContext(), viewModel.user)
                val action =
                    MobileVerificationFragmentDirections.actionMobileVerificationFragmentToHomeActivity()
                findNavController().navigate(action)
                activity?.finish()
            } else if (it == StatusObject.ERROR){
                failedAlertDialog.setMessage(viewModel.errorMessage).show()
            }
        })


        binding.etCode.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE){
                binding.btnConfirm.performClick()
            }
            false
        }

        binding.btnChange.setOnClickListener {
            val action = MobileVerificationFragmentDirections.actionMobileVerificationFragmentToChangePhoneNumberFragment(viewModel.user)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resetData()
    }

    override fun onPause() {
        super.onPause()
        if(this::timer.isInitialized) {
            timer.cancel()
        }

        if(this::hideSendAgainTimer.isInitialized) {
            hideSendAgainTimer.cancel()
        }

    }

}