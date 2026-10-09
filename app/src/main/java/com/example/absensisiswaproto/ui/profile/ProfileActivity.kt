package com.example.absensisiswaproto.ui.profile

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityProfileBinding
import com.example.absensisiswaproto.ui.login.LoginActivity
import com.example.absensisiswaproto.ui.riwayat.RiwayatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: "1234567890"
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Ananda Prayuda"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "XII DKV 2"

        setupProfileData()
        setupStatusKehadiran()
        setupRekapKehadiran()
        setupActions()
        setupOverlayPelajaran()
        setupBottomNav()
    }

    private fun setupProfileData() {
        binding.tvProfileName.text = userNama
        binding.tvProfileSchool.text = "SMK Negeri 9 Medan"
        binding.tvProfileNis.text = "NISN:$userNis"
        binding.tvProfileRole.text = "Kelas: $userKelas"
    }

    private fun setupStatusKehadiran() {
        val now = Date()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvStatusTanggal.text = dateFormat.format(now)
        binding.tvStatusJam.text = timeFormat.format(now)

        if (db.sudahAbsenHariIni(userId)) {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_hadir_pill)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_check)
            binding.tvStatusValue.text = "Hadir"
            binding.tvStatusDesc.text = "Terimakasih sudah disiplin"
        } else {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_alpha_pill)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_remove)
            binding.tvStatusValue.text = "Belum Absen"
            binding.tvStatusDesc.text = "Belum melakukan absensi hari ini"
        }
    }

    private fun setupRekapKehadiran() {
        val riwayat = db.getRiwayatAbsen(userId)
        val totalHadir = riwayat.count { it.status.equals("hadir", ignoreCase = true) }
        val totalAbsen = riwayat.count { it.status.equals("absen", ignoreCase = true) || it.status.equals("alpha", ignoreCase = true) }
        val totalTelat = riwayat.count { it.status.equals("telat", ignoreCase = true) }

        binding.tvTotalHadir.text = if (totalHadir > 0) "$totalHadir hari" else "26 hari"
        binding.tvTotalAbsen.text = if (totalAbsen > 0) "$totalAbsen hari" else "2 hari"
        binding.tvTotalTelat.text = if (totalTelat > 0) "$totalTelat hari" else "12 hari"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupActions() {
        setupTouchEffect(binding.btnPelajaran) {
            binding.overlayPelajaran.overlayRoot.visibility = View.VISIBLE
            binding.overlayPelajaran.overlayRoot.bringToFront()
        }

        setupTouchEffect(binding.btnRiwayat) {
            val intent = Intent(this, RiwayatActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOverlayPelajaran() {
        binding.overlayPelajaran.btnTutup.setOnClickListener {
            binding.overlayPelajaran.overlayRoot.visibility = View.GONE
        }

        binding.overlayPelajaran.blok1.setOnClickListener {
            Toast.makeText(this, "Blok 1 ditekan", Toast.LENGTH_SHORT).show()
        }

        binding.overlayPelajaran.blok2.setOnClickListener {
            Toast.makeText(this, "Blok 2 ditekan", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchEffect(view: View, onClick: () -> Unit) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.95f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.95f).setDuration(100).start()
                    false
                }
                MotionEvent.ACTION_UP -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 0.95f, 1f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 0.95f, 1f).setDuration(100).start()
                    onClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 0.95f, 1f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 0.95f, 1f).setDuration(100).start()
                    false
                }
                else -> false
            }
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            finish()
        }

        nav.btnFingerprint.setOnClickListener {
            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            } else {
                val berhasil = db.simpanAbsen(userId, "fingerprint")
                if (berhasil) {
                    Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                    setupStatusKehadiran()
                    setupRekapKehadiran()
                } else {
                    Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        nav.btnProfile.setOnClickListener {
            Toast.makeText(this, "Sudah di Profile", Toast.LENGTH_SHORT).show()
        }
    }
}