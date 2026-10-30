package com.example.absensisiswaproto.ui.guru

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityRegisterGuruBinding
import com.example.absensisiswaproto.databinding.DialogPendingVerificationBinding

class RegisterGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterGuruBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        binding.btnBackDaftar.setOnClickListener {
            finish()
        }

        binding.btnSubmitDaftar.setOnClickListener {
            submitDaftar()
        }
    }

    private fun submitDaftar() {
        val nama = binding.etNamaDaftar.text.toString().trim()
        val nip = binding.etNipDaftar.text.toString().trim()
        val telp = binding.etTelpDaftar.text.toString().trim()
        val email = binding.etEmailDaftar.text.toString().trim()

        if (nama.isEmpty()) {
            binding.etNamaDaftar.error = "Nama tidak boleh kosong"
            return
        }
        if (nip.isEmpty()) {
            binding.etNipDaftar.error = "NIP tidak boleh kosong"
            return
        }
        if (telp.isEmpty()) {
            binding.etTelpDaftar.error = "Nomor telepon tidak boleh kosong"
            return
        }
        if (email.isEmpty()) {
            binding.etEmailDaftar.error = "Email tidak boleh kosong"
            return
        }

        if (db.cekGuruExist(nip)) {
            binding.etNipDaftar.error = "NIP sudah terdaftar"
            return
        }

        val berhasil = db.registerGuru(
            nip = nip,
            nama = nama,
            noTelp = telp,
            email = email
        )

        if (berhasil) {
            showPendingDialog()
        } else {
            Toast.makeText(this, "Pendaftaran gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPendingDialog() {
        val dialog = Dialog(this)
        val dialogBinding = DialogPendingVerificationBinding.inflate(layoutInflater)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnOkPending.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
