package com.example.absensisiswaproto.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityBerandaBinding
import com.example.absensisiswaproto.ui.absen.AbsenActivity
import com.example.absensisiswaproto.ui.kalender.KalenderActivity
import com.example.absensisiswaproto.ui.notifikasi.NotificationActivity
import com.example.absensisiswaproto.ui.pelajaran.PelajaranActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import com.example.absensisiswaproto.ui.riwayat.RiwayatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BerandaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBerandaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        setupHeader()
        setupCardKehadiran()
        setupAbsenHariIni()
        setupMenuCards()
        setupBottomNav()
    }

    private fun setupHeader() {
        binding.headerLayout.btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }
    }

    private fun setupCardKehadiran() {
        val card = binding.cardKehadiran

        val now = Date()
        val locale = Locale("id", "ID")

        val tanggalFormat = SimpleDateFormat("dd", locale)
        val hariFormat = SimpleDateFormat("EEEE", locale)
        val bulanTahunFormat = SimpleDateFormat("MMMM/yyyy", locale)

        card.tvTanggal.text = tanggalFormat.format(now)
        card.tvHari.text = hariFormat.format(now)
        card.tvBulanTahun.text = bulanTahunFormat.format(now)

        val circleIds = listOf(
            R.id.ivStatus1,
            R.id.ivStatus2,
            R.id.ivStatus3,
            R.id.ivStatus4,
            R.id.ivStatus5
        )

        val todayIndex = getTodayIndex()

        circleIds.forEachIndexed { index, id ->
            val view = card.root.findViewById<ImageView>(id)
            if (index == todayIndex) {
                view.setBackgroundResource(R.drawable.bg_status_hadir)
            } else {
                view.setBackgroundResource(R.drawable.bg_status_kosong)
            }
        }
    }

    private fun getTodayIndex(): Int {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            else -> -1
        }
    }

    private fun setupAbsenHariIni() {
        val absen = binding.absenHariIni

        val now = Date()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        absen.tvTanggalAbsen.text = format.format(now)

        if (db.sudahAbsenHariIni(userId)) {
            absen.ivStatusAbsen.setBackgroundResource(R.drawable.bg_status_hadir)
        } else {
            absen.ivStatusAbsen.setBackgroundResource(R.drawable.bg_status_kosong)
        }
    }

    private fun setupMenuCards() {
        val grid = binding.menuGrid

        grid.menuRiwayat.ivMenuIcon.setImageResource(R.drawable.ic_history)
        grid.menuRiwayat.tvMenuLabel.text = "Riwayat Hadir"
        grid.menuRiwayat.root.setOnClickListener {
            val intent = Intent(this, RiwayatActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }

        grid.menuPelajaran.ivMenuIcon.setImageResource(R.drawable.ic_book)
        grid.menuPelajaran.tvMenuLabel.text = "Pelajaran"
        grid.menuPelajaran.root.setOnClickListener {
            val intent = Intent(this, PelajaranActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
            }
            startActivity(intent)
        }

        grid.menuAbsen.ivMenuIcon.setImageResource(R.drawable.ic_absen)
        grid.menuAbsen.tvMenuLabel.text = "Absen"
        grid.menuAbsen.root.setOnClickListener {
            val intent = Intent(this, AbsenActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }

        grid.menuKalender.ivMenuIcon.setImageResource(R.drawable.ic_kalendar)
        grid.menuKalender.tvMenuLabel.text = "Kalender"
        grid.menuKalender.root.setOnClickListener {
            val intent = Intent(this, KalenderActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            Toast.makeText(this, "Sudah di Beranda", Toast.LENGTH_SHORT).show()
        }

        nav.btnFingerprint.setOnClickListener {
            if (db.isHariLibur()) {
                Toast.makeText(
                    this,
                    "Absen hanya bisa dilakukan Senin - Jumat",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            } else {
                val berhasil = db.simpanAbsen(userId, "fingerprint")
                if (berhasil) {
                    Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                    setupAbsenHariIni()
                    setupCardKehadiran()
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
            }
            startActivity(intent)
        }
    }
}
