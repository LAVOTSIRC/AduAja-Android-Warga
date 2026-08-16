package com.example.latihansatu.network

// import model
import com.example.latihansatu.model.ReportResponse
import com.example.latihansatu.model.AuthRequest
import com.example.latihansatu.model.AuthResponse
import com.example.latihansatu.model.CreateReportRequest

// Import Retrofit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Tanda {id} berarti nilai tersebut akan diganti secara dinamis.
    @GET("api/reports/{id}")
    suspend fun getDetailLaporan(
        // 2. Ganti @Query menjadi @Path agar menempel langsung ke URL
        @Path("id") id: String
    ): ReportResponse
    @GET("/api/reports/my-reports")
    suspend fun fetchSemuaLaporan(): List<ReportResponse>

    // LOGIN
    @POST("api/auth/login") // Login Wajib Post
    suspend fun login(
        @Body request: AuthRequest // Data rahasia dimasukkan ke dalam kotak Body (JSON)
    ): Response<AuthResponse>// balasannya berisi token

    // Rute untuk mengirim laporan baru
    @POST("api/reports")
    suspend fun createReport(
        @Body request: CreateReportRequest
    ): Response<ReportResponse>
}