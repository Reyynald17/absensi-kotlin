package com.example.absensisiswaproto.ui.splash

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.databinding.ActivitySplashBinding
import com.example.absensisiswaproto.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playSplashAnimation()
    }

    private fun playSplashAnimation() {
        val logo = binding.ivLogo

        val startOffsetY = 150f
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        logo.alpha = 0f
        logo.translationY = startOffsetY

        val fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f).apply {
            duration = 1500
            interpolator = AccelerateDecelerateInterpolator()
        }
        fadeIn.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val moveUp = ObjectAnimator.ofPropertyValuesHolder(
                logo,
                PropertyValuesHolder.ofFloat(
                    "translationY",
                    startOffsetY,
                    -screenHeight / 3f
                ),
                PropertyValuesHolder.ofFloat("scaleX", 1f, 0.4f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 0.4f)
            ).apply {
                duration = 1500
                interpolator = AccelerateDecelerateInterpolator()
            }
            moveUp.start()
        }, 2500)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 5000)
    }
}