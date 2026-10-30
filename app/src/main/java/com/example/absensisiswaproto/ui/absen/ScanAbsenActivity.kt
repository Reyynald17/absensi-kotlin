package com.example.absensisiswaproto.ui.absen

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityScanAbsenBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@ExperimentalGetImage
class ScanAbsenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanAbsenBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var isFaceMode = true
    private var isScanning = false
    private var faceScanningActive = false

    private var cameraProvider: ProcessCameraProvider? = null
    private var faceDetector: FaceDetector? = null

    private val clockHandler = Handler(Looper.getMainLooper())
    private val clockRunnable = object : Runnable {
        override fun run() {
            binding.tvClock.text = SimpleDateFormat("HH.mm", Locale.getDefault()).format(Date())
            clockHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScanAbsenBinding.inflate(layoutInflater)
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

        setupHeader()
        setupToggle()
        setupMulaiScanButton()
        setupBottomNav()

        val initialMode = intent.getStringExtra("EXTRA_MODE") ?: "faceid"
        switchMode(initialMode.equals("faceid", ignoreCase = true))
    }

    override fun onResume() {
        super.onResume()
        clockHandler.post(clockRunnable)
    }

    override fun onPause() {
        super.onPause()
        clockHandler.removeCallbacks(clockRunnable)
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        faceDetector?.close()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupToggle() {
        binding.tvToggleMode.setOnClickListener {
            if (isScanning) return@setOnClickListener
            switchMode(!isFaceMode)
        }
    }

    private fun setupMulaiScanButton() {
        binding.btnMulaiScan.setOnClickListener {
            if (isScanning || faceScanningActive) return@setOnClickListener

            if (isFaceMode) {
                faceScanningActive = true
                binding.btnMulaiScan.visibility = View.GONE
                binding.tvInstruksi.text = "Mendeteksi wajah..."
            } else {
                binding.btnMulaiScan.visibility = View.GONE
                binding.tvInstruksi.text = "Memindai sidik jari..."
                startFingerprintScan()
            }
        }
    }

    private fun switchMode(face: Boolean) {
        isFaceMode = face
        faceScanningActive = false
        binding.arcProgress.setProgress(0f)

        if (face) {
            binding.tvToggleMode.text = "Face ID"
            binding.ivScanIcon.setImageResource(R.drawable.ic_face_id)
            binding.ivScanIcon.visibility = View.GONE
            binding.cameraPreview.visibility = View.VISIBLE
            binding.tvInstruksi.visibility = View.VISIBLE
            binding.tvInstruksi.text = "Posisikan wajah di tengah lingkaran"
            binding.btnMulaiScan.visibility = View.VISIBLE

            if (hasCameraPermission()) {
                startCamera()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQ_CAMERA
                )
            }
        } else {
            binding.tvToggleMode.text = "Fingerprint"
            binding.ivScanIcon.setImageResource(R.drawable.ic_fingerprint)
            binding.ivScanIcon.visibility = View.VISIBLE
            binding.ivScanIcon.alpha = 1f
            binding.cameraPreview.visibility = View.GONE
            binding.tvInstruksi.visibility = View.VISIBLE
            binding.tvInstruksi.text = "Tekan MULAI SCAN untuk memindai sidik jari"
            binding.btnMulaiScan.visibility = View.VISIBLE

            stopCamera()
        }
    }

    private fun startFingerprintScan() {
        if (isScanning) return
        isScanning = true

        binding.ivScanIcon.alpha = 0.5f

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2500
            interpolator = LinearInterpolator()
            addUpdateListener {
                val v = it.animatedValue as Float
                binding.arcProgress.setProgress(v)
                binding.ivScanIcon.alpha = 0.5f + v * 0.5f
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (isScanning) {
                        binding.ivScanIcon.setImageResource(R.drawable.ic_check)
                        binding.ivScanIcon.alpha = 1f
                        saveAndFinish("fingerprint")
                    }
                }
            })
        }
        animator.start()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (isFaceMode) startCamera()
        } else {
            Toast.makeText(this, "Izin kamera dibutuhkan", Toast.LENGTH_SHORT).show()
            switchMode(false)
        }
    }

    @ExperimentalGetImage
    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            try {
                val provider = future.get()
                cameraProvider = provider

                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val options = FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setMinFaceSize(0.3f)
                    .build()

                faceDetector?.close()
                val detector = FaceDetection.getClient(options)
                faceDetector = detector

                analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && faceScanningActive && !isScanning) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        detector.process(image)
                            .addOnSuccessListener { faces ->
                                if (faces.isNotEmpty() && !isScanning && faceScanningActive) {
                                    runOnUiThread { onFaceDetected() }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    analysis
                )
            } catch (_: Exception) {
                Toast.makeText(this, "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    private fun onFaceDetected() {
        if (isScanning) return
        isScanning = true

        binding.tvInstruksi.text = "Wajah terdeteksi..."

        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            interpolator = LinearInterpolator()
            addUpdateListener {
                binding.arcProgress.setProgress(it.animatedValue as Float)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    saveAndFinish("faceid")
                }
            })
        }
        animator.start()
    }

    private fun saveAndFinish(metode: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Anda sudah melakukan absensi hari ini", Toast.LENGTH_SHORT).show()
                finish()
                return@postDelayed
            }

            val berhasil = db.simpanAbsen(userId, metode)
            if (berhasil) {
                Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
                isScanning = false
                faceScanningActive = false
                binding.arcProgress.setProgress(0f)
                binding.tvInstruksi.text = "Tekan MULAI SCAN untuk mencoba lagi"
                binding.btnMulaiScan.visibility = View.VISIBLE
            }
        }, 400)
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            if (isScanning) return@setOnClickListener
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
            if (isScanning) return@setOnClickListener
            if (isFaceMode) switchMode(false)
        }

        nav.btnProfile.setOnClickListener {
            if (isScanning) return@setOnClickListener
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

    companion object {
        private const val REQ_CAMERA = 101
    }
}
