package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.FragmentBerandaGuruBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BerandaGuruFragment : Fragment() {

    private var _binding: FragmentBerandaGuruBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper

    private var guruId: Int = 0
    private var guruNip: String = ""
    private var guruNama: String = ""
    private var guruKelas: String = ""

    companion object {
        private const val ARG_GURU_ID = "ARG_GURU_ID"
        private const val ARG_GURU_NIP = "ARG_GURU_NIP"
        private const val ARG_GURU_NAMA = "ARG_GURU_NAMA"
        private const val ARG_GURU_KELAS = "ARG_GURU_KELAS"

        fun newInstance(
            guruId: Int,
            guruNip: String,
            guruNama: String,
            guruKelas: String
        ): BerandaGuruFragment {
            val fragment = BerandaGuruFragment()
            val args = Bundle().apply {
                putInt(ARG_GURU_ID, guruId)
                putString(ARG_GURU_NIP, guruNip)
                putString(ARG_GURU_NAMA, guruNama)
                putString(ARG_GURU_KELAS, guruKelas)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            guruId = it.getInt(ARG_GURU_ID, 0)
            guruNip = it.getString(ARG_GURU_NIP, "")
            guruNama = it.getString(ARG_GURU_NAMA, "Guru")
            guruKelas = it.getString(ARG_GURU_KELAS, "-")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupGreeting()
        setupTanggal()
        setupMenuCards()
    }

    private fun setupGreeting() {
        val firstName = guruNama.split(" ").firstOrNull() ?: guruNama
        val gelar = if (guruNama.contains("Bu", ignoreCase = true) ||
            guruNama.startsWith("Ibu", ignoreCase = true)
        ) "Bu" else "Pak"
        binding.tvGreetingGuru.text = "Halo, $gelar $firstName"

        binding.ivAvatarGuru.setImageResource(SiswaHelper.getFotoGuru(guruNama, guruId))
    }

    private fun setupTanggal() {
        val now = Date()
        val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        binding.tvTanggalGuru.text = format.format(now)
    }

    private fun setupMenuCards() {
        PressEffect.attach(binding.btnCardTanggal)
        PressEffect.attach(binding.btnAbsensiSiswa)
        PressEffect.attach(binding.btnRekapKelas)
        PressEffect.attach(binding.btnNotificationGuru)
        PressEffect.attach(binding.btnScanQr)

        binding.btnCardTanggal.setOnClickListener {
            Toast.makeText(requireContext(), "Pilih tanggal", Toast.LENGTH_SHORT).show()
        }

        binding.btnAbsensiSiswa.setOnClickListener {
            val intent = Intent(requireContext(), AbsensiSiswaActivity::class.java).apply {
                putExtra("EXTRA_GURU_NAMA", guruNama)
                putExtra("EXTRA_GURU_KELAS", guruKelas)
            }
            startActivity(intent)
        }

        binding.btnRekapKelas.setOnClickListener {
            startActivity(Intent(requireContext(), RekapKelasActivity::class.java))
        }

        binding.btnNotificationGuru.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationGuruActivity::class.java))
        }

        binding.btnScanQr.setOnClickListener {
            startActivity(Intent(requireContext(), ScanQrActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}