package com.example.absensisiswaproto.ui.absen

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityAbsenBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class AbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsenBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var isAnimating = false
    private var pendingMetode: String? = null

    private val panelStartHeightDp = 27

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        binding.overlayAnimasi.visibility = View.GONE
        binding.overlayQr.visibility = View.GONE
        binding.infoPanel.layoutParams.height = dpToPx(panelStartHeightDp)

        setupBlurOverlay()
        setupBlurQr()
        setupHeader()
        setupButtons()
        setupOutsideTap()
        setupPanelClose()
        setupQrOverlay()
        setupBottomNav()

        binding.overlayAnimasi.postDelayed({
            playIntroAnimation()
        }, 400)
    }

    private fun setupBlurOverlay() {
        val blurView: BlurView = binding.overlayAnimasi
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupBlurQr() {
        val blurView: BlurView = binding.blurQr
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            if (!isAnimating) finish()
        }
    }

    private fun setupButtons() {
        binding.btnFaceId.setOnClickListener {
            if (db.isHariLibur()) {
                Toast.makeText(this, "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ScanAbsenActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }
        binding.btnQrAbsen.setOnClickListener {
            showQrOverlay()
        }
    }

    private fun setupOutsideTap() {
        binding.dimBackground.setOnClickListener {
            handleCloseRequest()
        }
    }

    private fun setupPanelClose() {
        binding.btnCloseInfo.setOnClickListener {
            handleCloseRequest()
        }
        binding.btnMengerti.setOnClickListener {
            handleCloseRequest()
        }
    }

    private fun setupQrOverlay() {
        binding.btnCloseQr.setOnClickListener {
            hideQrOverlay()
        }
        binding.btnKonfirmasiAbsen.setOnClickListener {
            hideQrOverlay()
            binding.root.postDelayed({
                handleAbsen("qrcode")
            }, 250)
        }
    }

    private fun showQrOverlay() {
        binding.ivQrCard.setImageResource(getQrDrawable(userId))
        binding.tvQrNama.text = userNama
        binding.tvQrKelas.text = userKelas

        binding.overlayQr.visibility = View.VISIBLE
        binding.overlayQr.alpha = 0f
        binding.overlayQr.animate()
            .alpha(1f)
            .setDuration(250)
            .start()
    }

    private fun hideQrOverlay() {
        binding.overlayQr.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.overlayQr.visibility = View.GONE
            }
            .start()
    }

    private fun getQrDrawable(id: Int): Int {
        return when (id) {
            1 -> R.drawable.qr_jacobiambasukixiirpl3
            2 -> R.drawable.qr_aratanaokoxiifm2
            else -> R.drawable.ic_qr_code
        }
    }

    private fun handleCloseRequest() {
        if (isAnimating) return
        binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        if (pendingMetode == null) {
            closePanel()
        } else {
            closePanelAndSave()
        }
    }

    private fun handleAbsen(metode: String) {
        if (isAnimating) return

        if (db.isHariLibur()) {
            Toast.makeText(this, "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
            return
        }

        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            return
        }

        pendingMetode = metode
        playIntroAnimation()
    }

    private fun playIntroAnimation() {
        if (isAnimating) return
        isAnimating = true

        binding.overlayAnimasi.visibility = View.VISIBLE
        binding.overlayAnimasi.alpha = 0f

        binding.infoPanel.layoutParams.height = dpToPx(panelStartHeightDp)
        binding.infoPanel.requestLayout()

        binding.contentInside.alpha = 0f

        binding.overlayAnimasi.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        binding.infoPanel.postDelayed({
            expandPanel()
        }, 200)
    }

    private fun expandPanel() {
        val startHeight = dpToPx(panelStartHeightDp)
        val endHeight = getPanelMaxHeightPx()

        val animator = ValueAnimator.ofInt(startHeight, endHeight).apply {
            duration = 700
            interpolator = DecelerateInterpolator()

            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                val params = binding.infoPanel.layoutParams
                params.height = value
                binding.infoPanel.layoutParams = params
            }

            doOnEnd {
                binding.contentInside.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .start()

                isAnimating = false
            }
        }

        animator.start()
    }

    private fun closePanel() {
        if (isAnimating) return
        isAnimating = true

        binding.contentInside.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        val startHeight = binding.infoPanel.height
        val endHeight = dpToPx(panelStartHeightDp)

        binding.infoPanel.postDelayed({
            val animator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                duration = 500
                interpolator = AccelerateInterpolator()

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    val params = binding.infoPanel.layoutParams
                    params.height = value
                    binding.infoPanel.layoutParams = params
                }

                doOnEnd {
                    binding.overlayAnimasi.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.overlayAnimasi.visibility = View.GONE
                            isAnimating = false
                        }
                        .start()
                }
            }

            animator.start()
        }, 200)
    }

    private fun closePanelAndSave() {
        if (isAnimating) return
        isAnimating = true

        binding.contentInside.animate()
            .alpha(0f)
            .setDuration(200)
            .start()

        val startHeight = binding.infoPanel.height
        val endHeight = dpToPx(panelStartHeightDp)

        binding.infoPanel.postDelayed({
            val animator = ValueAnimator.ofInt(startHeight, endHeight).apply {
                duration = 500
                interpolator = AccelerateInterpolator()

                addUpdateListener { animation ->
                    val value = animation.animatedValue as Int
                    val params = binding.infoPanel.layoutParams
                    params.height = value
                    binding.infoPanel.layoutParams = params
                }

                doOnEnd {
                    binding.overlayAnimasi.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.overlayAnimasi.visibility = View.GONE
                            isAnimating = false
                            simpanAbsenDanKeluar()
                        }
                        .start()
                }
            }

            animator.start()
        }, 200)
    }

    private fun simpanAbsenDanKeluar() {
        val metode = pendingMetode ?: return
        pendingMetode = null

        val berhasil = db.simpanAbsen(userId, metode)
        if (berhasil) {
            Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPanelMaxHeightPx(): Int {
        val overlayHeight = binding.overlayAnimasi.height
        if (overlayHeight > 0) {
            return overlayHeight - dpToPx(16)
        }
        val screenHeight = resources.displayMetrics.heightPixels
        return (screenHeight * 0.85).toInt()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            if (isAnimating) return@setOnClickListener
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
            handleAbsen("fingerprint")
        }

        nav.btnProfile.setOnClickListener {
            if (isAnimating) return@setOnClickListener
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
