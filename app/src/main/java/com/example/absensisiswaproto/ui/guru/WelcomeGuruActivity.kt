package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.databinding.ActivityWelcomeGuruBinding

class WelcomeGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        binding.btnLoginGuru.setOnClickListener {
            startActivity(Intent(this, LoginGuruActivity::class.java))
        }

        binding.btnDaftarGuru.setOnClickListener {
            startActivity(Intent(this, RegisterGuruActivity::class.java))
        }

        binding.tvLoginOperator.setOnClickListener {
            startActivity(Intent(this, LoginOperatorActivity::class.java))
        }
    }
}
