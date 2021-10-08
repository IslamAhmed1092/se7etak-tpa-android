package com.example.se7etak_tpa.profile_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import androidx.databinding.DataBindingUtil
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.databinding.BottomSheetEditBinding


class BottomSheetEdit(val type:String, val currentValue: String, val onSaveClick: (String) -> Unit): BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditBinding

    companion object {
        const val NAME = "Name"
        const val EMAIL = "Email Address"
        const val ID = "Se7etak ID"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            onSaveClick(binding.etEdit.text.toString())
        }
    }
}