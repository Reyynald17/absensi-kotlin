package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityProfileOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import com.example.absensisiswaproto.ui.guru.WelcomeGuruActivity

class ProfileOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileOperatorBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }
        PressEffect.attach(binding.btnLogout)

        setupBottomNav()
        loadProfile()
    }

    private fun loadProfile() {
        val namaLogin = OperatorSession.getNama(this)
        if (namaLogin.isBlank()) {
            Toast.makeText(this, "Sesi tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val op = db.getOperatorByNama(namaLogin)
        if (op == null) {
            Toast.makeText(this, "Data operator tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        binding.tvNamaDisplay.text = op.namaLengkap.ifBlank { op.nama }
        binding.tvEmailDisplay.text = op.email
        binding.tvSekolah.text = "SMK Negeri 9 Medan"

        binding.tvFieldNama.text = op.namaLengkap.ifBlank { op.nama }
        binding.tvFieldEmail.text = op.email
        binding.tvFieldNoTelp.text = op.noTelp.ifBlank { "-" }
        binding.tvFieldJabatan.text = op.jabatan.ifBlank { "-" }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener {
            startActivity(
                Intent(this, KalenderOperatorActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.bottomNav.btnNavBeranda.setOnClickListener {
            startActivity(
                Intent(this, BerandaOperatorActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.bottomNav.btnNavProfil.setOnClickListener { }
        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Yakin ingin keluar dari akun operator?")
            .setPositiveButton("Log Out") { _, _ ->
                OperatorSession.clear(this)
                Toast.makeText(this, "Berhasil log out", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(this, WelcomeGuruActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}