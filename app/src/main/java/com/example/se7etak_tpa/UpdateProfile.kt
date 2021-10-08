package com.example.se7etak_tpa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast


class UpdateProfile : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etContactNo:EditText
    lateinit var etMembershipID:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        viewInitializations()
        supportActionBar?.hide()

    }


    fun viewInitializations() {

        etName = findViewById(R.id.et_name)
        etEmail  = findViewById(R.id.et_email)
        etContactNo = findViewById(R.id.et_contact_no)
        etMembershipID = findViewById(R.id.et_membershipID)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    fun validateInput(): Boolean {
        if (etName.text.toString().equals("")) {
            etName.setError("Please Enter First Name")
            return false
        }
        if (etEmail.text.toString().equals("")) {
            etEmail.setError("Please Enter Email")
            return false
        }

        if (etContactNo.text.toString().equals("")) {
            etContactNo.setError("Please Enter Contact No")
            return false
        }
        if (etMembershipID.text.toString().equals("")) {
            etMembershipID.setError("Please Enter your ID")
            return false
        }
        if (!isEmailValid(etEmail.text.toString())) {
            etEmail.setError("Please Enter Valid Email")
            return false
        }

        return true
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    fun performEditProfile (view: View) {
        if (validateInput()) {

            val Name = etName.text.toString()
            val email = etEmail.text.toString()
            val contactNo = etContactNo.text.toString()
            val membershipID = etMembershipID.text.toString()

            Toast.makeText(this,"Profile Update Successfully",Toast.LENGTH_SHORT).show()
            // Call API

        }
    }

}