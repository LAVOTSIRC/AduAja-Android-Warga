package com.example.latihansatu.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.latihansatu.model.CreateReportRequest
import com.example.latihansatu.model.ReportResponse
import com.example.latihansatu.network.ApiClient
import kotlinx.coroutines.launch

class AddReportViewModel : ViewModel() {

    // Pengeras Suara untuk mengabari layar apakah sukses atau gagal
    private val _uploadResult = MutableLiveData<Result<ReportResponse>>()
    val uploadResult: LiveData<Result<ReportResponse>> = _uploadResult

    // fungsi untuk menuruh kurir berangkat
    fun kirimLaporan(koper: CreateReportRequest, context: Context) {
        viewModelScope.launch {
            try {
                // Memanggil Asisten Kurir (ApiClient) yang otomatis membawa Token JWT di dompet
                val response = ApiClient.getInstance(context).createReport(koper)

                if(response.isSuccessful && response.body() != null) {
                    // kalau suskes (HTTP 200 OK), teriakkan hasilnya ke layar!
                    _uploadResult.value = Result.success(response.body()!!)
                }
                else {
                    // kalau gagal (misal 400 atau 500), beritahu eror-nya
                    _uploadResult.value = Result.failure(Exception("Gagal: ${response.message()}"))
                }
            }catch (e: Exception) {
                // Kalau internet mati atau server mati
                _uploadResult.value = Result.failure(e)
            }
        }
    }
}