package com.example.latihansatu.model

import com.google.gson.annotations.SerializedName

// Ini adalah Koper JSON yang 100% meniru CreateReportDTO di Spring Boot
data class CreateReportRequest(
    @SerializedName("description")
    val description: String,

    @SerializedName("locationHint")
    val locationHint: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("photoBase64")
    val photoBase64: String,

    @SerializedName("categoryId")
    val categoryId: String,

    @SerializedName("regionId")
    val regionId: String,

    @SerializedName("photoTakenAt")
    val photoTakenAt: String
)