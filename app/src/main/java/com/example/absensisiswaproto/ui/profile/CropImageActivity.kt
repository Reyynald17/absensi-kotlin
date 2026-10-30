package com.example.absensisiswaproto.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.absensisiswaproto.databinding.ActivityCropImageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import kotlin.math.max

class CropImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropImageBinding
    private val matrix = Matrix()
    private val savedMatrix = Matrix()

    private enum class Mode { NONE, DRAG, ZOOM }
    private var mode = Mode.NONE

    private var startX = 0f
    private var startY = 0f
    private var sourceBitmap: Bitmap? = null

    private lateinit var scaleDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCropImage Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        val imageUriString = intent.getStringExtra("EXTRA_IMAGE_URI")
        if (imageUriString == null) {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val uri = imageUriString.toUri()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }
                withContext(Dispatchers.Main) {
                    sourceBitmap = bitmap
                    binding.ivCropSource.setImageBitmap(bitmap)
                    binding.ivCropSource.post { initMatrix(bitmap) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CropImageActivity, "Gagal membaca gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                matrix.postScale(detector.scaleFactor, detector.scaleFactor, detector.focusX, detector.focusY)
                constrainMatrix()
                return true
            }
        })

        binding.ivCropSource.setOnTouchListener { _: View, event: MotionEvent ->
            scaleDetector.onTouchEvent(event)
            val action = event.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    startX = event.x
                    startY = event.y
                    mode = Mode.DRAG
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    mode = Mode.ZOOM
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == Mode.DRAG) {
                        matrix.set(savedMatrix)
                        matrix.postTranslate(event.x - startX, event.y - startY)
                        constrainMatrix()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    mode = Mode.NONE
                }
            }
            true
        }

        binding.btnCancelCrop.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.btnSaveCrop.setOnClickListener {
            cropAndSaveImage()
        }
    }

    private fun initMatrix(bitmap: Bitmap) {
        val cropRect = binding.cropOverlay.getCropRect()
        val scaleX = cropRect.width() / bitmap.width.toFloat()
        val scaleY = cropRect.height() / bitmap.height.toFloat()
        val initialScale = max(scaleX, scaleY)

        matrix.reset()
        matrix.postScale(initialScale, initialScale)
        val redundantX = (cropRect.width() - (bitmap.width * initialScale)) / 2f
        val redundantY = (cropRect.height() - (bitmap.height * initialScale)) / 2f
        matrix.postTranslate(cropRect.left + redundantX, cropRect.top + redundantY)
        binding.ivCropSource.imageMatrix = matrix
    }

    private fun constrainMatrix() {
        val bitmap = sourceBitmap ?: return
        val cropRect = binding.cropOverlay.getCropRect()
        val values = FloatArray(9)
        matrix.getValues(values)

        val scale = values[Matrix.MSCALE_X]
        val scaleX = cropRect.width() / bitmap.width.toFloat()
        val scaleY = cropRect.height() / bitmap.height.toFloat()
        val minScale = max(scaleX, scaleY)
        val maxScale = minScale * 10f // Allow zooming in up to 10x into photo details

        if (scale < minScale) {
            matrix.postScale(minScale / scale, minScale / scale)
        } else if (scale > maxScale) {
            matrix.postScale(maxScale / scale, maxScale / scale)
        }

        matrix.getValues(values)
        var transX = values[Matrix.MTRANS_X]
        var transY = values[Matrix.MTRANS_Y]

        val bitmapW = bitmap.width * values[Matrix.MSCALE_X]
        val bitmapH = bitmap.height * values[Matrix.MSCALE_Y]

        val minTransX = cropRect.right - bitmapW
        val maxTransX = cropRect.left
        val minTransY = cropRect.bottom - bitmapH
        val maxTransY = cropRect.top

        transX = transX.coerceIn(minTransX, maxTransX)
        transY = transY.coerceIn(minTransY, maxTransY)

        values[Matrix.MTRANS_X] = transX
        values[Matrix.MTRANS_Y] = transY
        matrix.setValues(values)
        binding.ivCropSource.imageMatrix = matrix
    }

    private fun cropAndSaveImage() {
        val bitmap = sourceBitmap ?: return
        val cropRect = binding.cropOverlay.getCropRect()

        val inverseMatrix = Matrix()
        matrix.invert(inverseMatrix)

        val pts = floatArrayOf(
            cropRect.left, cropRect.top,
            cropRect.right, cropRect.bottom
        )
        inverseMatrix.mapPoints(pts)

        val mappedRect = RectF(pts[0], pts[1], pts[2], pts[3])

        val left = mappedRect.left.coerceIn(0f, bitmap.width.toFloat())
        val top = mappedRect.top.coerceIn(0f, bitmap.height.toFloat())
        val right = mappedRect.right.coerceIn(left, bitmap.width.toFloat())
        val bottom = mappedRect.bottom.coerceIn(top, bitmap.height.toFloat())

        val width = (right - left).toInt().coerceAtLeast(50)
        val height = (bottom - top).toInt().coerceAtLeast(50)

        try {
            val croppedBitmap = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), width, height)
            val userId = intent.getIntExtra("EXTRA_USER_ID", 0)
            val file = java.io.File(filesDir, "profile_photo_$userId.png")
            FileOutputStream(file).use { out ->
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val resultIntent = Intent().apply {
                putExtra("EXTRA_FILE_PATH", file.absolutePath)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memotong gambar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
