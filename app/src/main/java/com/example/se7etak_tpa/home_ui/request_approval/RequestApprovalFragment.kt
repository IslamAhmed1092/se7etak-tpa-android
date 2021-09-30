package com.example.se7etak_tpa.home_ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.databinding.FragmentRequestApprovalBinding

class RequestApprovalFragment : Fragment() {

    private lateinit var requestApprovalViewModel: RequestApprovalViewModel
    private var _binding: FragmentRequestApprovalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestApprovalViewModel =
            ViewModelProvider(this).get(RequestApprovalViewModel::class.java)

        _binding = FragmentRequestApprovalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRequestApproval
        requestApprovalViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}