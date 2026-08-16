package com.example.latihansatu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.latihansatu.model.ReportResponse
import com.example.latihansatu.network.ApiClient
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    // 1. Ini adalah Tandon Air untuk menampung daftar laporan sementara
    val listLaporan = MutableLiveData<List<ReportResponse>>()

    fun ambilDataDariServer(context: Context) {
        // 2. Menyalakan mesin pompa di jalur belakang (Background Thread) agar HP tidak hang
        viewModelScope.launch {
            try {
                // 3. MISI LOGIKAMU: Menyedot air dari server
                // Panggil keran ApiClient kita, dan jalankan fungsi untuk mengambil seluruh data laporan.
                // Clue: Ingat kembali nama fungsi @GET("api/reports") yang kamu buat di ApiService.kt!
                val responsDariServer = ApiClient.getInstance(context).fetchSemuaLaporan()

                // 4. Masukkan air yang berhasil disedot ke dalam Tandon
                listLaporan.value = responsDariServer

            } catch (e: Exception) {
                // Memaksa sistem berteriak dengan label "CEK_POMPA" berwarna merah di Logcat
                android.util.Log.e("CEK_POMPA", "Gagal menyedot air karena: ${e.message}")
            }
        }
    }
}