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

        card.tvTanggal.text = SimpleDateFormat("dd", locale).format(now)
        card.tvHari.text = SimpleDateFormat("EEEE", locale).format(now)
        card.tvBulanTahun.text = SimpleDateFormat("MMMM/yyyy", locale).format(now)

        val circleIds = listOf(
            R.id.ivStatus1,
            R.id.ivStatus2,
            R.id.ivStatus3,
            R.id.ivStatus4,
            R.id.ivStatus5
        )

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        circleIds.forEachIndexed { index, id ->
            val view = card.root.findViewById<ImageView>(id)
            val tanggalHariIni = dateFmt.format(cal.time)

            val absensi = db.getAbsensiByTanggal(userId, tanggalHariIni)

            val bgRes = when (absensi?.status?.lowercase()) {
                "hadir" -> R.drawable.bg_status_hadir
                "telat" -> R.drawable.bg_status_telat
                "absen", "alpha" -> R.drawable.bg_icon_status_alpha
                else -> R.drawable.bg_status_kosong
            }

            view.setBackgroundResource(bgRes)
            cal.add(Calendar.DAY_OF_YEAR, 1)
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