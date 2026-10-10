package com.example.absensisiswaproto.ui.kalender

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityKalenderBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import java.util.Calendar

class KalenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKalenderBinding
    private lateinit var db: DatabaseHelper
    private lateinit var monthAdapter: MonthAdapter
    private lateinit var dayAdapter: CalendarDaysAdapter

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var currentSelectedMonth: Int = 0
    private val currentYear = 2026

    private val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    // Event per bulan (index 0 = Januari, dst)
    // Format: list of Pair(tanggal, judul)
    private val monthlyEvents: Map<Int, List<Pair<String, String>>> = mapOf(
        0 to listOf(
            "1" to "Tahun baru 2026 masehi",
            "16" to "Isra Mi'raj Nabi Muhammad SAW"
        ),
        1 to listOf(
            "17" to "Tahun baru Imlek 2577 Kongzili",
            "18" to "Cuti bersama Imlek"
        ),
        2 to listOf(
            "19" to "Hari Raya Nyepi Tahun Baru Saka 1948",
            "20" to "Cuti bersama Hari Raya Nyepi"
        ),
        3 to listOf(
            "3" to "Wafat Isa Al-Masih",
            "5" to "Awal Ramadhan 1447 H"
        ),
        4 to listOf(
            "1" to "Hari Buruh Internasional",
            "14" to "Hari Raya Waisak"
        ),
        5 to listOf(
            "1" to "Hari Lahir Pancasila"
        ),
        6 to emptyList(),
        7 to listOf(
            "17" to "Hari Kemerdekaan Republik Indonesia"
        ),
        8 to emptyList(),
        9 to listOf(
            "1" to "Hari Kesaktian Pancasila"
        ),
        10 to listOf(
            "25" to "Hari Guru Nasional"
        ),
        11 to listOf(
            "25" to "Hari Raya Natal"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        userId = intent.getIntExtra("EXTRA_USER_ID", 0)
        userNis = intent.getStringExtra("EXTRA_USER_NIS") ?: ""
        userNama = intent.getStringExtra("EXTRA_USER_NAMA") ?: "Siswa"
        userRole = intent.getStringExtra("EXTRA_USER_ROLE") ?: "siswa"
        userKelas = intent.getStringExtra("EXTRA_USER_KELAS") ?: "-"

        setupHeader()
        setupMonthGrid()
        setupCalendarGrid()
        setupMonthNavigation()
        setupBottomNav()

        showMonthGridView()
    }

    // =============================
    // HEADER
    // =============================
    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            if (binding.layoutCalendarDetail.visibility == View.VISIBLE) {
                showMonthGridView()
            } else {
                finish()
            }
        }
    }

    // =============================
    // STATE 1: MONTH GRID
    // =============================
    private fun setupMonthGrid() {
        monthAdapter = MonthAdapter(monthNames.toList()) { month ->
            val index = monthNames.indexOf(month)
            if (index >= 0) showCalendarDetailView(index)
        }
        binding.rvMonths.layoutManager = GridLayoutManager(this, 2)
        binding.rvMonths.adapter = monthAdapter
    }

    private fun showMonthGridView() {
        binding.layoutMonthGrid.visibility = View.VISIBLE
        binding.layoutCalendarDetail.visibility = View.GONE
        binding.tvHeaderTitle.text = "Kalender"
    }

    // =============================
    // STATE 2: CALENDAR DETAIL
    // =============================
    private fun setupCalendarGrid() {
        dayAdapter = CalendarDaysAdapter(emptyList())
        binding.rvCalendarDays.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarDays.adapter = dayAdapter
    }

    private fun showCalendarDetailView(monthIndex: Int) {
        currentSelectedMonth = monthIndex
        binding.layoutMonthGrid.visibility = View.GONE
        binding.layoutCalendarDetail.visibility = View.VISIBLE
        binding.tvHeaderTitle.text = "Kalender"

        renderMonthlyCalendar(monthIndex, currentYear)
    }

    private fun renderMonthlyCalendar(month: Int, year: Int) {
        binding.tvMonthName.text = monthNames[month]
        binding.tvYear.text = year.toString()

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dayItems = mutableListOf<DayItem>()

        // Konversi Sunday-first ke Monday-first
        val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

        // Empty cells sebelum tanggal 1
        for (i in 0 until offset) {
            dayItems.add(DayItem(day = ""))
        }

        // Ambil event bulan ini untuk warna merah
        val eventDays = monthlyEvents[month]
            ?.mapNotNull { it.first.toIntOrNull() }
            ?.toSet()
            ?: emptySet()

        // Hari ini (untuk highlight hijau)
        val today = Calendar.getInstance()
        val todayDay = today.get(Calendar.DAY_OF_MONTH)
        val todayMonth = today.get(Calendar.MONTH)
        val todayYear = today.get(Calendar.YEAR)

        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

            val isSunday = dayOfWeek == Calendar.SUNDAY
            val isEvent = eventDays.contains(day)
            val isToday = (day == todayDay && month == todayMonth && year == todayYear)

            val isRed = isSunday || isEvent
            val isGreen = !isRed && isToday

            dayItems.add(
                DayItem(
                    day = day.toString(),
                    isRed = isRed,
                    isGreen = isGreen
                )
            )
        }

        dayAdapter.updateData(dayItems)

        renderEventList(month)
    }

    private fun renderEventList(month: Int) {
        val container = binding.layoutEventList
        container.removeAllViews()

        val events = monthlyEvents[month] ?: emptyList()

        if (events.isEmpty()) {
            val tv = TextView(this).apply {
                text = "Tidak ada event bulan ini"
                setTextColor(0xFFB0B0B0.toInt())
                textSize = 11f
            }
            container.addView(tv)
            return
        }

        val inflater = LayoutInflater.from(this)
        for ((number, title) in events) {
            val item = inflater.inflate(R.layout.item_event, container, false)
            item.findViewById<TextView>(R.id.tvEventNumber).text = number
            item.findViewById<TextView>(R.id.tvEventTitle).text = title
            container.addView(item)
        }
    }

    // =============================
    // MONTH NAVIGATION
    // =============================
    private fun setupMonthNavigation() {
        binding.btnPrevMonth.setOnClickListener {
            if (currentSelectedMonth > 0) {
                showCalendarDetailView(currentSelectedMonth - 1)
            } else {
                Toast.makeText(this, "Sudah bulan pertama", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNextMonth.setOnClickListener {
            if (currentSelectedMonth < 11) {
                showCalendarDetailView(currentSelectedMonth + 1)
            } else {
                Toast.makeText(this, "Sudah bulan terakhir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // =============================
    // BOTTOM NAV
    // =============================
    private fun setupBottomNav() {
        val nav = binding.bottomNav

        nav.btnHome.setOnClickListener {
            val intent = Intent(this, BerandaActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
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

            if (db.sudahAbsenHariIni(userId)) {
                Toast.makeText(this, "Kamu sudah absen hari ini", Toast.LENGTH_SHORT).show()
            } else {
                val berhasil = db.simpanAbsen(userId, "fingerprint")
                if (berhasil) {
                    Toast.makeText(this, "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Absen gagal", Toast.LENGTH_SHORT).show()
                }
            }
        }

        nav.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    // =============================
    // BACK HANDLER
    // =============================
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.layoutCalendarDetail.visibility == View.VISIBLE) {
            showMonthGridView()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}