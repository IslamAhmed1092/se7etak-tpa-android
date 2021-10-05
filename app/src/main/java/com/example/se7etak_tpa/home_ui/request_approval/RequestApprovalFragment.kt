package com.example.se7etak_tpa.home_ui.request_approval

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.R
import com.example.se7etak_tpa.Utils.Utils
import com.example.se7etak_tpa.databinding.AttachmentCustomViewBinding
import com.example.se7etak_tpa.databinding.FragmentRequestApprovalBinding
import com.theartofdev.edmodo.cropper.CropImage

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

        binding.requestApprovalAttachment1View.visibility = View.GONE
        binding.requestApprovalAttachment2View.visibility = View.GONE

        requestApprovalViewModel.attachment1Uri.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.requestApprovalAttachment1View.visibility = View.GONE
                requestApprovalViewModel.attachment1Name.value = null
            } else {
                    binding.requestApprovalAttachment1View.visibility = View.VISIBLE
                    binding.requestApprovalAttachment1View.requestApprovalAttachmentNameTextView.text = requestApprovalViewModel.attachment1Name.value
                    binding.requestApprovalAttachment1View.requestApprovalRemoveAttachmentImageButton.setOnClickListener {
                        requestApprovalViewModel.attachment1Uri.value = null
                }
            }
        })


        requestApprovalViewModel.attachment2Uri.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.requestApprovalAttachment2View.visibility = View.GONE
                requestApprovalViewModel.attachment2Name.value = null
            } else {
                binding.requestApprovalAttachment2View.visibility = View.VISIBLE
                binding.requestApprovalAttachment2View.requestApprovalAttachmentNameTextView.text = requestApprovalViewModel.attachment2Name.value
                binding.requestApprovalAttachment2View.requestApprovalRemoveAttachmentImageButton.setOnClickListener {
                    requestApprovalViewModel.attachment2Uri.value = null
                }
            }
        })


        binding.requestApprovalAddAttachmentImageButton.setOnClickListener {
            if (requestApprovalViewModel.attachment1Uri.value != null && requestApprovalViewModel.attachment2Uri.value != null)
                Toast.makeText(
                    requireContext(),
                    "You can't add more than two attachments!",
                    Toast.LENGTH_SHORT
                ).show()
            else Utils.chooseAttachment(this)
        }

        binding.requestApprovalSubmitButton.setOnClickListener {
            if (validate()) {
                val providerType = binding.requestApprovalProviderTypeSpinner.selectedItem
                val providerName = binding.requestApprovalProviderNameSpinner.selectedItem
                val comment = binding.requestApprovalCommentEditText.text

                requestApprovalViewModel.sendEmail()
            }
        }
        val list = arrayListOf<String>("AAA", "ABC", "BDC", "dfsd")

        val providerTypeSpinner = binding.requestApprovalProviderTypeSpinner

        providerTypeSpinner.item = list as List<Any>?
        providerTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                requestApprovalViewModel.providerNameSelectedPosition.value = position
                Toast.makeText(requireActivity(), list[position], Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        requestApprovalViewModel.providerTypeSelectedPosition.observe(viewLifecycleOwner, {
            Log.d("Request", it.toString())
            providerTypeSpinner.setSelection(it)
        })


        val providerNameSpinner = binding.requestApprovalProviderNameSpinner
        providerNameSpinner.item = list as List<Any>?

        providerNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                requestApprovalViewModel.providerNameSelectedPosition.value = position
                Toast.makeText(requireActivity(), position.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

//        requestApprovalViewModel.providerNameSelectedPosition.observe(viewLifecycleOwner, {
//            providerNameSpinner.setSelection(it)
//        })

            binding.requestApprovalCommentEditText.setText(requestApprovalViewModel.comment.value ?: "")

//        binding.requestApprovalCommentEditText.addTextChangedListener(object: TextWatcher{
//            override fun afterTextChanged(e: Editable?) {
//                TODO("Not yet implemented")
//            }
//        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun validate(): Boolean {
        val providerTypeSpinner = binding.requestApprovalProviderTypeSpinner
        val providerNameSpinner = binding.requestApprovalProviderNameSpinner
        var flag = true
        if (requestApprovalViewModel.attachment1Uri.value == null || requestApprovalViewModel.attachment2Uri.value == null) {
            Toast.makeText(requireActivity(), "Attachments must not be empty!", Toast.LENGTH_SHORT)
                .show()
            flag = false
        }
        if (providerTypeSpinner.selectedItem == null) {
            providerTypeSpinner.errorText = "You must select an item!"
            flag = false
        }
        if (providerNameSpinner.selectedItem == null) {
            providerNameSpinner.errorText = "You must select an item!"
            flag = false
        }
        return flag
    }


        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            // get the image selected for Attachment.
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (requestCode == Utils.RC_PHOTO_PICKER || requestCode == Utils.REQUEST_IMAGE_CAPTURE) {
                    var uri = data?.data
                    if (requestCode == Utils.REQUEST_IMAGE_CAPTURE) uri = Utils.getImageUri(
                        requireActivity().applicationContext,
                        data?.extras?.get("data") as Bitmap
                    )
                    if (requestApprovalViewModel.attachment1Name.value == null) requestApprovalViewModel.attachment1Name.value =
                        Utils.getFileName(requireActivity(), uri)
                    else requestApprovalViewModel.attachment2Name.value =
                        Utils.getFileName(requireActivity(), uri)
                    CropImage.activity(uri)
                        .start(requireContext(), this)
                } else if (requestCode == Utils.RC_PDF_PICKER) { // pdf file
                    data?.data?.also { uri ->
                        if (requestApprovalViewModel.attachment1Uri.value == null) requestApprovalViewModel.attachment2Uri.value =
                            uri
                        else requestApprovalViewModel.attachment1Uri.value = uri
                    }
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    val result = CropImage.getActivityResult(data);
                    val resultUri = result.uri;
                    if (requestApprovalViewModel.attachment1Uri.value == null) requestApprovalViewModel.attachment1Uri.value =
                        resultUri
                    else requestApprovalViewModel.attachment2Uri.value = resultUri

                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(
                        requireActivity(),
                        "upload failed, try again!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.w(requireActivity().packageName, "OnActivityResult: UCrop.RESULT_ERROR")
                }
            } else super.onActivityResult(requestCode, resultCode, data)

        }

    }