package com.example.absensisiswaproto.ui.siswa

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivitySiswaMainBinding
import com.example.absensisiswaproto.ui.absen.AbsenFragment
import com.example.absensisiswaproto.ui.absen.ArcProgressView
import com.example.absensisiswaproto.ui.beranda.BerandaFragment
import com.example.absensisiswaproto.ui.kalender.KalenderFragment
import com.example.absensisiswaproto.ui.pelajaran.PelajaranFragment
import com.example.absensisiswaproto.ui.profile.ProfileFragment
import com.example.absensisiswaproto.ui.riwayat.RiwayatFragment

class SiswaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiswaMainBinding
    private lateinit var db: DatabaseHelper

    var userId: Int = 0
        private set
    var userNis: String = ""
        private set
    var userNama: String = ""
        private set
    var userRole: String = ""
        private set
    var userKelas: String = ""
        private set

    private var fingerprintTimer: CountDownTimer? = null
    private val holdDurationMs: Long = 1500L

    private var currentTab: String = TAB_HOME

    companion object {
        const val TAB_HOME = "tab_home"
        const val TAB_PROFILE = "tab_profile"
        const val TAB_RIWAYAT = "tab_riwayat"
        const val TAB_PELAJARAN = "tab_pelajaran"
        const val TAB_KALENDER = "tab_kalender"
        const val TAB_ABSEN = "tab_absen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySiswaMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        if (savedInstanceState == null) {
            showFragment(TAB_HOME, BerandaFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
        }

        setupBottomNav()
        setupOnBackPressed()
        updateFingerprintUiState()
    }

    override fun onResume() {
        super.onResume()
        updateFingerprintUiState()
    }

    private fun updateFingerprintUiState() {
        val sudahAbsen = db.sudahAbsenHariIni(userId)
        binding.bottomNav.btnFingerprint.apply {
            isEnabled = !sudahAbsen
            alpha = if (sudahAbsen) 0.5f else 1.0f
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnHome.setOnClickListener {
            if (currentTab != TAB_HOME) {
                showFragment(TAB_HOME, BerandaFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            }
        }

        binding.bottomNav.btnProfile.setOnClickListener {
            if (currentTab != TAB_PROFILE) {
                showFragment(TAB_PROFILE, ProfileFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            }
        }

        binding.bottomNav.btnFingerprint.setOnTouchListener { view, event ->
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
    }

    fun navigateToTab(tabName: String) {
        when (tabName) {
            TAB_HOME -> showFragment(TAB_HOME, BerandaFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            TAB_PROFILE -> showFragment(TAB_PROFILE, ProfileFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            TAB_RIWAYAT -> showFragment(TAB_RIWAYAT, RiwayatFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            TAB_PELAJARAN -> showFragment(TAB_PELAJARAN, PelajaranFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            TAB_KALENDER -> showFragment(TAB_KALENDER, KalenderFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
            TAB_ABSEN -> showFragment(TAB_ABSEN, AbsenFragment.newInstance(userId, userNis, userNama, userRole, userKelas))
        }
    }

    private fun showFragment(tag: String, fragment: Fragment) {
        currentTab = tag
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.siswaFragmentContainer, fragment, tag)
            .commit()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentTab != TAB_HOME) {
                    navigateToTab(TAB_HOME)
                } else {
                    finish()
                }
            }
        })
    }

    private fun canAbsen(): Boolean {
        if (db.isHariLibur()) {
            Toast.makeText(this, "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
            return false
        }
        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(this, "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
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

    private fun navProgressView(): ArcProgressView? {
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
            Toast.makeText(this, "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
            return
        }

        val berhasil = db.simpanAbsen(userId, "fingerprint")
        if (berhasil) {
            Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
            updateFingerprintUiState()
            val currentFrag = supportFragmentManager.findFragmentByTag(TAB_HOME)
            if (currentFrag is BerandaFragment) {
                currentFrag.refreshData()
            }
        } else {
            Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        fingerprintTimer?.cancel()
        fingerprintTimer = null
        super.onDestroy()
    }
}
