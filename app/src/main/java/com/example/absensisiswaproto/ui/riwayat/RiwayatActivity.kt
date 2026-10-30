package com.example.absensisiswaproto.ui.riwayat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.databinding.ActivityRiwayatBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: RiwayatAdapter

    private var userId: Int = 0
    private var userNama: String = ""

    private var isDropdownOpen = false
    private var selectedMonth: String = "Semua Bulan"
    private var allRiwayat: List<Absensi> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"

        setupHeader()
        setupRecyclerView()
        setupDropdownKalender()
        setupBottomNav()

        loadRiwayat()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = RiwayatAdapter(emptyList())
        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.adapter = adapter
    }

    private fun setupDropdownKalender() {
        binding.layoutComboboxHeader.setOnClickListener {
            toggleDropdown()
        }
    }

    private fun toggleDropdown() {
        isDropdownOpen = !isDropdownOpen

        if (isDropdownOpen) {
            binding.layoutMonthDropdown.visibility = View.VISIBLE
            binding.ivComboboxArrow.rotation = 180f
        } else {
            binding.layoutMonthDropdown.visibility = View.GONE
            binding.ivComboboxArrow.rotation = 0f
        }
    }

    private fun closeDropdown() {
        isDropdownOpen = false
        binding.layoutMonthDropdown.visibility = View.GONE
        binding.ivComboboxArrow.rotation = 0f
    }

    private fun setupMonthButtons() {
        val monthButtons = mapOf(
            binding.btnMonthSemua to "Semua Bulan",
            binding.btnMonthJanuari to "Januari",
            binding.btnMonthFebuari to "Februari",
            binding.btnMonthMaret to "Maret",
            binding.btnMonthApril to "April",
            binding.btnMonthMei to "Mei",
            binding.btnMonthJuni to "Juni",
            binding.btnMonthJuli to "Juli",
            binding.btnMonthAgustus to "Agustus",
            binding.btnMonthSeptember to "September",
            binding.btnMonthOktober to "Oktober",
            binding.btnMonthNovember to "November",
            binding.btnMonthDesember to "Desember"
        )

        monthButtons.forEach { (button, monthName) ->
            button.setOnClickListener {
                selectedMonth = monthName
                binding.tvFilterKalender.text = monthName
                closeDropdown()
                applyFilter()
            }
        }
    }

    private fun applyFilter() {
        val filtered = if (selectedMonth == "Semua Bulan") {
            allRiwayat
        } else {
            allRiwayat.filter { absensi ->
                val bulanAbsensi = getBulanName(absensi.tanggal)
                bulanAbsensi.equals(selectedMonth, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)
        toggleEmptyState(filtered.isEmpty())
    }

    private fun getBulanName(tanggal: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val monthFormat = SimpleDateFormat("MMMM", Locale("id", "ID"))
            val date = input.parse(tanggal)
            if (date != null) monthFormat.format(date) else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvRiwayat.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvRiwayat.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            finish()
        }

        nav.btnFingerprint.setOnClickListener {
            if (db.isHariLibur()) {
                Toast.makeText(
                    this,
                    "Absen hanya bisa dilakukan Senin - Jumat",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Absen via fingerprint", Toast.LENGTH_SHORT).show()
        }

        nav.btnProfile.setOnClickListener {
            finish()
        }
    }

    private fun loadRiwayat() {
        setupMonthButtons()

        val dataDb = db.getRiwayatAbsen(userId)
        allRiwayat = RiwayatProvider.getAll(userId, dataDb)

        selectedMonth = "Semua Bulan"
        binding.tvFilterKalender.text = "Semua Bulan"
        applyFilter()
    }
}
