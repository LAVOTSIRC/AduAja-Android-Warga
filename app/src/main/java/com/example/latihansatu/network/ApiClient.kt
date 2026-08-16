package com.example.latihansatu.network

import android.content.Context
import com.example.latihansatu.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Pertanyaan 1: Masukkan IP Public VM Azure milikmu beserta port Spring Boot.
    // Aturan Retrofit: Base URL WAJIB diakhiri dengan garis miring (/)
    private const val BASE_URL = "http://192.168.1.7:8081/"

    // kita ubah menjadi fungsi agar bisa menerima 'Context' dari Activity
    fun getInstance(context: Context): ApiService {

        // 1. Panggil Dompet Rahasia Menggunakan context
        val sessionManager = SessionManager(context)

        // 2. Buat Asisten Kurir (Interceptor)
        val interceptor = Interceptor { chain ->
            // Tangkap kurir sebelum dia berangkat
            val requestBuilder = chain.request().newBuilder()

            // Ambil token dari dommpet
            val token = sessionManager.fetchAuthToken()

            // jika token ada (sudah login), pakaikan ke kerah bajunya
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            // Izinkan kurir melanjutkan perjalanan
            chain.proceed((requestBuilder.build()))
        }

        // 3. Daftarkan Asisten kuri ini ke dalam Markas (OkHttpClient)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // 4. Bangung ulang Retrofit dengan menyematkan Markas baru (.client)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- INI KUNCI UTAMANYA
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}