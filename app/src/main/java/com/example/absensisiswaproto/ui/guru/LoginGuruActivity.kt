package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityLoginGuruBinding

class LoginGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginGuruBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnBackGuru.setOnClickListener {
            finish()
        }

        binding.btnSubmitGuru.setOnClickListener {
            val nama = binding.etNamaGuru.text.toString().trim()
            val nip = binding.etNipGuru.text.toString().trim()

            if (nama.isEmpty()) {
                binding.etNamaGuru.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }
            if (nip.isEmpty()) {
                binding.etNipGuru.error = "NIP tidak boleh kosong"
                return@setOnClickListener
            }

            val guru = db.loginGuru(nip, nama)
            if (guru == null) {
                Toast.makeText(this, "NIP atau Nama Lengkap salah", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BerandaGuruActivity::class.java).apply {
                    putExtra("EXTRA_GURU_ID", guru.id)
                    putExtra("EXTRA_GURU_NIP", guru.nip)
                    putExtra("EXTRA_GURU_NAMA", guru.nama)
                    putExtra("EXTRA_GURU_KELAS", guru.kelasMapel)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}