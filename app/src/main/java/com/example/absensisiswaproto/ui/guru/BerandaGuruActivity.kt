package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityBerandaGuruBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BerandaGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaGuruBinding
    private lateinit var db: DatabaseHelper

    private var guruId: Int = 0
    private var guruNip: String = ""
    private var guruNama: String = ""
    private var guruKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        guruId = intent.getIntExtra("EXTRA_GURU_ID", 0)
        guruNip = intent.getStringExtra("EXTRA_GURU_NIP") ?: ""
        guruNama = intent.getStringExtra("EXTRA_GURU_NAMA") ?: "Guru"
        guruKelas = intent.getStringExtra("EXTRA_GURU_KELAS") ?: "-"

        if (guruId == 0) {
            guruId = GuruSession.getGuruId(this)
            if (guruId <= 0) {
                startActivity(Intent(this, LoginGuruActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
                return
            } else {
                guruNip = GuruSession.getNip(this)
                guruNama = GuruSession.getNama(this)
            }
        } else {
            GuruSession.save(this, guruId, guruNip, guruNama)
        }

        setupGreeting()
        setupTanggal()
        setupMenuCards()
        setupBottomNav()
    }

    private fun setupGreeting() {
        val firstName = guruNama.split(" ").firstOrNull() ?: guruNama
        val gelar = if (guruNama.contains("Bu", ignoreCase = true) ||
            guruNama.startsWith("Ibu", ignoreCase = true)
        ) "Bu" else "Pak"
        binding.tvGreetingGuru.text = "Halo, $gelar $firstName"

        binding.ivAvatarGuru.setImageResource(SiswaHelper.getFotoGuru(guruNama, guruId))
    }

    private fun setupTanggal() {
        val now = Date()
        val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        binding.tvTanggalGuru.text = format.format(now)
    }

    private fun setupMenuCards() {
        PressEffect.attach(binding.btnCardTanggal)
        PressEffect.attach(binding.btnAbsensiSiswa)
        PressEffect.attach(binding.btnRekapKelas)
        PressEffect.attach(binding.btnNotificationGuru)

        binding.btnCardTanggal.setOnClickListener {
            Toast.makeText(this, "Pilih tanggal", Toast.LENGTH_SHORT).show()
        }

        binding.btnAbsensiSiswa.setOnClickListener {
            val intent = Intent(this, AbsensiSiswaActivity::class.java).apply {
                putExtra("EXTRA_GURU_NAMA", guruNama)
                putExtra("EXTRA_GURU_KELAS", guruKelas)
            }
            startActivity(intent)
        }

        binding.btnRekapKelas.setOnClickListener {
            startActivity(Intent(this, RekapKelasActivity::class.java))
        }

        binding.btnNotificationGuru.setOnClickListener {
            startActivity(Intent(this, NotificationGuruActivity::class.java))
        }
    }

    private fun setupBottomNav() {
        PressEffect.attach(binding.btnHomeGuru, 0.88f)
        PressEffect.attach(binding.btnSearchGuru, 0.88f)
        PressEffect.attach(binding.btnProfileGuru, 0.88f)

        binding.btnHomeGuru.setOnClickListener {
            Toast.makeText(this, "Sudah di Beranda", Toast.LENGTH_SHORT).show()
        }

        binding.btnSearchGuru.setOnClickListener {
            startActivity(Intent(this, SearchKelasActivity::class.java))
        }

        binding.btnProfileGuru.setOnClickListener {
            startActivity(Intent(this, ProfileGuruActivity::class.java))
        }
    }
}