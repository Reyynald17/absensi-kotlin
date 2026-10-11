package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityKalenderOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class KalenderOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKalenderOperatorBinding
    private lateinit var db: DatabaseHelper
    private lateinit var dayAdapter: KalenderDayAdapter
    private lateinit var infoAdapter: InfoEventAdapter

    private var currentYear = 2026
    private var currentMonth = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKalenderOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }
        PressEffect.attach(binding.fabAddEvent)

        setupRecyclers()
        setupNav()
        setupBottomNav()

        renderMonth()
    }

    private fun setupRecyclers() {
        dayAdapter = KalenderDayAdapter()
        binding.rvKalender.layoutManager = GridLayoutManager(this, 7)
        binding.rvKalender.adapter = dayAdapter

        infoAdapter = InfoEventAdapter { event ->
            @Suppress("DEPRECATION")
            startActivityForResult(
                Intent(this, FormEventActivity::class.java)
                    .putExtra("MODE", "edit")
                    .putExtra("EVENT_ID", event.id),
                REQ_FORM
            )
        }
        binding.rvInfoEvent.layoutManager = LinearLayoutManager(this)
        binding.rvInfoEvent.adapter = infoAdapter
    }

    private fun setupNav() {
        binding.btnPrevMonth.setOnClickListener {
            if (currentMonth == 1) {
                currentMonth = 12; currentYear--
            } else currentMonth--
            renderMonth()
        }
        binding.btnNextMonth.setOnClickListener {
            if (currentMonth == 12) {
                currentMonth = 1; currentYear++
            } else currentMonth++
            renderMonth()
        }
        binding.fabAddEvent.setOnClickListener {
            @Suppress("DEPRECATION")
            startActivityForResult(
                Intent(this, FormEventActivity::class.java).putExtra("MODE", "add"),
                REQ_FORM
            )
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener {
            startActivity(Intent(this, AgendaEventActivity::class.java))
        }
        binding.bottomNav.btnNavBeranda.setOnClickListener {
            startActivity(
                Intent(this, BerandaOperatorActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        binding.bottomNav.btnNavProfil.setOnClickListener {
            startActivity(Intent(this, ProfileOperatorActivity::class.java))
        }
    }

    private fun renderMonth() {
        binding.tvMonthLabel.text = OperatorUtils.formatBulanIndonesia(currentMonth)
        binding.tvYearLabel.text = currentYear.toString()

        lifecycleScope.launch {
            val (cells, events) = withContext(Dispatchers.IO) {
                val mapEvent = db.getAllEventBulan(currentYear, currentMonth)

                val cal = Calendar.getInstance()
                cal.set(currentYear, currentMonth - 1, 1)

                var firstDow = cal.get(Calendar.DAY_OF_WEEK) - 1
                if (firstDow == 0) firstDow = 7

                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                val list = mutableListOf<DayCell>()
                for (i in 1 until firstDow) list += DayCell(empty = true)
                for (d in 1..daysInMonth) {
                    val dayCal = Calendar.getInstance()
                    dayCal.set(currentYear, currentMonth - 1, d)
                    val dow = dayCal.get(Calendar.DAY_OF_WEEK)
                    val isWeekend = (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY)
                    val evList = mapEvent[d] ?: emptyList()
                    val tipe = evList.minByOrNull { tipePriority(it.tipe) }?.tipe
                    list += DayCell(
                        day = d, tipe = tipe, isWeekend = isWeekend
                    )
                }
                while (list.size < 42) list += DayCell(empty = true)

                val flat = mapEvent.values.flatten().sortedBy { it.tanggal }
                Pair(list, flat)
            }

            dayAdapter.submit(cells)
            infoAdapter.submit(events)
        }
    }

    private fun tipePriority(tipe: String): Int = when (tipe) {
        "libur" -> 1
        "upacara" -> 2
        "event" -> 3
        else -> 4
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FORM && resultCode == RESULT_OK) renderMonth()
    }

    companion object {
        const val REQ_FORM = 200
    }
}