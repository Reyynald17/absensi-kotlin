package com.example.absensisiswaproto.ui.pelajaran

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.Pelajaran
import com.example.absensisiswaproto.databinding.ActivityPelajaranBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity

class PelajaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPelajaranBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPelajaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        binding.tvTitleInfoKelas.text = "Jadwal Pelajaran - $userKelas"

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupListPelajaran()
        setupBottomNav()
    }

    private fun setupListPelajaran() {
        val listPelajaran = listOf(
            Pelajaran("Pemrograman Web & Mobile", "Budi Santoso, S.Kom", "Lab Komputer 1", "07:30 - 09:30"),
            Pelajaran("Matematika Terapan", "Dra. Siti Aminah", "Ruang 12", "09:45 - 11:15"),
            Pelajaran("Bahasa Indonesia", "Drs. Ahmad Dahlan", "Ruang 12", "11:15 - 12:30"),
            Pelajaran("Basis Data & Cloud", "Rizky Pratama, M.T.", "Lab Komputer 2", "13:00 - 15:00")
        )

        binding.rvPelajaran.layoutManager = LinearLayoutManager(this)
        binding.rvPelajaran.adapter = PelajaranAdapter(listPelajaran)
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
