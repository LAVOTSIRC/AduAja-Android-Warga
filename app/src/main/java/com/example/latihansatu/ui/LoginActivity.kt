package com.example.latihansatu.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.customview.view.AbsSavedState
import com.example.latihansatu.R
import com.example.latihansatu.utils.SessionManager
import com.example.latihansatu.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    // Panggil Otak (ViewModel)
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Panggil Dompet
        val sessionManager = SessionManager(this)

        // BLOK KODE AUTO-LOGIN
        if (sessionManager.fetchAuthToken() != null) {
            // Warga sudah punya token! Langsung pindahkan ke layyar Utama
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // KALAU DOMPET KOSONG, BARU GAMBAR LAYAR LOGIN
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 1. Dengarkan pengunmuman dari otak (viewModel) lewat LiveData
        loginViewModel.LoginResult.observe(this) {result ->
            result.onSuccess { authResponse ->
                // Hore! Sukses dapat token dari server! Simpan token ke dompet.
                sessionManager.saveAuthToken(authResponse.token, authResponse.role)
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                // Pindah ke halaman utama (MainACtivity)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Tutup halaman login ini
            }
            result.onFailure { error ->
                // Gagal :( Tampilkan pesan eror dari Satpam Spring Boot
                Toast.makeText(this, "Gagal: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }

        // 2. Saat tombol diklik, perintahkan Otak bekerja!
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // suruh kurir berangkat lewat ViewModel (ini otomatis jalan di later belakang
                loginViewModel.performLogin(this, email, password)
            }
            else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}