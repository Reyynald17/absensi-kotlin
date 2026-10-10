package com.example.absensisiswaproto.ui.notifikasi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.Notif
import com.example.absensisiswaproto.databinding.ActivityNotificationBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        setupHeader()
        setupRecyclerView()
        setupBottomNav()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val list = getDummyNotif()
        val adapter = NotificationAdapter(list)
        binding.rvNotif.layoutManager = LinearLayoutManager(this)
        binding.rvNotif.adapter = adapter
    }

    private fun getDummyNotif(): List<Notif> {
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()

        return listOf(
            Notif(
                iconRes = R.drawable.ic_remove,
                bgRes = R.drawable.bg_notif_circle_red,
                iconTint = white,
                title = "Tidak Hadir Lagi?",
                subtitle = "hari kedua tidak hadir"
            ),
            Notif(
                iconRes = R.drawable.ic_fingerprint,
                bgRes = R.drawable.bg_notif_circle_green,
                iconTint = white,
                title = "Anda Sudah Absen",
                subtitle = "anda hadir tepat waktu"
            ),
            Notif(
                iconRes = R.drawable.ic_fingerprint,
                bgRes = R.drawable.bg_notif_circle_green,
                iconTint = white,
                title = "Jangan Lupa Melakukan Absen",
                subtitle = "lakukan absen sebelum terlambat"
            ),
            Notif(
                iconRes = R.drawable.ic_remove,
                bgRes = R.drawable.bg_notif_circle_white,
                iconTint = black,
                title = "Tidak Hadir Hari Ini?",
                subtitle = "kabari wali anda"
            ),
            Notif(
                iconRes = R.drawable.ic_kalendar,
                bgRes = R.drawable.bg_notif_square_green,
                iconTint = white,
                title = "Saatnya Upacara",
                subtitle = "lakukan absen sebelum terlambat"
            ),
            Notif(
                iconRes = R.drawable.ic_fingerprint,
                bgRes = R.drawable.bg_notif_circle_green,
                iconTint = white,
                title = "Anda Sudah Absen",
                subtitle = "anda hadir tepat waktu"
            ),
            Notif(
                iconRes = R.drawable.ic_fingerprint,
                bgRes = R.drawable.bg_notif_circle_green,
                iconTint = white,
                title = "Jangan Lupa Melakukan Absen",
                subtitle = "lakukan absen sebelum terlambat"
            ),
            Notif(
                iconRes = R.drawable.ic_kalendar,
                bgRes = R.drawable.bg_notif_square_white,
                iconTint = black,
                title = "Ada perubahan tanggal",
                subtitle = "admin menambahkan jadwal baru"
            ),
            Notif(
                iconRes = R.drawable.ic_book,
                bgRes = R.drawable.bg_notif_square_white,
                iconTint = black,
                title = "Ada Perubahan Pelajaran",
                subtitle = "mapel anda di rubah"
            ),
            Notif(
                iconRes = R.drawable.ic_fingerprint,
                bgRes = R.drawable.bg_notif_circle_green,
                iconTint = white,
                title = "Berhasil Absen",
                subtitle = "Tetap datang walau terlambat"
            )
        )
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
            Toast.makeText(this, "Absen via fingerprint", Toast.LENGTH_SHORT).show()
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
