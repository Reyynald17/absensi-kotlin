package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.NotifGuru
import com.example.absensisiswaproto.databinding.ActivityNotificationGuruBinding

class NotificationGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationGuruBinding
    private lateinit var adapter: NotifGuruAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

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
        binding.btnHomeGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_HOME)
            }
            startActivity(intent)
        }
        binding.btnSearchGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_SEARCH)
            }
            startActivity(intent)
        }
        binding.btnProfileGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_PROFILE)
            }
            startActivity(intent)
        }
    }
}
