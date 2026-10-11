package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivityAbsensiSiswaBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AbsensiSiswaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiSiswaBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: SiswaAbsensiAdapter

    private var guruNama: String = "Guru"
    private var kelasFilter: String = "X DKV 1"
    private var tanggalFilter: String = ""
    private var statusFilter: String = "semua"

    private val stages = listOf("X", "XI", "XII")
    private val majors = listOf("DKV", "RPL", "TKJ", "AN", "PSPT", "PS")
    private val locals = listOf("1", "2", "3")

    private val kelasOptions: Array<String> =
        stages.flatMap { stage ->
            majors.flatMap { major ->
                locals.map { local -> "$stage $major $local" }
            }
        }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiSiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        guruNama = intent.getStringExtra("EXTRA_GURU_NAMA") ?: "Guru"
        kelasFilter = intent.getStringExtra("EXTRA_GURU_KELAS") ?: "X DKV 1"

        tanggalFilter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        setupHeader()
        setupDatePicker()
        setupKelasPicker()
        setupFilterChips()
        setupRecyclerView()
        setupBottomNav()

        loadData()
    }

    private fun setupHeader() {
        binding.btnBackAbsensi.setOnClickListener { finish() }
    }

    private fun setupDatePicker() {
        updateTanggalText()

        binding.cardTanggal.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Absensi")
                .setSelection(parseTanggalToMillis(tanggalFilter))
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tanggalFilter = formatter.format(Date(selection))
                updateTanggalText()
                loadData()
            }

            picker.show(supportFragmentManager, "date_picker")
        }
    }

    private fun parseTanggalToMillis(tanggal: String): Long {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.parse(tanggal)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun updateTanggalText() {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val date = parser.parse(tanggalFilter)
        binding.tvTanggalAbsensi.text = if (date != null) formatter.format(date) else tanggalFilter
    }

    private fun setupKelasPicker() {
        binding.tvKelasAbsensi.text = kelasFilter
        binding.cardKelas.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Kelas")
            builder.setItems(kelasOptions) { _, which ->
                kelasFilter = kelasOptions[which]
                binding.tvKelasAbsensi.text = kelasFilter
                loadData()
            }
            builder.show()
        }
    }

    private fun setupFilterChips() {
        binding.chipSemua.setOnClickListener { setFilter("semua") }
        binding.chipHadir.setOnClickListener { setFilter("hadir") }
        binding.chipTelat.setOnClickListener { setFilter("telat") }
        binding.chipAlpha.setOnClickListener { setFilter("alpha") }
    }

    private fun setFilter(filter: String) {
        statusFilter = filter

        val chips = mapOf(
            binding.chipSemua to "semua",
            binding.chipHadir to "hadir",
            binding.chipTelat to "telat",
            binding.chipAlpha to "alpha"
        )

        chips.forEach { (chip, value) ->
            val isActive = value == filter
            chip.setBackgroundResource(
                if (isActive) com.example.absensisiswaproto.R.drawable.bg_chip_filter_active
                else com.example.absensisiswaproto.R.drawable.bg_chip_filter_inactive
            )
            chip.setTextColor(
                if (isActive) 0xFFFFFFFF.toInt() else 0xFF1E8827.toInt()
            )
        }

        loadData()
    }

    private fun setupRecyclerView() {
        adapter = SiswaAbsensiAdapter(emptyList()) { siswa ->
            val intent = Intent(this, DetailSiswaActivity::class.java).apply {
                putExtra("EXTRA_SISWA_ID", siswa.id)
                putExtra("EXTRA_SISWA_NIS", siswa.nis)
                putExtra("EXTRA_SISWA_NAMA", siswa.nama)
                putExtra("EXTRA_SISWA_KELAS", siswa.kelas)
                putExtra("EXTRA_TANGGAL", tanggalFilter)
            }
            startActivity(intent)
        }
        binding.rvSiswa.layoutManager = LinearLayoutManager(this)
        binding.rvSiswa.adapter = adapter
    }

    private fun loadData() {
        val siswaList = db.getSiswaByKelas(kelasFilter)
        val isWeekend = SiswaHelper.isWeekend(tanggalFilter)

        val mapped = siswaList.map { user ->
            val status = if (isWeekend) {
                "libur"
            } else {
                val dbRiwayat = db.getRiwayatAbsen(user.id)
                val fullRiwayat = RiwayatProvider.getAll(user.id, dbRiwayat)
                val match = fullRiwayat.find { it.tanggal == tanggalFilter }
                match?.status ?: "belum"
            }
            SiswaAbsensiItem(
                id = user.id,
                nis = user.nis,
                nama = user.nama,
                kelas = user.kelas,
                status = status
            )
        }

        val filtered = when (statusFilter) {
            "semua" -> mapped
            "libur" -> mapped.filter { it.status.equals("libur", ignoreCase = true) }
            else -> mapped.filter { it.status.equals(statusFilter, ignoreCase = true) }
        }

        adapter.updateData(filtered)
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

    override fun onResume() {
        super.onResume()
        loadData()
    }
}