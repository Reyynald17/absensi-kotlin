package com.example.absensisiswaproto.ui.notifikasi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.Notif
import com.example.absensisiswaproto.databinding.ActivityNotificationBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

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
        val list = getNotifikasiFromDb()
        val adapter = NotificationAdapter(list)
        binding.rvNotif.layoutManager = LinearLayoutManager(this)
        binding.rvNotif.adapter = adapter

        if (list.isNotEmpty()) {
            db.markNotifikasiDibaca(userId)
        }
    }


    private fun getNotifikasiFromDb(): List<Notif> {
        val white = 0xFFFFFFFF.toInt()
        val black = 0xFF000000.toInt()

        val notifItems = db.getNotifikasiByUser(userId)


        if (notifItems.isEmpty()) {
            return emptyList()
        }

        return notifItems.map { item ->
            val (iconRes, bgRes, iconTint) = when {
                item.judul.contains("Absensi", ignoreCase = true) -> Triple(
                    R.drawable.ic_remove,
                    R.drawable.bg_notif_circle_red,
                    white
                )
                item.judul.contains("Event", ignoreCase = true) -> Triple(
                    R.drawable.ic_kalendar,
                    R.drawable.bg_notif_square_green,
                    white
                )
                item.judul.contains("Hadir", ignoreCase = true) ||
                        item.judul.contains("Absen", ignoreCase = true) -> Triple(
                    R.drawable.ic_fingerprint,
                    R.drawable.bg_notif_circle_green,
                    white
                )
                else -> Triple(
                    R.drawable.ic_fingerprint,
                    R.drawable.bg_notif_circle_white,
                    black
                )
            }

            Notif(
                iconRes = iconRes,
                bgRes = bgRes,
                iconTint = iconTint,
                title = item.judul,
                subtitle = item.pesan
            )
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