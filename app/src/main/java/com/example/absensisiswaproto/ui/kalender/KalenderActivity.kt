package com.example.absensisiswaproto.ui.kalender

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityKalenderBinding
import com.example.absensisiswaproto.ui.beranda.BerandaActivity
import com.example.absensisiswaproto.ui.profile.ProfileActivity
import java.util.Calendar

class KalenderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKalenderBinding
    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    private var currentSelectedMonth: Int = 0 // 0 = Jan, 11 = Dec
    private val currentYear = 2026

    private val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
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

        setupBackButton()
        setupMonthGridButtons()
        setupMonthNavigation()
        setupBottomNav()
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            if (binding.layoutCalendarDetail.visibility == View.VISIBLE) {
                showMonthGridView()
            } else {
                finish()
            }
        }
    }

    private fun showMonthGridView() {
        binding.layoutMonthGrid.visibility = View.VISIBLE
        binding.layoutCalendarDetail.visibility = View.GONE
        binding.tvHeaderTitle.text = "kalender"
    }

    private fun showCalendarDetailView(monthIndex: Int) {
        currentSelectedMonth = monthIndex
        binding.layoutMonthGrid.visibility = View.GONE
        binding.layoutCalendarDetail.visibility = View.VISIBLE
        binding.tvHeaderTitle.text = "kalender"

        renderMonthlyCalendar(currentSelectedMonth, currentYear)
    }

    private fun renderMonthlyCalendar(month: Int, year: Int) {
        binding.tvMonthName.text = monthNames[month]
        binding.tvYear.text = year.toString()

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon ...
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val dayItems = mutableListOf<DayItem>()

        // Convert Sunday-first to Monday-first (0 = Mon, 6 = Sun)
        val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

        for (i in 0 until offset) {
            dayItems.add(DayItem("", isRed = false, isGreen = false))
        }

        // Green days (Upacara): 8, 15, 22, 29
        val greenDays = setOf(8, 15, 22, 29)
        // Red days (Holidays/Saturdays/Sundays): 1, 6, 7, 13, 14, 16, 20, 21, 27, 28
        val redDays = setOf(1, 6, 7, 13, 14, 16, 20, 21, 27, 28)

        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

            val isRed = isWeekend || redDays.contains(day)
            val isGreen = !isRed && greenDays.contains(day)

            dayItems.add(DayItem(day.toString(), isRed = isRed, isGreen = isGreen))
        }

        binding.rvCalendarDays.layoutManager = GridLayoutManager(this, 7)
        binding.rvCalendarDays.adapter = CalendarDaysAdapter(dayItems)

        // Events list in Informasi box
        when (month) {
            0 -> {
                binding.tvEvent1Number.text = "1"
                binding.tvEvent1Title.text = "Tahun baru 2026 masehi"
                binding.tvEvent2Number.text = "16"
                binding.tvEvent2Title.text = "Isra Mi'raj Nabi Muhammad SAW"
            }
            1 -> {
                binding.tvEvent1Number.text = "17"
                binding.tvEvent1Title.text = "Tahun baru Imlek 2577 Kongzili"
                binding.tvEvent2Number.text = "18"
                binding.tvEvent2Title.text = "Cuti bersama Imlek"
            }
            2 -> {
                binding.tvEvent1Number.text = "19"
                binding.tvEvent1Title.text = "Hari Raya Nyepi Tahun Baru Saka 1948"
                binding.tvEvent2Number.text = "20"
                binding.tvEvent2Title.text = "Cuti bersama Hari Raya Nyepi"
            }
            else -> {
                binding.tvEvent1Number.text = "17"
                binding.tvEvent1Title.text = "Hari Kemerdekaan Republik Indonesia"
                binding.tvEvent2Number.text = "18"
                binding.tvEvent2Title.text = "Upacara Bendera Sekolah"
            }
        }
    }

    private fun setupMonthGridButtons() {
        binding.btnJanuari.setOnClickListener { showCalendarDetailView(0) }
        binding.btnFebuari.setOnClickListener { showCalendarDetailView(1) }
        binding.btnMaret.setOnClickListener { showCalendarDetailView(2) }
        binding.btnApril.setOnClickListener { showCalendarDetailView(3) }
        binding.btnMei.setOnClickListener { showCalendarDetailView(4) }
        binding.btnJuni.setOnClickListener { showCalendarDetailView(5) }
        binding.btnJuli.setOnClickListener { showCalendarDetailView(6) }
        binding.btnAgustus.setOnClickListener { showCalendarDetailView(7) }
        binding.btnSeptember.setOnClickListener { showCalendarDetailView(8) }
        binding.btnOktober.setOnClickListener { showCalendarDetailView(9) }
        binding.btnNovember.setOnClickListener { showCalendarDetailView(10) }
        binding.btnDesember.setOnClickListener { showCalendarDetailView(11) }
    }

    private fun setupMonthNavigation() {
        binding.btnNextMonth.setOnClickListener {
            if (currentSelectedMonth < 11) {
                showCalendarDetailView(currentSelectedMonth + 1)
            } else {
                showCalendarDetailView(0)
            }
        }
    }

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
}
