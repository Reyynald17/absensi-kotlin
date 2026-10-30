package com.example.absensisiswaproto.ui.login

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.User
import com.example.absensisiswaproto.databinding.ActivityLoginBinding
import com.example.absensisiswaproto.ui.guru.WelcomeGuruActivity
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        setupButtonPressEffect()

        binding.tvAdmin.setOnClickListener {
            startActivity(Intent(this, WelcomeGuruActivity::class.java))
        }

        binding.btnSubmit.setOnClickListener {
            val nis = binding.etNis.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (nis.isEmpty()) {
                binding.etNis.error = "NIS tidak boleh kosong"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                return@setOnClickListener
            }

            val user = db.login(nis, password)
            if (user == null) {
                Toast.makeText(
                    this,
                    "NIS atau password salah",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                playLockAnimation(user)
            }
        }
    }

    private fun playLockAnimation(user: User) {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.ivLock.setImageResource(R.drawable.lock1)

        val popIn = ObjectAnimator.ofPropertyValuesHolder(
            binding.cardLoading,
            PropertyValuesHolder.ofFloat("scaleX", 0.3f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 0.3f, 1f)
        ).apply {
            duration = 400
            interpolator = AccelerateDecelerateInterpolator()
        }
        popIn.start()

        Handler(Looper.getMainLooper()).postDelayed({
            binding.ivLock.setImageResource(R.drawable.lock2)

            val shake = ObjectAnimator.ofFloat(
                binding.ivLock,
                "rotation",
                0f, -8f, 8f, -6f, 6f, 0f
            ).apply {
                duration = 500
            }
            shake.start()
        }, 800)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SiswaMainActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", user.id)
                putExtra("EXTRA_USER_NIS", user.nis)
                putExtra("EXTRA_USER_NAMA", user.nama)
                putExtra("EXTRA_USER_ROLE", user.role)
                putExtra("EXTRA_USER_KELAS", user.kelas)
            }
            startActivity(intent)
            finish()
        }, 2200)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupButtonPressEffect() {
        val btn = binding.btnSubmit

        btn.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.92f).apply {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }.start()
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.92f).apply {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }.start()
                    view.performClick()
                    false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    ObjectAnimator.ofFloat(view, "scaleX", 0.92f, 1f).apply {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }.start()
                    ObjectAnimator.ofFloat(view, "scaleY", 0.92f, 1f).apply {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }.start()
                    false
                }
                else -> false
            }
        }
    }
}
