package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.Guru
import com.example.absensisiswaproto.databinding.ActivityVerifikasiGuruBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerifikasiGuruActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifikasiGuruBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: GuruVerifikasiAdapter
    private var currentFilter = "all"
    private var currentQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiGuruBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        setupRecycler()
        setupChips()
        setupSearch()
        setupBottomNav()

        loadData()
    }

    private fun setupRecycler() {
        adapter = GuruVerifikasiAdapter(
            onDetail = { guru ->
                startActivity(Intent(this, DetailGuruVerifikasiActivity::class.java)
                    .putExtra("NIP", guru.nip))
            },
            onVerifikasi = { guru -> confirmVerifikasi(guru) }
        )
        binding.rvGuru.layoutManager = LinearLayoutManager(this)
        binding.rvGuru.adapter = adapter
    }

    private fun setupChips() {
        binding.chipSemua.setOnClickListener {
            currentFilter = "all"; highlightChip(); loadData()
        }
        binding.chipMenunggu.setOnClickListener {
            currentFilter = "pending"; highlightChip(); loadData()
        }
        binding.chipTerverifikasi.setOnClickListener {
            currentFilter = "verified"; highlightChip(); loadData()
        }
    }

    private fun highlightChip() {
        val active = R.drawable.bg_chip_white
        val inactive = R.drawable.bg_chip_white_outline
        val activeRed = android.graphics.Color.parseColor("#8B0000")
        val white = android.graphics.Color.parseColor("#FFFFFF")

        binding.chipSemua.setBackgroundResource(
            if (currentFilter == "all") active else inactive)
        binding.chipSemua.setTextColor(if (currentFilter == "all") activeRed else white)

        binding.chipMenunggu.setBackgroundResource(
            if (currentFilter == "pending") active else inactive)
        binding.chipMenunggu.setTextColor(if (currentFilter == "pending") activeRed else white)

        binding.chipTerverifikasi.setBackgroundResource(
            if (currentFilter == "verified") active else inactive)
        binding.chipTerverifikasi.setTextColor(if (currentFilter == "verified") activeRed else white)
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { e ->
            currentQuery = e?.toString().orEmpty()
            loadData()
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener {
            startActivity(Intent(this, AgendaEventActivity::class.java)); finish()
        }
        binding.bottomNav.btnNavBeranda.setOnClickListener {
            startActivity(Intent(this, BerandaOperatorActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish()
        }
        binding.bottomNav.btnNavProfil.setOnClickListener {
            startActivity(Intent(this, ProfileOperatorActivity::class.java))
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            val all = withContext(Dispatchers.IO) { db.countGuruByStatus("all") }
            val pending = withContext(Dispatchers.IO) { db.countGuruByStatus("pending") }
            val verified = withContext(Dispatchers.IO) { db.countGuruByStatus("verified") }
            val raw = withContext(Dispatchers.IO) {
                when (currentFilter) {
                    "pending" -> db.getGuruByStatus("pending")
                    "verified" -> db.getGuruByStatus("verified")
                    else -> db.getAllGuru()
                }
            }
            val filtered = if (currentQuery.isBlank()) raw
            else raw.filter {
                it.nama.contains(currentQuery, true) || it.nip.contains(currentQuery)
            }

            binding.chipSemua.text = "Semua [$all]"
            binding.chipMenunggu.text = "Menunggu [$pending]"
            binding.chipTerverifikasi.text = "Terverifikasi [$verified]"

            adapter.submit(filtered)
            binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun confirmVerifikasi(guru: Guru) {
        AlertDialog.Builder(this)
            .setTitle("Verifikasi Guru")
            .setMessage("Yakin memverifikasi ${guru.nama}?")
            .setPositiveButton("Verifikasi") { _, _ ->
                lifecycleScope.launch {
                    val ok = withContext(Dispatchers.IO) {
                        db.updateStatusGuruById(guru.id, "verified")
                    }
                    if (ok) {
                        Toast.makeText(
                            this@VerifikasiGuruActivity,
                            "${guru.nama} berhasil diverifikasi",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadData()
                    } else {
                        Toast.makeText(
                            this@VerifikasiGuruActivity,
                            "Gagal verifikasi — cek ID guru",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}