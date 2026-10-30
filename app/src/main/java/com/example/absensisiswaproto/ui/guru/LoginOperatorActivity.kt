package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityLoginOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import com.example.absensisiswaproto.ui.operator.BerandaOperatorActivity

class LoginOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOperatorBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        PressEffect.attach(binding.btnMasuk)
        PressEffect.attach(binding.tvLupaPassword)

        binding.btnMasuk.setOnClickListener { doLogin() }

        binding.tvLupaPassword.setOnClickListener {
            Toast.makeText(this, "Hubungi admin sekolah untuk reset password", Toast.LENGTH_LONG).show()
        }
    }

    private fun doLogin() {
        val nama = binding.etNama.text.toString().trim()
        val pass = binding.etPassword.text.toString()

        if (nama.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Nama dan password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (db.loginOperator(nama, pass)) {
            Toast.makeText(this, "Selamat datang, $nama", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, BerandaOperatorActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Nama atau password salah", Toast.LENGTH_SHORT).show()
        }
    }
}
