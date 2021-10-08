package com.example.se7etak_tpa

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context

private var dialog: Dialog? = null

fun buildLoadingDialog(context: Context){
    val builder = AlertDialog.Builder(context)
    builder.setCancelable(false)
    builder.setView(R.layout.loading_dialog)
    dialog = builder.create()
}

fun showLoadingDialog(){
    dialog?.show()
}

fun hideLoadingDialog(){
    dialog?.hide()
}

