package com.example.latihansatu.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.latihansatu.R // Sesuaikan dengan nama package aplikasimu
import com.example.latihansatu.model.ReportResponse // Sesuaikan

// 1. Deklarasi Pelayan yang membawa daftar laporan (List)
class ReportAdapter(
    private var reportList: List<ReportResponse>,
    private val onClick: (ReportResponse) -> Unit // Ini jalur lapor ke Mandor yang membawa teks (String)
    ) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    // 2. Mengenali Gelas (Mencari ID dari item_report.xml)
    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    // 3. Mengambil cetakan item_report.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    // 4. Menentukan berapa banyak gelas yang harus disiapkan
    override fun getItemCount(): Int {
        return reportList.size
    }

    // ==============================================================
    // 5. MISI LOGIKAMU: MENUANGKAN DATA KE DALAM GELAS
    // ==============================================================
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        // Ambil satu laporan spesifik berdasarkan urutannya (baris ke-0, ke-1, dst)
        val reportSaatIni = reportList[position]

        // Masukkan data dari reportSaatIni ke dalam teks di layar (holder)

        // Pertanyaan 1: Variabel apa dari reportSaatIni yang diisi ke Judul?
        holder.tvTitle.text = reportSaatIni.title

        // Pertanyaan 2: Variabel apa yang diisi ke Kategori?
        holder.tvCategory.text = reportSaatIni.category?.namaKategori?: "Kategori Tidak Diketahui"

        // Pertanyaan 3: Variabel apa yang diisi ke Tanggal?
        holder.tvDate.text = reportSaatIni.date

        // SENSOR SENTUH BARU:
        // itemView adalah seluruh area kotak kartu tersebut
        holder.itemView.setOnClickListener {
            // Saat diklik, panggil jalur lapor ke Mandor sambil membawa ID (ticketNumber)
            onClick(reportSaatIni)
        }
    }

    fun perbaruiData(daftarBaru: List<ReportResponse>) {
        this.reportList = daftarBaru // Ganti daftar lama dengan daftar baru
        notifyDataSetChanged()       // Teriak ke RecyclerView: "Woy, data berubah! Gambar ulang layarnya!"
    }
}