package com.example.se7etak_tpa.profile_ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.utils.Utils.validateEmail
import com.example.se7etak_tpa.utils.Utils.validateID
import com.example.se7etak_tpa.utils.Utils.validateName
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

        binding.title.text = getString(R.string.text_edit_title, type)
        binding.etEdit.setText(currentValue)

        binding.etEdit.inputType = when(type){
            NAME -> InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            ID -> InputType.TYPE_CLASS_NUMBER
            else -> binding.etEdit.inputType
        }

        binding.etEdit.requestFocus()

        binding.etEdit.text?.length?.let { binding.etEdit.setSelection(it) }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.etEdit.setOnClickListener {
            binding.ilEdit.isErrorEnabled = false
            binding.ilEdit.error = null
        }


        binding.etEdit.setOnEditorActionListener { _, id, _ ->
            if(id == EditorInfo.IME_ACTION_DONE) {
                binding.btnSave.performClick()
            }
            false
        }


        binding.btnSave.setOnClickListener {
            hideKeyboard()
            if(checkNewValue()) {
                onSaveClick(binding.etEdit.text.toString())
                dismiss()
            }
        }
    }

    private fun checkNewValue(): Boolean {
        if(type == NAME) {
            if(validateName(binding.etEdit.text?.toString())) {
                return true
            } else {
                binding.ilEdit.isErrorEnabled = true
                binding.ilEdit.error = "Name can't be empty"
                return false
            }
        } else if(type == EMAIL) {
            if(validateEmail(binding.etEdit.text?.toString())) {
                return true
            } else {
                binding.ilEdit.isErrorEnabled = true
                binding.ilEdit.error = "invalid email address"
                return false
            }
        } else {
            if(validateID(binding.etEdit.text?.toString())) {
                return true
            } else {
                binding.ilEdit.isErrorEnabled = true
                binding.ilEdit.error = "invalid ID"
                return false
            }
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
}