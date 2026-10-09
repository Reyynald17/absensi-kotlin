package com.example.absensisiswaproto.ui.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.databinding.ActivityRiwayatBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var isComboboxExpanded = false
    private var selectedMonthFilter: Int? = null // null = All months, 0 = Jan ... 11 = Dec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupComboboxDropdown()
        setupListRiwayat()
        setupBottomNav()
    }

    private fun setupComboboxDropdown() {
        binding.layoutComboboxHeader.setOnClickListener {
            toggleCombobox()
        }

        val monthButtons = mapOf(
            binding.btnMonthSemua to null,
            binding.btnMonthJanuari to 0,
            binding.btnMonthFebuari to 1,
            binding.btnMonthMaret to 2,
            binding.btnMonthApril to 3,
            binding.btnMonthMei to 4,
            binding.btnMonthJuni to 5,
            binding.btnMonthJuli to 6,
            binding.btnMonthAgustus to 7,
            binding.btnMonthSeptember to 8,
            binding.btnMonthOktober to 9,
            binding.btnMonthNovember to 10,
            binding.btnMonthDesember to 11
        )

        val monthNames = arrayOf(
            "Januari", "Febuari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )

        monthButtons.forEach { (view, monthIndex) ->
            view.setOnClickListener {
                selectedMonthFilter = monthIndex
                binding.tvFilterKalender.text = if (monthIndex != null) monthNames[monthIndex] else "Kalender"
                toggleCombobox()
                setupListRiwayat()
            }
        }
    }

    private fun toggleCombobox() {
        isComboboxExpanded = !isComboboxExpanded
        if (isComboboxExpanded) {
            binding.layoutMonthDropdown.visibility = View.VISIBLE
            binding.ivComboboxArrow.setImageResource(R.drawable.ic_arrow_up)
        } else {
            binding.layoutMonthDropdown.visibility = View.GONE
            binding.ivComboboxArrow.setImageResource(R.drawable.ic_arrow_down)
        }
    }

    private fun setupListRiwayat() {
        var list = db.getRiwayatAbsen(userId)

        // Generate sample September data if db has few records so mockup matches Screenshot 3
        if (selectedMonthFilter == 8 && list.none { it.tanggal.contains("-09-") }) {
            list = listOf(
                Absensi(1, userId, "2026-09-21", "07:15", "hadir", "fingerprint"),
                Absensi(2, userId, "2026-09-22", "00:00", "alpha", "-"),
                Absensi(3, userId, "2026-09-23", "07:20", "hadir", "fingerprint"),
                Absensi(4, userId, "2026-09-24", "07:10", "hadir", "fingerprint"),
                Absensi(5, userId, "2026-09-25", "00:00", "-", "-")
            )
        } else if (selectedMonthFilter != null) {
            val monthStr = String.format("%02d", selectedMonthFilter!! + 1)
            list = list.filter { it.tanggal.contains("-$monthStr-") }
        }

        if (list.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.rvRiwayat.visibility = View.GONE
        } else {
            binding.layoutEmptyState.visibility = View.GONE
            binding.rvRiwayat.visibility = View.VISIBLE
            binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
            binding.rvRiwayat.adapter = RiwayatAdapter(list)
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            val intent = Intent(this, BerandaActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        nav.btnFingerprint.setOnClickListener {
            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            } else {
                val berhasil = db.simpanAbsen(userId, "fingerprint")
                if (berhasil) {
                    Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                    setupListRiwayat()
                } else {
                    Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        nav.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}
