package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivityDetailSiswaBinding
import com.example.absensisiswaproto.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Locale

class DetailSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSiswaBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: RiwayatSiswaAdapter

    private var siswaId: Int = 0
    private var siswaNis: String = ""
    private var siswaNama: String = ""
    private var siswaKelas: String = ""
    private var tanggal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        siswaId = intent.getIntExtra("EXTRA_SISWA_ID", 0)
        siswaNis = intent.getStringExtra("EXTRA_SISWA_NIS") ?: ""
        siswaNama = intent.getStringExtra("EXTRA_SISWA_NAMA") ?: "Siswa"
        siswaKelas = intent.getStringExtra("EXTRA_SISWA_KELAS") ?: "-"
        tanggal = intent.getStringExtra("EXTRA_TANGGAL") ?: ""

        setupHeader()
        setupProfil()
        setupRecyclerView()
        setupBottomNav()

        loadStatusHariIni()
        loadRiwayat()
    }

    private fun setupHeader() {
        binding.btnBackDetail.setOnClickListener { finish() }
    }

    private fun setupProfil() {
        binding.tvNamaDetail.text = siswaNama
        binding.tvNisDetail.text = "NIS$siswaNis"
        binding.tvKelasDetail.text = siswaKelas
        binding.ivFotoDetail.setImageResource(SiswaHelper.getFoto(siswaId))
    }

    private fun setupRecyclerView() {
        adapter = RiwayatSiswaAdapter(emptyList())
        binding.rvRiwayatSiswa.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayatSiswa.adapter = adapter
    }

    private fun loadStatusHariIni() {
        val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val displayTanggal = dateParser.parse(tanggal)?.let { dateFormatter.format(it) } ?: tanggal

        binding.tvStatusBigTanggal.text = displayTanggal

        if (SiswaHelper.isWeekend(tanggal)) {
            binding.tvStatusBigLabel.text = "Libur"
            binding.tvStatusBigLabel.setTextColor(0xFF607D8B.toInt())
            binding.tvStatusBigJam.text = "-"
            binding.ivStatusBig.setBackgroundResource(R.drawable.bg_status_libur_small)
            binding.ivStatusBig.setImageResource(R.drawable.ic_kalendar)
            binding.ivStatusBig.setColorFilter(0xFFFFFFFF.toInt())

            binding.cardStatusDetail.setOnClickListener {
                Toast.makeText(
                    this,
                    "Hari libur, tidak bisa ubah status",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val dbAbsen = db.getAbsensiByUserTanggal(siswaId, tanggal)
        val statusFromDb = dbAbsen?.status

        val finalStatus = if (statusFromDb != null) {
            statusFromDb
        } else {
            val fullRiwayat = RiwayatProvider.getAll(siswaId, db.getRiwayatAbsen(siswaId))
            val match = fullRiwayat.find { it.tanggal == tanggal }
            match?.status ?: "belum"
        }

        val jam = dbAbsen?.jam ?: "-"
        binding.tvStatusBigJam.text = jam

        when (finalStatus.lowercase()) {
            "hadir" -> {
                binding.tvStatusBigLabel.text = "Hadir"
                binding.tvStatusBigLabel.setTextColor(0xFF1E8827.toInt())
                binding.ivStatusBig.setBackgroundResource(R.drawable.bg_status_hadir_small)
                binding.ivStatusBig.setImageResource(R.drawable.ic_check)
                binding.ivStatusBig.setColorFilter(0xFFFFFFFF.toInt())
            }
            "telat" -> {
                binding.tvStatusBigLabel.text = "Telat"
                binding.tvStatusBigLabel.setTextColor(0xFFB8860B.toInt())
                binding.ivStatusBig.setBackgroundResource(R.drawable.bg_status_telat_small)
                binding.ivStatusBig.setImageResource(R.drawable.ic_warning)
                binding.ivStatusBig.setColorFilter(0xFF000000.toInt())
            }
            "absen", "alpha" -> {
                binding.tvStatusBigLabel.text = "Alpha"
                binding.tvStatusBigLabel.setTextColor(0xFFE53935.toInt())
                binding.ivStatusBig.setBackgroundResource(R.drawable.bg_status_alpha_small)
                binding.ivStatusBig.setImageResource(R.drawable.ic_remove)
                binding.ivStatusBig.setColorFilter(0xFFFFFFFF.toInt())
            }
            else -> {
                binding.tvStatusBigLabel.text = "Belum Absen"
                binding.tvStatusBigLabel.setTextColor(0xFF888888.toInt())
                binding.ivStatusBig.setBackgroundResource(R.drawable.bg_status_belum_absen_small)
                binding.ivStatusBig.setImageResource(R.drawable.ic_remove)
                binding.ivStatusBig.setColorFilter(0xFFFFFFFF.toInt())
            }
        }

        binding.cardStatusDetail.setOnClickListener {
            showUbahStatusDialog()
        }
    }

    private fun showUbahStatusDialog() {
        if (SiswaHelper.isWeekend(tanggal)) {
            Toast.makeText(this, "Hari libur, tidak bisa ubah status", Toast.LENGTH_SHORT).show()
            return
        }

        val options = arrayOf("Hadir", "Telat", "Alpha")
        val values = arrayOf("hadir", "telat", "absen")

        AlertDialog.Builder(this)
            .setTitle("Ubah Status Absensi")
            .setItems(options) { _, which ->
                val newStatus = values[which]
                val label = options[which]

                val berhasil = db.updateStatusAbsensi(siswaId, tanggal, newStatus)

                if (berhasil) {
                    // ⭐ 1. Simpan notifikasi ke DB (untuk badge di beranda siswa)
                    val judul = "Status Absensi Diubah"
                    val pesan = "Status absensi kamu untuk tanggal $tanggal " +
                            "diubah menjadi ${label.uppercase()} oleh guru."

                    db.insertNotifikasi(
                        userId = siswaId,
                        judul = judul,
                        pesan = pesan
                    )

                    // ⭐ 2. Tampilkan notifikasi sistem (heads-up pop-up)
                    NotificationHelper.showNotification(
                        context = this,
                        userId = siswaId,
                        title = judul,
                        message = pesan,
                        notifId = siswaId
                    )

                    Toast.makeText(
                        this,
                        "Status diubah menjadi $label",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadStatusHariIni()
                    loadRiwayat()
                } else {
                    Toast.makeText(this, "Gagal ubah status", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadRiwayat() {
        val dbRiwayat = db.getRiwayatAbsen(siswaId)
        val fullRiwayat = RiwayatProvider.getAll(siswaId, dbRiwayat)
        adapter.updateData(fullRiwayat)
    }

    private fun setupBottomNav() {
        binding.btnHomeGuru.setOnClickListener { finish() }
        binding.btnSearchGuru.setOnClickListener {
            startActivity(Intent(this, SearchKelasActivity::class.java))
        }
        binding.btnProfileGuru.setOnClickListener {
            startActivity(Intent(this, ProfileGuruActivity::class.java))
        }
    }
}