package com.example.absensisiswaproto.ui.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.databinding.FragmentRiwayatBinding
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: RiwayatAdapter

    private var userId: Int = 0
    private var userNama: String = ""

    private var isDropdownOpen = false
    private var selectedMonth: String = "Semua Bulan"
    private var allRiwayat: List<Absensi> = emptyList()

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"
        private const val ARG_USER_NAMA = "ARG_USER_NAMA"

        fun newInstance(userId: Int, userNis: String, userNama: String, userRole: String, userKelas: String): RiwayatFragment {
            return RiwayatFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                    putString(ARG_USER_NAMA, userNama)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID, 0)
            userNama = it.getString(ARG_USER_NAMA, "Siswa")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupHeader()
        setupRecyclerView()
        setupDropdownKalender()
        setupBottomNav()

        loadRiwayat()
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_HOME)
        }
    }

    private fun setupRecyclerView() {
        adapter = RiwayatAdapter(emptyList())
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
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
        // Handled by SiswaMainActivity bottom nav
    }

    private fun loadRiwayat() {
        setupMonthButtons()
        val dataDb = db.getRiwayatAbsen(userId)
        allRiwayat = RiwayatProvider.getAll(userId, dataDb)
        selectedMonth = "Semua Bulan"
        binding.tvFilterKalender.text = "Semua Bulan"
        applyFilter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
