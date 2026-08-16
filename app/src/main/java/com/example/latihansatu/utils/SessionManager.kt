package com.example.latihansatu.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // Membuat atau membuka dompet bernama "AduAjaPrefs"
    private var prefs: SharedPreferences = context.getSharedPreferences("AduAjaPrefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ROLE = "user_role"
    }

    // Fungsi untuk memasukkan TOken dan ROle ke dalam dompet
    fun saveAuthToken(token: String, role: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putString(USER_ROLE, role)
        editor.commit() // Simpan secara asinkron (tidak bikin UI nge-freeze)
    }

    // Fungsi untuk mengambil token dari dompet (bisa null kalau belum login)
    fun fetchAuthToken(): String? {
        return  prefs.getString(USER_TOKEN, null)
    }

    // fungsi untuk mengambil Role
    fun fetchUserRole() : String? {
        return  prefs.getString(USER_ROLE, null)
    }

    // Fungsi untuk membuang dompet (logout)
    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}