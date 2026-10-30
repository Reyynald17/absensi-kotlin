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
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.ActivityRiwayatPeriodeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RiwayatPeriodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPeriodeBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: RekapSiswaAdapter

    private var kelas: String = ""
    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRiwayatPeriodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        kelas = intent.getStringExtra("EXTRA_KELAS") ?: "-"
        startDate = intent.getLongExtra("EXTRA_START_DATE", 0)
        endDate = intent.getLongExtra("EXTRA_END_DATE", 0)

        setupHeader()
        setupRecyclerView()
        setupBottomNav()

        loadRekap()
    }

    private fun setupHeader() {
        binding.btnBackPeriode.setOnClickListener { finish() }

        binding.tvKelasPeriode.text = kelas

        val formatter = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
        val start = formatter.format(Date(startDate))
        val end = formatter.format(Date(endDate))
        binding.tvRangePeriode.text = "$start - $end"
    }

    private fun setupRecyclerView() {
        adapter = RekapSiswaAdapter(emptyList()) { item ->
            val intent = Intent(this, DetailSiswaActivity::class.java).apply {
                putExtra("EXTRA_SISWA_ID", item.id)
                putExtra("EXTRA_SISWA_NIS", item.nis)
                putExtra("EXTRA_SISWA_NAMA", item.nama)
                putExtra("EXTRA_SISWA_KELAS", kelas)
                putExtra("EXTRA_TANGGAL",
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(endDate)))
            }
            startActivity(intent)
        }
        binding.rvRekapSiswa.layoutManager = LinearLayoutManager(this)
        binding.rvRekapSiswa.adapter = adapter
    }

    private fun loadRekap() {
        val siswaList = db.getSiswaByKelas(kelas)
        val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val startStr = dateParser.format(Date(startDate))
        val endStr = dateParser.format(Date(endDate))

        var totalHadir = 0
        var totalTelat = 0
        var totalAlpha = 0

        val rekapList = siswaList.map { user ->
            val dbRiwayat = db.getRiwayatAbsen(user.id)
            val fullRiwayat = RiwayatProvider.getAll(user.id, dbRiwayat)

            val inRange = fullRiwayat.filter { absensi ->
                absensi.tanggal >= startStr && absensi.tanggal <= endStr
            }

            val hadir = inRange.count { it.status.equals("hadir", ignoreCase = true) }
            val telat = inRange.count { it.status.equals("telat", ignoreCase = true) }
            val alpha = inRange.count {
                it.status.equals("absen", ignoreCase = true) ||
                it.status.equals("alpha", ignoreCase = true)
            }

            totalHadir += hadir
            totalTelat += telat
            totalAlpha += alpha

            RekapSiswaItem(
                id = user.id,
                nis = user.nis,
                nama = user.nama,
                hadir = hadir,
                telat = telat,
                alpha = alpha
            )
        }.sortedByDescending { it.hadir }

        adapter.updateData(rekapList)

        binding.tvTotalHadir.text = totalHadir.toString()
        binding.tvTotalTelat.text = totalTelat.toString()
        binding.tvTotalAlpha.text = totalAlpha.toString()
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
