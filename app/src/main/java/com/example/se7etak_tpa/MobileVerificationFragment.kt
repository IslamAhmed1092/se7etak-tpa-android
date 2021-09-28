package com.example.se7etak_tpa

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.se7etak_tpa.databinding.FragmentMobileVerificationBinding

class MobileVerificationFragment : Fragment() {

    private lateinit var binding: FragmentMobileVerificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val timer = object: CountDownTimer(300000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val minutes = millisUntilFinished / 1000 / 60
//                val seconds = millisUntilFinished / 1000 % 60
//                view.findViewById<TextView>(R.id.tv_code_expire_text).text = getString(R.string.code_expire_text, minutes, seconds)
//            }
//            override fun onFinish() {
//
//            }
//        }
//        timer.start()
    }

}