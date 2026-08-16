package com.example.latihansatu.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.customview.view.AbsSavedState
import com.example.latihansatu.R
import kotlinx.coroutines.FlowPreview
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.core.content.ContextCompat
import android.Manifest.permission
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.example.latihansatu.model.CreateReportRequest
import com.example.latihansatu.viewmodel.AddReportViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.io.encoding.Base64

class AddReportActivity : AppCompatActivity() {

    // Otak aplikasi (ViewModel)
    private lateinit var viewModel: AddReportViewModel

    // 1. Siapkan wadah kosong untuk semua elemen UI
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var etTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etLocationHint: EditText
    private lateinit var btnGetLocation: Button
    private lateinit var tvCoordinateResult: TextView
    private lateinit var etDescription: EditText
    private lateinit var btnTakePhoto: Button
    private lateinit var ivPreview: ImageView
    private lateinit var btnSubmitReport: Button

    // Variabel untuk menyimpan titik GPS asli
    private var latitudeAsli: Double? = null
    private var longitudeAsli: Double? = null

    // Kamus Penerjemah: Teks Spinner --> UUID Database
    private val kamusKategori = mapOf(
        "Kerusakan Jalan/Infrastruktur" to "e3cc7f7a-110e-472c-b072-e1cb3ef66d26",
        "Fasilitas Penerangan Jalan" to "1680bccf-b00f-460a-8e00-1e8fedaf932e",
        "Pemeliharaan Taman/Ruang Publik" to "196e2f9d-af5d-43bb-ae7f-8521341131a9",
        "Penanganan Kebersihan/Sampah" to "b5b04f6f-cc21-4fc4-8a3d-35c642d76373"
    )
    // variabel untuk menyimpan hasil sandi foto
    private var fotoBase64: String? = null

    // Kunci Gudang (URI) untuk lokasi foto HD
    private lateinit var photoUri: android.net.Uri

    // Mesin penangkap hasil dari aplikasi kamarea
    private lateinit var kameraLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_report)

        // 2. Katikan wadah kosong dengan ID yang ada di XML
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etLocationHint = findViewById(R.id.etLocationHint)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        tvCoordinateResult = findViewById(R.id.tvCoordinateResult)
        etDescription = findViewById(R.id.etDescription)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        ivPreview = findViewById(R.id.ivPreview)
        btnSubmitReport = findViewById(R.id.btnSubmitReport)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Membangunkan ViewModel secara klasik
        viewModel = androidx.lifecycle.ViewModelProvider(this)[AddReportViewModel::class.java]

        // Menghidupkan Mesin dan Pengangkap Kamera
        kameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {sukses ->
            if(sukses) {
                // 1. Panjang foto langsung dari GUdang(photoUri)
                ivPreview.setImageURI(photoUri)
                ivPreview.visibility = View.VISIBLE

                // 2. Terjemahkan foto HD tersebut ke Base64
                fotoBase64 = ubahUriKeBase64(photoUri)
            }
            else {
                Toast.makeText(this, "Batal mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }

        // ==========================================
        // MENGHIDUPKAN SENSOR 1: DROPDOWN KATEGORI
        // ==========================================
        val daftarKategori = arrayOf(
            "Pilih kategori",
            "Kerusakan Jalan/Infrastruktur",
            "Fasilitas Penerangan Jalan",
            "Pemeliharaan Taman/Ruang Publik",
            "Penanganan Kebersihan/Sampah"
        )

        // Panggil pelayan (ArrayAdapter) dan beri dia barangnya
        val pelayanSpinner = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            daftarKategori
        )

        // Suruh pelayan menaruh barang ke Rak SPinner
        spinnerCategory.adapter = pelayanSpinner

        // 3. Pasang sensor klik sementara (Dummy)
        btnGetLocation.setOnClickListener {
            cekDanAmbilLokasi()
        }

        btnTakePhoto.setOnClickListener {
            // 1. Buat file kosong sementara di memori HP
            val fileFoto = createTempFile(
                "FOTO_BUKTI_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )

            // 2. Buat "Kunci Gudang" (URI) dari file kosong tersebut menggunakan FIleProvider
            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                fileFoto
            )

            // 3. Suruh kamera menyala dan berikan kunci gudangnya!
            kameraLauncher.launch(photoUri)
        }

        btnSubmitReport.setOnClickListener {
            // Tahap 1: Pengumpulan Bahan Mentah

            // 1 & 2: AMbil teks dari ketikan warga
            val deskripsi = etDescription.text.toString().trim()
            val patokanLokasi = etLocationHint.text.toString().trim()

            // 3. Ambil kategori dari Rak Spinner
            // Nanti harus diubah teks ini jadi UUID asli permintaan database
            val namaKategori = spinnerCategory.selectedItem.toString()

            // 4. Ambil waktu saat ini (Sesuai format ISO-8601 yang diminta spring boot)
            val waktuSekarang = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(
                Date()
            )

            // 5 Region ID
            // Ganti nanti ke UUID REGION ID yang vali di PostfreSQL
            val regionDummy = "933fb792-f0da-4d5d-a657-ec1259845ba7"


            // Tahap 2: Validasi
            if(deskripsi.isEmpty() || namaKategori == "Pilih Kategori...") {
                Toast.makeText(this, "Kategori dan Deskripsi Wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Berhenti di sini, jangna kirim kopernya!
            }
            if (fotoBase64 == null) {
                Toast.makeText(this, "Bukti foto wajib dilampirkan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cek apakah GPS sudah ditekan
            if(latitudeAsli == null || longitudeAsli == null) {
                Toast.makeText(this, "Ambil koordinat GPS dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Terjemahkan nama kategori menjadi UUID pakai kamus!
            val uuidKategori = kamusKategori[namaKategori]
            if (uuidKategori == null) {
                Toast.makeText(this, "Kategori tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Masukkan Barang ke Dalam Koper
            val koperLaporanBaru = CreateReportRequest (
                description = deskripsi,
                locationHint = patokanLokasi,
                latitude = latitudeAsli!!,
                longitude = longitudeAsli!!,
                photoBase64 = fotoBase64!!,
                categoryId = uuidKategori, // Nanti pakai UUID asli
                regionId = "933fb792-f0da-4d5d-a657-ec1259845ba7",
                photoTakenAt = waktuSekarang
            )

          // Serahkan koper ke Otak (ViewModel) untuk dikirim!
            viewModel.kirimLaporan(koperLaporanBaru, this)

            // Ubah tombol jadi loading
            btnSubmitReport.text = "Mengirim..."
            btnSubmitReport.isEnabled = false
        }

        viewModel.uploadResult.observe(this) {result ->
            // kembalikan tombol seperti semula
            btnSubmitReport.text = "KIRIM LAPORAN"
            btnSubmitReport.isEnabled = true

            result.fold(
                onSuccess = {balasan ->
                    Toast.makeText(this, "Sukses lapor! Tiket: ${balasan.id}", Toast.LENGTH_LONG).show()
                    // tutup layar ini dan ke kembali ke menu utama
                    finish()
                },
                onFailure = {error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun cekDanAmbilLokasi() {
        // 1. Bertanya ke Satpan OS Andorid: "Apakah Izin Lokasi Sudah diberikan?"
        val izinDiberikan = ContextCompat.checkSelfPermission(
            this,
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (izinDiberikan) {
            // 2. Kalau sudah diizinkan, suruh mesin pelakack mengambnil lokasi terakhir
            fusedLocationClient.lastLocation.addOnSuccessListener { lokasi ->
                if (lokasi != null) {

                    // Simpan Lokasi Asli
                    latitudeAsli = lokasi.latitude
                    longitudeAsli = lokasi.longitude

                    val teksKoordinat = "Latitude: ${lokasi.latitude}, Longitude: ${lokasi.longitude}"
                    tvCoordinateResult.text = teksKoordinat
                    tvCoordinateResult.setTextColor(android.graphics.Color.parseColor("#10b981"))
                }
                else {
                    Toast.makeText(this, "Nyalakan GPS Hp-mu dulu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else {
            // 3. Kalau belum diizinkan, munculkan pop-up permintaan izin ke Warga
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.ACCESS_FINE_LOCATION),
                100
            )
        }
    }

    private fun ubahUriKeBase64(uri: Uri): String? {
        return try {
            // sedot gambar dari gudang
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // kompres sedikit agar internet warga tidak jebol saat upload
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)

            // ubah jadi teks
            val susunanByte = outputStream.toByteArray()
            android.util.Base64.encodeToString(susunanByte, android.util.Base64.NO_WRAP)
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
