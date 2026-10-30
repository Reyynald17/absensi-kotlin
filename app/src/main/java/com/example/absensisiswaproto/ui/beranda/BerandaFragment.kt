package com.example.absensisiswaproto.ui.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.FragmentBerandaBinding
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"
        private const val ARG_USER_NIS = "ARG_USER_NIS"
        private const val ARG_USER_NAMA = "ARG_USER_NAMA"
        private const val ARG_USER_ROLE = "ARG_USER_ROLE"
        private const val ARG_USER_KELAS = "ARG_USER_KELAS"

        fun newInstance(
            userId: Int,
            userNis: String,
            userNama: String,
            userRole: String,
            userKelas: String
        ): BerandaFragment {
            val fragment = BerandaFragment()
            val args = Bundle().apply {
                putInt(ARG_USER_ID, userId)
                putString(ARG_USER_NIS, userNis)
                putString(ARG_USER_NAMA, userNama)
                putString(ARG_USER_ROLE, userRole)
                putString(ARG_USER_KELAS, userKelas)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID, 0)
            userNis = it.getString(ARG_USER_NIS, "")
            userNama = it.getString(ARG_USER_NAMA, "Siswa")
            userRole = it.getString(ARG_USER_ROLE, "siswa")
            userKelas = it.getString(ARG_USER_KELAS, "-")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupHeader()
        setupCardKehadiran()
        setupAbsenHariIni()
        setupMenuCards()
        updateStatusTombolAbsen()
    }



    private fun setupHeader() {
        binding.headerLayout.btnNotification.setOnClickListener {
            val intent = android.content.Intent(requireContext(), com.example.absensisiswaproto.ui.notifikasi.NotificationActivity::class.java).apply {
                putExtra("EXTRA_USER_ID", userId)
                putExtra("EXTRA_USER_NIS", userNis)
                putExtra("EXTRA_USER_NAMA", userNama)
                putExtra("EXTRA_USER_ROLE", userRole)
                putExtra("EXTRA_USER_KELAS", userKelas)
            }
            startActivity(intent)
        }
    }

    private fun setupCardKehadiran() {
        val card = binding.cardKehadiran

        val now = Date()
        val locale = Locale("id", "ID")

        card.tvTanggal.text = SimpleDateFormat("dd", locale).format(now)
        card.tvHari.text = SimpleDateFormat("EEEE", locale).format(now)
        card.tvBulanTahun.text = SimpleDateFormat("MMMM/yyyy", locale).format(now)

        val circleIds = listOf(
            R.id.ivStatus1,
            R.id.ivStatus2,
            R.id.ivStatus3,
            R.id.ivStatus4,
            R.id.ivStatus5
        )

        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        circleIds.forEachIndexed { _, id ->
            val view = card.root.findViewById<ImageView>(id)
            val tanggalHariIni = dateFmt.format(cal.time)

            val absensi = db.getAbsensiByTanggal(userId, tanggalHariIni)

            val bgRes = when (absensi?.status?.lowercase()) {
                "hadir" -> R.drawable.bg_status_hadir
                "telat" -> R.drawable.bg_status_telat
                "absen", "alpha" -> R.drawable.bg_icon_status_alpha
                else -> R.drawable.bg_status_kosong
            }

            view.setBackgroundResource(bgRes)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun setupAbsenHariIni() {
        val absen = binding.absenHariIni

        val now = Date()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        absen.tvTanggalAbsen.text = format.format(now)

        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggalHariIni = dateFmt.format(now)

        val absensiHariIni = db.getAbsensiByTanggal(userId, tanggalHariIni)

        val bgRes = when (absensiHariIni?.status?.lowercase()) {
            "hadir" -> R.drawable.bg_status_hadir
            "telat" -> R.drawable.bg_status_telat
            "alpha", "absen" -> R.drawable.bg_status_alpha_small
            else -> R.drawable.bg_status_kosong
        }

        absen.ivStatusAbsen.setBackgroundResource(bgRes)
    }

    private fun setupMenuCards() {
        val grid = binding.menuGrid

        grid.menuRiwayat.ivMenuIcon.setImageResource(R.drawable.ic_history)
        grid.menuRiwayat.tvMenuLabel.text = "Riwayat Hadir"
        grid.menuRiwayat.root.setOnClickListener {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_RIWAYAT)
        }

        grid.menuPelajaran.ivMenuIcon.setImageResource(R.drawable.ic_book)
        grid.menuPelajaran.tvMenuLabel.text = "Pelajaran"
        grid.menuPelajaran.root.setOnClickListener {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_PELAJARAN)
        }

        grid.menuAbsen.ivMenuIcon.setImageResource(R.drawable.ic_absen)
        grid.menuAbsen.tvMenuLabel.text = "Absen"
        grid.menuAbsen.root.setOnClickListener {
            val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val tanggalHariIni = dateFmt.format(Date())
            val absensiHariIni = db.getAbsensiByTanggal(userId, tanggalHariIni)

            if (db.isHariLibur()) {
                Toast.makeText(requireContext(), "Absen hanya bisa Senin - Jumat", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (absensiHariIni != null || db.sudahAbsenHariIni(userId)) {
                Toast.makeText(requireContext(), "Anda sudah absen hari ini", Toast.LENGTH_SHORT).show()
                updateStatusTombolAbsen()
                return@setOnClickListener
            }

            val btnView = it
            btnView.isEnabled = false

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val success = withContext(Dispatchers.IO) {
                        val check = db.getAbsensiByTanggal(userId, tanggalHariIni)
                        if (check == null && !db.sudahAbsenHariIni(userId)) {
                            db.simpanAbsen(userId, "fingerprint")
                        } else false
                    }
                    if (_binding != null) {
                        if (success) {
                            Toast.makeText(requireContext(), "Absen berhasil, $userNama!", Toast.LENGTH_SHORT).show()
                            refreshData()
                            updateStatusTombolAbsen()
                        } else {
                            Toast.makeText(requireContext(), "Anda sudah absen hari ini", Toast.LENGTH_SHORT).show()
                            updateStatusTombolAbsen()
                        }
                    }
                } catch (e: Exception) {
                    if (_binding != null) {
                        Toast.makeText(requireContext(), "Absen gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    if (_binding != null) {
                        updateStatusTombolAbsen()
                    }
                }
            }
        }

        grid.menuKalender.ivMenuIcon.setImageResource(R.drawable.ic_kalendar)
        grid.menuKalender.tvMenuLabel.text = "Kalender"
        grid.menuKalender.root.setOnClickListener {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_KALENDER)
        }
    }

    fun refreshData() {
        setupCardKehadiran()
        setupAbsenHariIni()
        updateStatusTombolAbsen()
    }

    private fun updateStatusTombolAbsen() {
        if (_binding == null) return
        val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val tanggalHariIni = dateFmt.format(Date())
        val absensiHariIni = db.getAbsensiByTanggal(userId, tanggalHariIni)
        val sudahAbsen = absensiHariIni != null || db.sudahAbsenHariIni(userId)

        val absenCard = binding.menuGrid.menuAbsen
        if (sudahAbsen) {
            absenCard.root.isEnabled = false
            absenCard.root.alpha = 0.5f
            absenCard.tvMenuLabel.text = "Sudah Absen"
        } else {
            absenCard.root.isEnabled = true
            absenCard.root.alpha = 1.0f
            absenCard.tvMenuLabel.text = "Absen"
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
