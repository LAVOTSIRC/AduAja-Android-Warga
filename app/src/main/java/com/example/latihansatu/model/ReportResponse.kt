package com.example.latihansatu.model

import com.google.gson.annotations.SerializedName

data class ReportResponse (
    @SerializedName("photoBase64")
    val photoBase64: String?,

    @SerializedName("reportId")
    val reportId: String,

    // Tangkap dari reportId atau ticketNumber (kita pakai ticketNumber agar rapi)
    @SerializedName("ticketNumber")
    val id: String,

    // Karena backend tidak punya title, kita tangkap description untuk dijadikan judul
    @SerializedName("description")
    val title: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("category")
    val category: CategoryResponse?,

    // Sesuaikan dengan JSON: locationHint
    @SerializedName("locationHint")
    val location: String,

    // Sesuaikan dengan JSON: createdAt
    @SerializedName("createdAt")
    val date: String
)
// Kita buat cetakan khusus untuk objek Kategori
data class CategoryResponse(
    // Ini menangkap nama kategorinya (misal: "Infrastruktur")
    @SerializedName("categoryName")
    val namaKategori: String
)