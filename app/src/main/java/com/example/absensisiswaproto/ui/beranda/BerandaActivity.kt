package com.example.absensisiswaproto.ui.beranda

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityBerandaBinding
import com.example.absensisiswaproto.ui.absen.AbsenActivity
import com.example.absensisiswaproto.ui.kalender.KalenderActivity
import com.example.absensisiswaproto.ui.notifikasi.NotificationActivity
import com.example.absensisiswaproto.ui.pelajaran.PelajaranActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import com.example.absensisiswaproto.ui.riwayat.RiwayatActivity
import com.example.absensisiswaproto.util.NotificationHelper
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

    private var fingerprintTimer: CountDownTimer? = null
    private val holdDurationMs: Long = 1500L

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

        NotificationHelper.createChannel(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

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

        circleIds.forEachIndexed { _, id ->
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

        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggalHariIni = dateFmt.format(now)

        val absensiHariIni = db.getAbsensiByTanggal(userId, tanggalHariIni)

        val bgRes = when (absensiHariIni?.status?.lowercase()) {
            "hadir" -> R.drawable.bg_status_hadir
            "telat" -> R.drawable.bg_status_telat
            "alpha", "absen" -> R.drawable.bg_status_alpha_small
            else -> R.drawable.bg_status_kosong
        }

        absen.ivStatusAbsen.setBackgroundResource(bgRes)
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

        nav.btnFingerprint.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!canAbsen()) return@setOnTouchListener true
                    startFingerprintHold(view)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    cancelFingerprintHold(view)
                    true
                }
                else -> false
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

    private fun canAbsen(): Boolean {
        if (db.isHariLibur()) {
            Toast.makeText(this, "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
            return false
        }
        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun startFingerprintHold(view: View) {
        fingerprintTimer?.cancel()

        try {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        } catch (_: Exception) {}

        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(150)
            .start()

        val progress = navProgressView()
        try {
            progress?.visibility = View.VISIBLE
            progress?.setProgress(0f)
        } catch (_: Exception) {}

        fingerprintTimer = object : CountDownTimer(holdDurationMs, 30L) {
            override fun onTick(msUntilFinished: Long) {
                val p = 1f - (msUntilFinished.toFloat() / holdDurationMs)
                try {
                    progress?.setProgress(p)
                } catch (_: Exception) {}
                view.alpha = 0.6f + (p * 0.4f)
            }

            override fun onFinish() {
                view.scaleX = 1f
                view.scaleY = 1f
                view.alpha = 1f
                try {
                    progress?.visibility = View.GONE
                } catch (_: Exception) {}

                safeVibrate(120)
                onFingerprintComplete()
            }
        }.start()
    }

    private fun cancelFingerprintHold(view: View) {
        fingerprintTimer?.cancel()
        fingerprintTimer = null

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(150)
            .start()

        try {
            navProgressView()?.apply {
                setProgress(0f)
                visibility = View.GONE
            }
        } catch (_: Exception) {}
    }

    private fun safeVibrate(durationMs: Long) {
        try {
            val vibrator = getSystemService(Vibrator::class.java) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun navProgressView(): com.example.absensisiswaproto.ui.absen.ArcProgressView? {
        return try {
            binding.bottomNav.root.findViewById(R.id.arcFingerprintProgress)
        } catch (e: Exception) { null }
    }

    private fun onFingerprintComplete() {
        if (db.isHariLibur()) {
            Toast.makeText(this, "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
            return
        }
        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            return
        }

        val berhasil = db.simpanAbsen(userId, "fingerprint")
        if (berhasil) {
            Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
            setupAbsenHariIni()
            setupCardKehadiran()
        } else {
            Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshNotifBadge() {
        val count = db.countNotifikasiBelumDibaca(userId)
        val badge = binding.headerLayout.tvNotifBadge

        if (count > 0) {
            badge.visibility = View.VISIBLE
            badge.text = if (count > 9) "9+" else count.toString()
        } else {
            badge.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        refreshNotifBadge()
        setupAbsenHariIni()
        setupCardKehadiran()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        refreshNotifBadge()
    }

    override fun onDestroy() {
        fingerprintTimer?.cancel()
        fingerprintTimer = null
        super.onDestroy()
    }
}