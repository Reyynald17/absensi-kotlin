package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivitySearchKelasDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchKelasDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchKelasDetailBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: SiswaAbsensiAdapter

    private var kelas: String = ""
    private var allSiswa: List<SiswaAbsensiItem> = emptyList()
    private val tanggalHariIni: String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchKelasDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        kelas = intent.getStringExtra("EXTRA_KELAS") ?: "-"

        binding.tvJudulKelasDetail.text = kelas

        setupHeader()
        setupSearch()
        setupRecyclerView()
        setupBottomNav()

        loadSiswaAsync()
    }

    private fun setupHeader() {
        binding.btnBackDetailSearch.setOnClickListener { finish() }
    }

    private fun setupSearch() {
        binding.etSearchSiswa.doAfterTextChanged { editable ->
            filterSiswa(editable?.toString().orEmpty())
        }
    }

    private fun setupRecyclerView() {
        adapter = SiswaAbsensiAdapter(emptyList()) { siswa ->
            val intent = Intent(this, DetailSiswaActivity::class.java).apply {
                putExtra("EXTRA_SISWA_ID", siswa.id)
                putExtra("EXTRA_SISWA_NIS", siswa.nis)
                putExtra("EXTRA_SISWA_NAMA", siswa.nama)
                putExtra("EXTRA_SISWA_KELAS", siswa.kelas)
                putExtra("EXTRA_TANGGAL", tanggalHariIni)
            }
            startActivity(intent)
        }
        binding.rvSiswaDetailSearch.layoutManager = LinearLayoutManager(this)
        binding.rvSiswaDetailSearch.adapter = adapter
    }

    private fun loadSiswaAsync() {
        lifecycleScope.launch {
            val (siswaList, mapped) = withContext(Dispatchers.IO) {
                val list = db.getSiswaByKelas(kelas)
                val mappedList = list.map { user ->
                    val dbRiwayat = db.getRiwayatAbsen(user.id)
                    val fullRiwayat = RiwayatProvider.getAll(user.id, dbRiwayat)
                    val match = fullRiwayat.find { it.tanggal == tanggalHariIni }
                    val status = match?.status ?: "belum"

                    SiswaAbsensiItem(
                        id = user.id,
                        nis = user.nis,
                        nama = user.nama,
                        kelas = user.kelas,
                        status = status
                    )
                }
                list to mappedList
            }

            binding.tvJumlahSiswaDetail.text = "${siswaList.size} Siswa"
            allSiswa = mapped
            filterSiswa(binding.etSearchSiswa.text.toString())
        }
    }

    private fun filterSiswa(keyword: String) {
        val filtered = if (keyword.isBlank()) {
            allSiswa
        } else {
            allSiswa.filter {
                it.nama.contains(keyword, ignoreCase = true) ||
                it.nis.contains(keyword, ignoreCase = true)
            }
        }
        adapter.updateData(filtered)
    }

    private fun setupBottomNav() {
        binding.btnHomeGuru.setOnClickListener { finish() }
        binding.btnSearchGuru.setOnClickListener {
            finish()
        }
        binding.btnProfileGuru.setOnClickListener {
            startActivity(Intent(this, ProfileGuruActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadSiswaAsync()
    }
}