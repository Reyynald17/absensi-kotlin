package com.example.absensisiswaproto.ui.profile

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivityProfileBinding
import com.example.absensisiswaproto.ui.login.LoginActivity
import com.example.absensisiswaproto.ui.riwayat.RiwayatActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
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
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: "1234567890"
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Ananda Prayuda"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "XII DKV 2"

        binding.overlayPelajaran.root.visibility = View.GONE
        binding.overlayJadwal.root.visibility = View.GONE

        setupBlurEffect()
        setupProfileData()
        setupStatusKehadiran()
        setupRekapKehadiran()
        setupActions()
        setupOverlayPelajaran()
        setupOverlayJadwal()
        setupBottomNav()
    }

    private fun setupBlurEffect() {
        val blurView: BlurView = binding.overlayJadwal.blurView
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupProfileData() {
        binding.tvProfileName.text = userNama
        binding.tvProfileSchool.text = "SMK Negeri 9 Medan"
        binding.tvProfileNis.text = "NISN:$userNis"
        binding.tvProfileRole.text = "Kelas: $userKelas"

        binding.ivProfilePhoto.setImageResource(getProfilePhoto(userId))
    }

    private fun getProfilePhoto(id: Int): Int {
        return when (id) {
            1 -> R.drawable.ppjacobiambasukixiirpl3
            2 -> R.drawable.pparatanaokoxiifm2
            else -> R.drawable.siswa_kanan
        }
    }

    private fun setupStatusKehadiran() {
        val now = Date()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvStatusTanggal.text = dateFormat.format(now)
        binding.tvStatusJam.text = timeFormat.format(now)

        if (db.isHariLibur()) {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_notif_circle_white)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_kalendar)
            binding.ivStatusHariIni.setColorFilter(0xFF123B5D.toInt())
            binding.tvStatusValue.text = "Libur"
            binding.tvStatusDesc.text = "Hari ini tidak ada absensi"
            return
        }

        val absenHariIni = db.getAbsenHariIni(userId)

        if (absenHariIni == null) {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_notif_circle_white)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_remove)
            binding.ivStatusHariIni.setColorFilter(0xFF123B5D.toInt())
            binding.tvStatusValue.text = "Belum Absen"
            binding.tvStatusDesc.text = "Segera lakukan absensi hari ini"
            return
        }

        when (absenHariIni.status.lowercase()) {
            "hadir" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_check)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Hadir"
                binding.tvStatusDesc.text = "Terima kasih sudah disiplin"
            }
            "telat" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_telat_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_warning)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Telat"
                binding.tvStatusDesc.text = "Datang lebih awal besok ya"
            }
            "absen", "alpha" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_alpha_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_remove)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Absen"
                binding.tvStatusDesc.text = "Kamu tidak hadir hari ini"
            }
            else -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_check)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Hadir"
                binding.tvStatusDesc.text = "Terima kasih sudah disiplin"
            }
        }
    }

    private fun setupRekapKehadiran() {
        val dataDb = db.getRiwayatAbsen(userId)
        val (hadir, telat, absen) = RiwayatProvider.rekap(userId, dataDb)

        binding.tvTotalHadir.text = "$hadir hari"
        binding.tvTotalAbsen.text = "$absen hari"
        binding.tvTotalTelat.text = "$telat hari"
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupActions() {
        setupTouchEffect(binding.btnPelajaran) {
            binding.overlayPelajaran.root.visibility = View.VISIBLE
            binding.overlayPelajaran.root.bringToFront()
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

    private fun setupOverlayPelajaran() {
        binding.overlayPelajaran.btnTutup.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
        }

        binding.overlayPelajaran.blok1.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
            binding.overlayJadwal.root.visibility = View.VISIBLE
            binding.overlayJadwal.root.bringToFront()
        }

        binding.overlayPelajaran.blok2.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
            binding.overlayJadwal.root.visibility = View.VISIBLE
            binding.overlayJadwal.root.bringToFront()
        }
    }

    private fun setupOverlayJadwal() {
        binding.overlayJadwal.btnTutupJadwal.setOnClickListener {
            binding.overlayJadwal.root.visibility = View.GONE
            binding.overlayPelajaran.root.visibility = View.VISIBLE
            binding.overlayPelajaran.root.bringToFront()
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
            if (db.isHariLibur()) {
                Toast.makeText(
                    this,
                    "Absen hanya bisa dilakukan Senin - Jumat",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
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
