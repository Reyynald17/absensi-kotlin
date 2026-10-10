package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.databinding.ActivityWelcomeGuruBinding

class WelcomeGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeGuruBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginGuru.setOnClickListener {
            startActivity(Intent(this, LoginGuruActivity::class.java))
        }

        binding.btnDaftarGuru.setOnClickListener {
            startActivity(Intent(this, RegisterGuruActivity::class.java))
        }

        binding.tvLoginOperator.setOnClickListener {
            Toast.makeText(this, "Fitur operator segera hadir", Toast.LENGTH_SHORT).show()
        }
    }
}