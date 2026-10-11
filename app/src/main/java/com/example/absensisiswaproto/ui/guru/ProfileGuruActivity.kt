package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityProfileGuruBinding

class ProfileGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileGuruBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        loadGuruProfile()
        setupBottomNav()
    }

    private fun loadGuruProfile() {
        var guruId = GuruSession.getGuruId(this)
        if (guruId <= 0) {
            guruId = intent.getIntExtra("EXTRA_GURU_ID", 1)
        }

        val guru = db.getGuruById(guruId)
        if (guru == null) {
            Toast.makeText(this, "Data guru tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.ivAvatar.setImageResource(SiswaHelper.getFotoGuru(guru.nama, guru.id))
        binding.tvNama.text = guru.nama.uppercase()
        binding.tvNip.text = guru.nip
        binding.tvNoHp.text = guru.noTelp
        binding.tvEmail.text = guru.email.uppercase()
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
            startActivity(Intent(this, SearchKelasActivity::class.java))
            finish()
        }
        binding.btnProfileGuru.setOnClickListener {
            // Already on Profile page
        }
    }
}