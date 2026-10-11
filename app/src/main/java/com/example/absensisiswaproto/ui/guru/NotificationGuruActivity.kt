package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.NotifGuru
import com.example.absensisiswaproto.databinding.ActivityNotificationGuruBinding

class NotificationGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationGuruBinding
    private lateinit var adapter: NotifGuruAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupRecyclerView()
        setupBottomNav()
    }

    private fun setupHeader() {
        binding.btnBackNotifGuru.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = NotifGuruAdapter(getDummyNotif()) { notif ->
            Toast.makeText(this, notif.namaSiswa, Toast.LENGTH_SHORT).show()
        }
        binding.rvNotifGuru.layoutManager = LinearLayoutManager(this)
        binding.rvNotifGuru.adapter = adapter
    }

    private fun getDummyNotif(): List<NotifGuru> {
        return listOf(
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(1),
                namaSiswa = "PUTRA X RPL 2",
                deskripsi = "SUDAH ALPHA 2 HARI",
                tipeStatus = "alpha"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(2),
                namaSiswa = "PUTRA X DKV 1",
                deskripsi = "HARI INI TELAT",
                tipeStatus = "telat"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(1),
                namaSiswa = "PUTRA X RPL 2",
                deskripsi = "ALPHA DALAM SEMINGGU",
                tipeStatus = "alpha"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(2),
                namaSiswa = "PUTRA X RPL 2",
                deskripsi = "2 MINGGU ALPHA\nSEGERA TELPON WALI",
                tipeStatus = "alpha"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(1),
                namaSiswa = "PUTRA X DKV 1",
                deskripsi = "HARI INI TELAT",
                tipeStatus = "telat"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(2),
                namaSiswa = "PUTRA X DKV 1",
                deskripsi = "HARI INI TELAT",
                tipeStatus = "telat"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(1),
                namaSiswa = "PUTRA X DKV 1",
                deskripsi = "HARI INI TELAT",
                tipeStatus = "telat"
            ),
            NotifGuru(
                fotoRes = SiswaHelper.getFoto(2),
                namaSiswa = "PUTRA X DKV 1",
                deskripsi = "HARI INI TELAT",
                tipeStatus = "telat"
            )
        )
    }

    private fun setupBottomNav() {
        binding.btnHomeGuru.setOnClickListener { finish() }
        binding.btnSearchGuru.setOnClickListener {
            startActivity(Intent(this, SearchKelasActivity::class.java))
        }
        binding.btnProfileGuru.setOnClickListener {
            startActivity(Intent(this, ProfileGuruActivity::class.java))
        }
    }
}