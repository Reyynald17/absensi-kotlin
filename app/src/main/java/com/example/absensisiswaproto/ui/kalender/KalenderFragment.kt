package com.example.absensisiswaproto.ui.kalender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.FragmentKalenderBinding
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class KalenderFragment : Fragment() {

    private var _binding: FragmentKalenderBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var monthAdapter: MonthAdapter
    private lateinit var dayAdapter: CalendarDaysAdapter

    private var userId: Int = 0

    private var currentSelectedMonth: Int = 0
    private val currentYear = 2026

    private val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"

        fun newInstance(userId: Int, userNis: String, userNama: String, userRole: String, userKelas: String): KalenderFragment {
            return KalenderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt(ARG_USER_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupHeader()
        setupMonthGrid()
        setupCalendarGrid()
        setupMonthNavigation()

        showMonthGridView()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            if (binding.layoutCalendarDetail.visibility == View.VISIBLE) {
                showMonthGridView()
            } else {
                (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_HOME)
            }
        }
    }

    private fun setupMonthGrid() {
        monthAdapter = MonthAdapter(monthNames.toList()) { month ->
            val index = monthNames.indexOf(month)
            if (index >= 0) showCalendarDetailView(index)
        }
        binding.rvMonths.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMonths.adapter = monthAdapter
    }

    private fun showMonthGridView() {
        binding.layoutMonthGrid.visibility = View.VISIBLE
        binding.layoutCalendarDetail.visibility = View.GONE
        binding.tvHeaderTitle.text = "Kalender"
    }

    private fun setupCalendarGrid() {
        dayAdapter = CalendarDaysAdapter(emptyList())
        binding.rvCalendarDays.layoutManager = GridLayoutManager(requireContext(), 7)
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

        viewLifecycleOwner.lifecycleScope.launch {
            val mapEvent = withContext(Dispatchers.IO) {
                db.getAllEventBulan(year, month + 1)
            }

            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, 1)

            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

            val dayItems = mutableListOf<DayItem>()
            val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

            for (i in 0 until offset) {
                dayItems.add(DayItem(day = ""))
            }

            val today = Calendar.getInstance()
            val todayDay = today.get(Calendar.DAY_OF_MONTH)
            val todayMonth = today.get(Calendar.MONTH)
            val todayYear = today.get(Calendar.YEAR)

            for (day in 1..daysInMonth) {
                cal.set(Calendar.DAY_OF_MONTH, day)
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val isSunday = dayOfWeek == Calendar.SUNDAY
                val isToday = (day == todayDay && month == todayMonth && year == todayYear)

                val eventsHariIni = mapEvent[day] ?: emptyList()
                val tipeTertinggi = eventsHariIni.minByOrNull { tipePriority(it.tipe) }?.tipe

                val isLibur = isSunday || tipeTertinggi == "libur"
                val isUpacara = !isLibur && tipeTertinggi == "upacara"
                val isEvent = !isLibur && !isUpacara && tipeTertinggi == "event"
                val isGreen = !isLibur && !isUpacara && !isEvent && isToday

                dayItems.add(
                    DayItem(
                        day = day.toString(),
                        isRed = isLibur,
                        isGreen = isUpacara || isGreen,
                        isBlue = isEvent
                    )
                )
            }

            dayAdapter.updateData(dayItems)
            renderEventList(mapEvent)
        }
    }

    private fun tipePriority(tipe: String): Int = when (tipe.lowercase()) {
        "libur" -> 1
        "upacara" -> 2
        "event" -> 3
        else -> 4
    }

    private fun renderEventList(mapEvent: Map<Int, List<com.example.absensisiswaproto.data.model.Event>>) {
        val container = binding.layoutEventList
        container.removeAllViews()

        val allEvents = mapEvent.toSortedMap().flatMap { it.value }

        if (allEvents.isEmpty()) {
            val tv = TextView(requireContext()).apply {
                text = "Tidak ada event bulan ini"
                setTextColor(0xFFB0B0B0.toInt())
                textSize = 11f
            }
            container.addView(tv)
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for (ev in allEvents) {
            val item = inflater.inflate(R.layout.item_event, container, false)
            val dayNumber = ev.tanggal.substring(8, 10).trimStart('0')
            item.findViewById<TextView>(R.id.tvEventNumber).text = dayNumber
            item.findViewById<TextView>(R.id.tvEventTitle).text = ev.judul
            container.addView(item)
        }
    }

    private fun setupMonthNavigation() {
        binding.btnPrevMonth.setOnClickListener {
            if (currentSelectedMonth > 0) {
                showCalendarDetailView(currentSelectedMonth - 1)
            } else {
                Toast.makeText(requireContext(), "Sudah bulan pertama", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNextMonth.setOnClickListener {
            if (currentSelectedMonth < 11) {
                showCalendarDetailView(currentSelectedMonth + 1)
            } else {
                Toast.makeText(requireContext(), "Sudah bulan terakhir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.layoutCalendarDetail.visibility == View.VISIBLE) {
            renderMonthlyCalendar(currentSelectedMonth, currentYear)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
