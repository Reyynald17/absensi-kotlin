package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivityRekapKelasDetailBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RekapKelasDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapKelasDetailBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: SiswaAbsensiAdapter

    private var jurusan: String = ""
    private var tanggalFilter: String = ""
    private var kelasTerpilih: String = ""

    private val stages = listOf("X", "XI", "XII")
    private val locals = listOf("1", "2", "3")

    private val kelasOptions: List<String> by lazy {
        stages.flatMap { stage ->
            locals.map { local -> "$stage $jurusan $local" }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRekapKelasDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        jurusan = intent.getStringExtra("EXTRA_JURUSAN") ?: "DKV"
        tanggalFilter = intent.getStringExtra("EXTRA_TANGGAL")
            ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        binding.tvJudulRekap.text = "Rekap Kelas - $jurusan"

        setupHeader()
        setupDatePicker()
        setupKelasPicker()
        setupRecyclerView()
        setupBottomNav()

        resetData()
    }

    private fun setupHeader() {
        binding.btnBackDetailRekap.setOnClickListener { finish() }
    }

    private fun setupDatePicker() {
        updateTanggalText()
        binding.cardTanggalDetail.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(parseTanggalToMillis(tanggalFilter))
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tanggalFilter = formatter.format(Date(selection))
                updateTanggalText()
                if (kelasTerpilih.isNotEmpty()) {
                    loadRekap()
                }
            }

            picker.show(supportFragmentManager, "date_picker_detail")
        }
    }

    private fun updateTanggalText() {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val date = parser.parse(tanggalFilter)
        binding.tvTanggalDetail.text = if (date != null) formatter.format(date) else tanggalFilter
    }

    private fun parseTanggalToMillis(tanggal: String): Long {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.parse(tanggal)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun setupKelasPicker() {
        binding.cardPilihKelas.setOnClickListener {
            if (kelasOptions.isEmpty()) {
                Toast.makeText(this, "Tidak ada kelas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val arr = kelasOptions.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Pilih Kelas $jurusan")
                .setItems(arr) { _, which ->
                    kelasTerpilih = arr[which]
                    binding.tvPilihKelas.text = kelasTerpilih
                    binding.tvPilihKelas.setTextColor(0xFF222222.toInt())
                    loadRekap()
                }
                .show()
        }
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
        binding.rvRekapSiswa.layoutManager = LinearLayoutManager(this)
        binding.rvRekapSiswa.adapter = adapter
    }

    private fun resetData() {
        binding.donutChart.setData(0, 0, 0)
        binding.tvEmptyState.text = "Pilih kelas terlebih dahulu"
        binding.tvEmptyState.visibility = View.VISIBLE
        adapter.updateData(emptyList())
    }

    private fun loadRekap() {
        if (kelasTerpilih.isEmpty()) {
            resetData()
            return
        }

        val siswaList = db.getSiswaByKelas(kelasTerpilih)

        if (siswaList.isEmpty()) {
            binding.donutChart.setData(0, 0, 0)
            binding.tvEmptyState.text = "Belum ada data siswa di kelas ini"
            binding.tvEmptyState.visibility = View.VISIBLE
            adapter.updateData(emptyList())
            return
        }

        var countHadir = 0
        var countTelat = 0
        var countAlpha = 0

        val mapped = siswaList.map { user ->
            val dbRiwayat = db.getRiwayatAbsen(user.id)
            val fullRiwayat = RiwayatProvider.getAll(user.id, dbRiwayat)
            val match = fullRiwayat.find { it.tanggal == tanggalFilter }
            val status = match?.status ?: "belum"

            when (status.lowercase()) {
                "hadir" -> countHadir++
                "telat" -> countTelat++
                "absen", "alpha" -> countAlpha++
            }

            SiswaAbsensiItem(
                id = user.id,
                nis = user.nis,
                nama = user.nama,
                kelas = user.kelas,
                status = status
            )
        }

        binding.donutChart.setData(countHadir, countTelat, countAlpha)

        if (countHadir + countTelat + countAlpha == 0) {
            binding.tvEmptyState.text = "Belum ada absensi hari ini"
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.tvEmptyState.visibility = View.GONE
        }

        adapter.updateData(mapped)
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

    override fun onResume() {
        super.onResume()
        if (kelasTerpilih.isNotEmpty()) {
            loadRekap()
        }
    }
}
