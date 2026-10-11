package com.example.absensisiswaproto.ui.guru

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityScanQrBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class ScanQrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanQrBinding
    private lateinit var db: DatabaseHelper
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val barcodeScanner = BarcodeScanning.getClient()
    private var isProcessing = false
    private var scanAnimator: ValueAnimator? = null

    @androidx.camera.core.ExperimentalGetImage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        setupTopBar()
        setupBottomNav()
        startScanBarAnimation()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), REQ_CAMERA
            )
        }
    }

    private fun setupTopBar() {
        binding.btnBack.setOnClickListener {
            scanAnimator?.cancel()
            finish()
        }

        binding.btnSearchIcon.setOnClickListener {
            Toast.makeText(this, "Arahkan QR ke kamera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScanBarAnimation() {
        scanAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1100
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { binding.scanBar.scaleY = it.animatedValue as Float }
            start()
        }
    }

    private fun setupBottomNav() {
        binding.btnHomeGuru.setOnClickListener {
            startActivity(
                Intent(this, BerandaGuruActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.btnSearchGuru.setOnClickListener {
            // Already on QR Scan page
        }
        binding.btnProfileGuru.setOnClickListener {
            startActivity(Intent(this, ProfileGuruActivity::class.java))
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            @Suppress("DEPRECATION")
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(cameraExecutor) { proxy ->
                val mediaImage = proxy.image
                if (mediaImage == null || isProcessing) {
                    proxy.close()
                    return@setAnalyzer
                }

                val image = InputImage.fromMediaImage(
                    mediaImage, proxy.imageInfo.rotationDegrees
                )
                barcodeScanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                            ?.rawValue?.let { handleQr(it) }
                    }
                    .addOnCompleteListener { proxy.close() }
            }

            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            provider.unbindAll()
            provider.bindToLifecycle(this, selector, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun handleQr(raw: String) {
        if (isProcessing) return
        val payload = parseQr(raw) ?: run {
            runOnUiThread {
                Toast.makeText(this, "QR tidak dikenali", Toast.LENGTH_SHORT).show()
            }
            return
        }
        isProcessing = true

        runOnUiThread {
            val siswa = db.getUserByNis(payload.nis)
            if (siswa == null) {
                isProcessing = false
                Toast.makeText(this, "NIS ${payload.nis} tidak ditemukan", Toast.LENGTH_LONG).show()
                return@runOnUiThread
            }

            val ok = db.setHadirByNis(siswa.nis, "qr_code")
            showResultDialog(
                nama = siswa.nama,
                nis = siswa.nis,
                kelas = siswa.kelas,
                success = ok
            )
        }
    }

    private fun parseQr(raw: String): QrPayload? {
        var nama: String? = null
        var nis: String? = null
        var kelas: String? = null

        raw.split(";").forEach { segment ->
            val kv = segment.split(":", limit = 2)
            if (kv.size == 2) {
                when (kv[0].trim().lowercase()) {
                    "nama" -> nama = kv[1].trim()
                    "nis" -> nis = kv[1].trim()
                    "kelas" -> kelas = kv[1].trim()
                }
            }
        }
        return if (!nis.isNullOrBlank()) QrPayload(nama, nis!!, kelas) else null
    }

    private fun showResultDialog(nama: String, nis: String, kelas: String, success: Boolean) {
        scanAnimator?.cancel()
        binding.scanBar.scaleY = 0f

        AlertDialog.Builder(this)
            .setTitle(if (success) "Absensi Berhasil" else "Gagal Menyimpan")
            .setMessage(
                "Nama  : $nama\n" +
                "NIS   : $nis\n" +
                "Kelas : $kelas\n" +
                "Status: ${if (success) "HADIR" else "GAGAL"}"
            )
            .setPositiveButton("Scan Lagi") { _, _ ->
                isProcessing = false
                scanAnimator?.start()
            }
            .setNegativeButton("Selesai") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        scanAnimator?.cancel()
        cameraExecutor.shutdown()
        barcodeScanner.close()
        super.onDestroy()
    }

    data class QrPayload(val nama: String?, val nis: String, val kelas: String?)

    companion object {
        private const val REQ_CAMERA = 101
    }
}