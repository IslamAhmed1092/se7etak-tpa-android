package com.example.se7etak_tpa.home_ui.RequestApprovalAbstract

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.se7etak_tpa.Utils.*
import com.example.se7etak_tpa.databinding.FragmentRequestApprovalBinding
import com.example.se7etak_tpa.showLoadingDialog
import com.theartofdev.edmodo.cropper.CropImage


abstract class RequestApprovalAbstractClass(requestApprovalViewModel: RequestApprovalAbstractClassViewModel) : Fragment() {
    private var requestApprovalAbstractClassViewModel: RequestApprovalAbstractClassViewModel
    init {
        this.requestApprovalAbstractClassViewModel = requestApprovalViewModel
    }

    private var _binding: FragmentRequestApprovalBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requestApprovalAbstractClassViewModel =
            ViewModelProvider(this).get(requestApprovalAbstractClassViewModel::class.java)

        _binding = FragmentRequestApprovalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.requestApprovalAttachment1View.visibility = View.GONE
        binding.requestApprovalAttachment2View.visibility = View.GONE

        requestApprovalAbstractClassViewModel.attachment1Exists.observe(viewLifecycleOwner, {
            if (it) binding.requestApprovalAttachment1View.visibility = View.VISIBLE
            else binding.requestApprovalAttachment1View.visibility = View.GONE
        })


        requestApprovalAbstractClassViewModel.attachment1Name.observe(viewLifecycleOwner, {
            binding.requestApprovalAttachment1View.requestApprovalAttachmentNameTextView.text =
                it
            binding.requestApprovalAttachment1View.requestApprovalRemoveAttachmentImageButton.setOnClickListener {
                // order is important
                deleteFile(
                    getPath(
                        requireContext(),
                        requestApprovalAbstractClassViewModel.attachment1Name.value!!
                    )
                )
                requestApprovalAbstractClassViewModel.attachment1Name.value = null
                requestApprovalAbstractClassViewModel.attachment1Exists.value = false
            }
        })



        requestApprovalAbstractClassViewModel.attachment2Exists.observe(viewLifecycleOwner,{
            if(it) binding.requestApprovalAttachment2View.visibility = View.VISIBLE
            else binding.requestApprovalAttachment2View.visibility = View.GONE
        })
        requestApprovalAbstractClassViewModel.attachment2Name.observe(viewLifecycleOwner, {
            binding.requestApprovalAttachment2View.requestApprovalAttachmentNameTextView.text =
                requestApprovalAbstractClassViewModel.attachment2Name.value
            binding.requestApprovalAttachment2View.requestApprovalRemoveAttachmentImageButton.setOnClickListener {
                deleteFile(
                    getPath(
                        requireContext(),
                        requestApprovalAbstractClassViewModel.attachment2Name.value!!
                    )
                )
                requestApprovalAbstractClassViewModel.attachment2Name.value = null
                requestApprovalAbstractClassViewModel.attachment2Exists.value = false
            }
        })


        binding.requestApprovalAddAttachmentImageButton.setOnClickListener {
            if (requestApprovalAbstractClassViewModel.attachment1Name.value != null && requestApprovalAbstractClassViewModel.attachment2Name.value != null)
                Toast.makeText(
                    requireContext(),
                    "You can't add more than two attachments!",
                    Toast.LENGTH_SHORT
                ).show()
            else Utils.chooseAttachment(this)
        }



        binding.requestApprovalSubmitButton.setOnClickListener {

            if (validate()) {
                val user = loadUserData(requireContext())
                user.let {
                    showLoadingDialog()
                    Thread {
                        requestApprovalAbstractClassViewModel.submitRequest(
                            it.token,
                            requireContext()
                        )
                        requestApprovalAbstractClassViewModel.sendEmail(it.id, requireContext())
                    }.start()
                }
            }
        }

        val providerTypeSpinner = binding.requestApprovalProviderTypeSpinner
        providerTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                requestApprovalAbstractClassViewModel.providerTypeSelectedPosition.value = position
                requestApprovalAbstractClassViewModel.providerTypeList.value?.get(position)
                    ?.let { requestApprovalAbstractClassViewModel.getProviderName(it) }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }
        requestApprovalAbstractClassViewModel.providerTypeSelectedPosition.observe(
            viewLifecycleOwner,
            {
                providerTypeSpinner.setSelection(it)
            })


        val providerNameSpinner = binding.requestApprovalProviderNameSpinner
        providerNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                requestApprovalAbstractClassViewModel.providerNameSelectedPosition.value = position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        requestApprovalAbstractClassViewModel.providerNameSelectedPosition.observe(
            viewLifecycleOwner,
            {
                providerNameSpinner.setSelection(it)
            })

        binding.requestApprovalCommentEditText.setText(requestApprovalAbstractClassViewModel.comment)

        binding.requestApprovalCommentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(e: Editable?) {
                requestApprovalAbstractClassViewModel.comment = e?.trim().toString() ?: ""
            }
        })


        requestApprovalAbstractClassViewModel.providerTypeList.observe(viewLifecycleOwner, {
            it?.let { binding.requestApprovalProviderTypeSpinner.item = it }
        })

        requestApprovalAbstractClassViewModel.providerNameWithIdList.observe(viewLifecycleOwner, {
            it?.let {
                val providerNameList = ArrayList<String>()
                for (providerNameWithId in it) {
                    providerNameList.add(providerNameWithId.providerName)
                }
                binding.requestApprovalProviderNameSpinner.item = providerNameList as List<Any>?
            }
        })


        return root
    }

    override fun onDestroyView() {
        if(requestApprovalAbstractClassViewModel.attachment1Exists.value!!) deleteFile(getPath(requireContext(),requestApprovalAbstractClassViewModel.attachment1Name.value!!))
        if(requestApprovalAbstractClassViewModel.attachment2Exists.value!!) deleteFile(getPath(requireContext(),requestApprovalAbstractClassViewModel.attachment2Name.value!!))
        deleteAllImages(requireContext())
        super.onDestroyView()

        _binding = null
    }


    private fun validate(): Boolean {
        val providerTypeSpinner = binding.requestApprovalProviderTypeSpinner
        val providerNameSpinner = binding.requestApprovalProviderNameSpinner
        var flag = true
        if (!requestApprovalAbstractClassViewModel.attachment1Exists.value!! && !requestApprovalAbstractClassViewModel.attachment2Exists.value!!) {
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
                if (requestCode == Utils.REQUEST_IMAGE_CAPTURE) {
                    uri = Utils.getImageUri(
                        requireActivity().applicationContext,
                        data?.extras?.get("data") as Bitmap
                    )
                }
                if (!requestApprovalAbstractClassViewModel.attachment1Exists.value!!) requestApprovalAbstractClassViewModel.attachment1Name.value =
                    getFileName(requireActivity(), uri)
                else requestApprovalAbstractClassViewModel.attachment2Name.value =
                    getFileName(requireActivity(), uri)
                CropImage.activity(uri)
                    .start(requireContext(), this)
            } else if (requestCode == Utils.RC_PDF_PICKER) { // pdf file
                data?.data?.also { uri ->
                    copyDirectoryOneLocationToAnotherLocation(
                        uri,
                        getPath(
                            requireContext(), getFileName(
                                requireContext(), uri
                            )
                        ), requireActivity()
                    )
                    if (!requestApprovalAbstractClassViewModel.attachment1Exists.value!!) {
                        requestApprovalAbstractClassViewModel.attachment1Name.value =
                            getFileName(requireContext(), uri)
                        requestApprovalAbstractClassViewModel.attachment1Exists.value = true
                    } else {
                        requestApprovalAbstractClassViewModel.attachment2Name.value =
                            getFileName(requireContext(), uri)
                        requestApprovalAbstractClassViewModel.attachment2Exists.value = true
                    }
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                val resultUri = result.uri
                if (!requestApprovalAbstractClassViewModel.attachment1Exists.value!!) {
                    copyDirectoryOneLocationToAnotherLocation(
                        resultUri!!,
                        getPath(requireContext(),requestApprovalAbstractClassViewModel.attachment1Name.value!!),
                        requireActivity()
                    )
                    requestApprovalAbstractClassViewModel.attachment1Exists.value = true
                } else {
                    copyDirectoryOneLocationToAnotherLocation(
                        resultUri!!,
                        getPath(requireContext(),requestApprovalAbstractClassViewModel.attachment2Name.value!!),
                        requireActivity()
                    )
                    requestApprovalAbstractClassViewModel.attachment2Exists.value = true
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    requireActivity(),
                    "upload failed, try again!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.w(requireActivity().packageName, "OnActivityResult: UCrop.RESULT_ERROR")
            } else
                super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Utils.WRITE_EXTERNAL_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.captureImage(this)
            } else {
                Toast.makeText(
                    requireContext(),
                    "failed, You have to accept permissions to use the camera!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}

