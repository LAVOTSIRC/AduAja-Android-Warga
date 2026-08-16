package com.example.latihansatu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.latihansatu.model.AuthResponse
import com.example.latihansatu.model.AuthRequest
import com.example.latihansatu.network.ApiClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // LiveData: Pengeras suara untuk mengabari layar Activity apakah login sukses/gagal
    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val LoginResult: LiveData<Result<AuthResponse>> = _loginResult

    fun performLogin(context: Context, email: String, pass: String) {

        // Membuka jalur Coroutine untuk menjalankan tugas berat
        viewModelScope.launch {
            try {
                // 1. Siapkan paket data (JSON)
                val request = AuthRequest(email, pass)

                // 2. Suruh Kurir berangkat ke Loket Spring Boot
                val response = ApiClient.getInstance(context).login(request)

                // 3. Cek balasan dari Satpam Srping Boot
                if(response.isSuccessful && response.body() != null) {
                    // berhasil dapat Token! Umumkan ke layar!
                    _loginResult.value = Result.success(response.body()!!)
                }
                else {
                    // Ditolak (misal: password salah). Umumkan eror ke layar!
                    _loginResult.value = Result.failure(Exception("login Gagal: ${response.code()}"))
                }
            }catch (e: Exception) {
                // Kurir Kecelakaan di jalan (misal: WiFi putus atau server mati)
                _loginResult.value = Result.failure(e)
            }
        }
    }
}