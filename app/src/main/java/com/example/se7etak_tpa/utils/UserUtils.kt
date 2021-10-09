package com.example.se7etak_tpa.utils

import android.content.Context
import com.example.se7etak_tpa.data.User
import com.google.gson.Gson

fun saveUserData(context: Context, user: User) {
    val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    pref.edit()
        .putString("USER", Gson().toJson(user))
        .apply()
}

fun loadUserData(context: Context): User {
    val emptyJson = "{\"email\":\"\",\"id\":\"\",\"name\":\"\",\"phoneNumber\":\"\",\"token\":\"\"}"
    val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    return Gson().fromJson(pref.getString("USER", emptyJson), User::class.java)
}

fun deleteUserData(context: Context) {
    val pref = context.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    pref.edit().remove("USER").apply()
}