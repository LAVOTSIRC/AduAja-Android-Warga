package com.example.latihansatu.ui
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.latihansatu.R
import com.example.latihansatu.network.ApiClient // Sesuaikan jika foldernya beda
import com.example.latihansatu.network.ApiService
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val tvStatus = findViewById<TextView>(R.id.status)
        val tvTicketNumber = findViewById<TextView>(R.id.ticketnumber)
        val tvCategory = findViewById<TextView>(R.id.category)
        val tvDescription = findViewById<TextView>(R.id.description)

        // 1. Bongkar koper bawaan Kurir
        val tiketMasuk = intent.getStringExtra("KUNCI_TIKET") ?: "Tidak ada tiket"
        val kategoriMasuk = intent.getStringExtra("KUNCI_KATEGORI") ?: "Tidak ada kategori"
        val statusMasuk = intent.getStringExtra("KUNCI_STATUS") ?: "Tidak ada status"
        val uuidMasuk = intent.getStringExtra("KUNCI_UUID") // Menangkap UUID

        // 2. Tuangkan data instan ke layar
        tvTicketNumber.text = tiketMasuk
        tvCategory.text = kategoriMasuk
        tvStatus.text = statusMasuk

        // 3. Tarik data lengkap dari Azure di latar belakang
        if (uuidMasuk != null) {
            // lifecycleScope.launch ini adalah Coroutine (pekerja latar belakang)
            lifecycleScope.launch {
                try {
                    // Menyedot data dari Spring Boot pakai UUID
                    val responsDetail = ApiClient.getInstance(this@DetailActivity).getDetailLaporan(uuidMasuk)

                    // Ingat trik kita sebelumnya?
                    // Di ReportResponse.kt, kata "description" dari JSON ditangkap oleh variabel bernama "title"
                    tvDescription.text = responsDetail.title
                    // 1. Hubungkan pigura XML ke Kotlin
                    val ivPhoto = findViewById<android.widget.ImageView>(R.id.ivPhoto)
                    val sandiBase64 = responsDetail.photoBase64

                    // 2. Cek apakah warga melampirkan foto
                    if (sandiBase64 != null) {
                        // Bersihkan teks (kadang web mengirim awalan "data:image/png;base64,")
                        val sandiBeresih = sandiBase64.substringAfter(",")

                        // Terjemahkan sandi menjadi susunan byte memori
                        val susunanByte = android.util.Base64.decode(sandiBeresih, android.util.Base64.DEFAULT)

                        // Rakit byte memori menjadi gambar utuh (Bitmap)
                        val gambarAsli = android.graphics.BitmapFactory.decodeByteArray(susunanByte, 0, susunanByte.size)

                        // Pajang gambar ke pigura
                        ivPhoto.setImageBitmap(gambarAsli)
                        ivPhoto.visibility = android.view.View.VISIBLE
                    } else {
                        // Kalau tidak ada foto, hilangkan piguranya agar layar tidak bolong
                        ivPhoto.visibility = android.view.View.GONE
                    }

                } catch (e: Exception) {
                    // Kalau gagal (misal server mati/lemot), kasih tau errornya
                    tvDescription.text = "Gagal memuat deskripsi: ${e.message}"
                }
            }
        }
    }
}