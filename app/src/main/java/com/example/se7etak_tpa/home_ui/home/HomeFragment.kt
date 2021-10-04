package com.example.se7etak_tpa.home_ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.AuthActivity
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.SignupViewModel
import com.example.se7etak_tpa.databinding.FragmentHomeBinding
import com.example.se7etak_tpa.databinding.FragmentSignupBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivLogout.setOnClickListener {
            SignupViewModel.deleteUserData(requireContext())
            //TODO: send request to the server here
            val intent = Intent(context, AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

}