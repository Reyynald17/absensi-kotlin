package com.example.absensisiswaproto.ui.absen

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.FragmentAbsenBinding
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class AbsenFragment : Fragment() {

    private var _binding: FragmentAbsenBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var isAnimating = false
    private var pendingMetode: String? = null

    private val panelStartHeightDp = 27

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"
        private const val ARG_USER_NIS = "ARG_USER_NIS"
        private const val ARG_USER_NAMA = "ARG_USER_NAMA"
        private const val ARG_USER_ROLE = "ARG_USER_ROLE"
        private const val ARG_USER_KELAS = "ARG_USER_KELAS"

        fun newInstance(
            userId: Int,
            userNis: String,
            userNama: String,
            userRole: String,
            userKelas: String
        ): AbsenFragment {
            return AbsenFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                    putString(ARG_USER_NIS, userNis)
                    putString(ARG_USER_NAMA, userNama)
                    putString(ARG_USER_ROLE, userRole)
                    putString(ARG_USER_KELAS, userKelas)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID, 0)
            userNis = it.getString(ARG_USER_NIS, "")
            userNama = it.getString(ARG_USER_NAMA, "Siswa")
            userRole = it.getString(ARG_USER_ROLE, "siswa")
            userKelas = it.getString(ARG_USER_KELAS, "-")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbsenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

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

        binding.overlayAnimasi.postDelayed({
            playIntroAnimation()
        }, 400)
    }

    private fun setupBlurOverlay() {
        val blurView: BlurView = binding.overlayAnimasi
        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupBlurQr() {
        val blurView: BlurView = binding.blurQr
        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            if (!isAnimating) {
                (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_HOME)
            }
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    private fun setupButtons() {
        binding.btnFaceId.setOnClickListener {
            if (db.isHariLibur()) {
                Toast.makeText(requireContext(), "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(requireContext(), "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), ScanAbsenActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
                putExtra("EXTRA_MODE", "faceid")
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
                handleAbsen()
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

    private fun handleAbsen() {
        if (isAnimating) return

        if (db.isHariLibur()) {
            Toast.makeText(requireContext(), "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
            return
        }

        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(requireContext(), "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
            return
        }

        pendingMetode = "qrcode"
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

        if (db.sudahAbsenHariIni(userId)) {
            Toast.makeText(requireContext(), "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
            return
        }

        val berhasil = db.simpanAbsen(userId, metode)
        if (berhasil) {
            Toast.makeText(requireContext(), "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_HOME)
        } else {
            Toast.makeText(requireContext(), "Absen gagal", Toast.LENGTH_SHORT).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
