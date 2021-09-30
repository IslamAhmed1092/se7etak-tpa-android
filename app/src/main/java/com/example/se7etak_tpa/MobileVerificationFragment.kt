package com.example.se7etak_tpa

import android.app.AlertDialog
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
import com.example.se7etak_tpa.databinding.FragmentMobileVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MobileVerificationFragment : Fragment() {

    private lateinit var binding: FragmentMobileVerificationBinding
    private val signupViewModel: SignupViewModel by activityViewModels()

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
        binding.viewModel = signupViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val timer = object: CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                signupViewModel.setTimer(millisUntilFinished)
            }

            override fun onFinish() {
                signupViewModel.setTimerFinished(true)
            }
        }
        timer.start()

        binding.btnSendAgain.setOnClickListener {
            // call the api to send code
            timer.cancel()
            timer.start()
            signupViewModel.setTimerFinished(false)
        }

        val failedAlertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Failed")
            .setMessage("Code isn't correct, please try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        binding.btnConfirm.setOnClickListener {
            if(signupViewModel.isCodeCorrect(binding.etCode.text.toString())) {
                Toast.makeText(context, "Code is correct", Toast.LENGTH_LONG).show()
            } else {
                    failedAlertDialog.show()
            }

        }

        binding.etCode.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE){
                binding.btnConfirm.performClick()
                true
            } else {
                false
            }
        }
    }

}