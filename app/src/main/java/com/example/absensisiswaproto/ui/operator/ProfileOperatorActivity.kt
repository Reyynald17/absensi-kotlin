package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityProfileOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import com.example.absensisiswaproto.ui.guru.WelcomeGuruActivity

class ProfileOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileOperatorBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }
        PressEffect.attach(binding.btnLogout)

        setupBottomNav()
        loadProfile()
    }

    private fun loadProfile() {
        var namaLogin = OperatorSession.getNama(this)
        if (namaLogin.isBlank()) {
            namaLogin = "optu1"
            OperatorSession.save(this, namaLogin)
        }
        val op = db.getOperatorByNama(namaLogin) ?: com.example.absensisiswaproto.data.model.Operator(
            id = 1,
            nama = "optu1",
            password = "",
            namaLengkap = "Operator Sekolah",
            email = "OperatorSekolah@Gmail.com",
            noTelp = "-",
            jabatan = "Operator Sekolah"
        )

        binding.tvNamaDisplay.text = op.namaLengkap.ifBlank { "Operator Sekolah" }
        binding.tvEmailDisplay.text = op.email.ifBlank { "OperatorSekolah@Gmail.com" }
        binding.tvSekolah.text = "SMK Negeri 9 Medan"

        binding.tvFieldNama.text = op.namaLengkap.ifBlank { "Operator Sekolah" }
        binding.tvFieldEmail.text = op.email.ifBlank { "OperatorSekolah@Gmail.com" }
        binding.tvFieldNoTelp.text = op.noTelp.ifBlank { "-" }
        binding.tvFieldJabatan.text = op.jabatan.ifBlank { "Operator Sekolah" }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener {
            startActivity(
                Intent(this, AgendaEventActivity::class.java)
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
