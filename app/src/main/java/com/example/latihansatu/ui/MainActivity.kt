package com.example.latihansatu.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.latihansatu.R
import com.example.latihansatu.viewmodel.ReportViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var recyclerView: RecyclerView

    // TAMBAHKAN VARIABEL PELAYAN GLOBAL DI SINI:
    private lateinit var pelayanUtama: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rvReports)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 1. BUAT PELAYAN SATU KALI SAJA (Dengan daftar kosong di awal)
        pelayanUtama = ReportAdapter(emptyList()) { laporanYangDiklik ->
            val kurir = android.content.Intent(this, DetailActivity::class.java)
            kurir.putExtra("KUNCI_TIKET", laporanYangDiklik.id)
            kurir.putExtra("KUNCI_KATEGORI", laporanYangDiklik.category?.namaKategori)
            kurir.putExtra("KUNCI_STATUS", laporanYangDiklik.status)
            kurir.putExtra("KUNCI_UUID", laporanYangDiklik.reportId)
            startActivity(kurir)
        }

        // Pasangkan pelayan ke nampan
        recyclerView.adapter = pelayanUtama

        viewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        // 2. ROMBAK PEMANTAUAN (OBSERVE)
        viewModel.listLaporan.observe(this) { dataLaporan ->
            // Setiap kali ada air/data baru datang dari server,
            // JANGAN buat pelayan baru. Suruh pelayan lama memperbarui datanya!
            pelayanUtama.perbaruiData(dataLaporan)
        }

        val fabAdd = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddReport)
        fabAdd.setOnClickListener {
            val intentTambah = android.content.Intent(this, AddReportActivity::class.java)
            startActivity(intentTambah)
        }
    }

    override fun onResume() {
        super.onResume()
        // Sedot data setiap kali layar kembali aktif
        viewModel.ambilDataDariServer(this)
    }
}