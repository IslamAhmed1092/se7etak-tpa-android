package com.example.se7etak_tpa.home_ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.databinding.FragmentRequestReimbursementBinding

class RequestReimbursementFragment : Fragment() {

    private lateinit var requestReimbursementViewModel: RequestReimbursementViewModel
    private var _binding: FragmentRequestReimbursementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestReimbursementViewModel =
            ViewModelProvider(this).get(RequestReimbursementViewModel::class.java)

        _binding = FragmentRequestReimbursementBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRequestReimbursement
        requestReimbursementViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}