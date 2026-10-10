package com.example.absensisiswaproto.ui.pelajaran

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.databinding.ActivityPelajaranBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class PelajaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPelajaranBinding

    private var currentStage: String = "X"

    private val majors = listOf("DKV", "RPL", "TKJ", "AN", "PSPT", "PS")
    private val locals = listOf("1", "2", "3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPelajaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.overlayJadwal.root.visibility = View.GONE

        setupBlurEffect()
        setupHeader()
        setupTabs()
        setupSearch()
        setupBottomNav()
        setupOverlayJadwal()

        renderKelasList()
    }

    private fun setupBlurEffect() {
        val blurView: BlurView = binding.overlayJadwal.blurView
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupTabs() {
        binding.tabX.setOnClickListener { switchStage("X") }
        binding.tabXI.setOnClickListener { switchStage("XI") }
        binding.tabXII.setOnClickListener { switchStage("XII") }
    }

    private fun switchStage(stage: String) {
        currentStage = stage

        binding.tabX.setBackgroundResource(
            if (stage == "X") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabX.setTextColor(
            if (stage == "X") getColor(R.color.color_white) else getColor(R.color.color_primary_dark)
        )

        binding.tabXI.setBackgroundResource(
            if (stage == "XI") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabXI.setTextColor(
            if (stage == "XI") getColor(R.color.color_white) else getColor(R.color.color_primary_dark)
        )

        binding.tabXII.setBackgroundResource(
            if (stage == "XII") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabXII.setTextColor(
            if (stage == "XII") getColor(R.color.color_white) else getColor(R.color.color_primary_dark)
        )

        renderKelasList()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderKelasList(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun renderKelasList(filter: String = "") {
        val container = binding.containerKelas
        container.removeAllViews()

        val list = mutableListOf<String>()
        for (major in majors) {
            for (local in locals) {
                val kelas = "$currentStage $major $local"
                if (filter.isEmpty() || kelas.contains(filter, ignoreCase = true)) {
                    list.add(kelas)
                }
            }
        }

        val inflater = LayoutInflater.from(this)
        for (namaKelas in list) {
            val item = inflater.inflate(R.layout.item_kelas, container, false)
            item.findViewById<TextView>(R.id.tvNamaKelas).text = namaKelas

            item.setOnClickListener {
                bukaJadwal(namaKelas)
            }

            container.addView(item)
        }
    }

    private fun bukaJadwal(namaKelas: String) {
        binding.overlayJadwal.root.visibility = View.VISIBLE
        binding.overlayJadwal.root.bringToFront()
        Toast.makeText(this, "Jadwal $namaKelas", Toast.LENGTH_SHORT).show()
    }

    private fun setupOverlayJadwal() {
        binding.overlayJadwal.btnTutupJadwal.setOnClickListener {
            binding.overlayJadwal.root.visibility = View.GONE
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            finish()
        }

        nav.btnFingerprint.setOnClickListener {
            Toast.makeText(this, "Absen via fingerprint", Toast.LENGTH_SHORT).show()
        }

        nav.btnProfile.setOnClickListener {
            Toast.makeText(this, "Sudah di Pelajaran", Toast.LENGTH_SHORT).show()
        }
    }
}