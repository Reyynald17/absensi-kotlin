package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.FragmentProfileGuruBinding
import com.example.absensisiswaproto.ui.login.LoginActivity

class ProfileGuruFragment : Fragment() {

    private var _binding: FragmentProfileGuruBinding? = null
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
        ): ProfileGuruFragment {
            return ProfileGuruFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_GURU_ID, guruId)
                    putString(ARG_GURU_NIP, guruNip)
                    putString(ARG_GURU_NAMA, guruNama)
                    putString(ARG_GURU_KELAS, guruKelas)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            guruId = it.getInt(ARG_GURU_ID, 0)
            guruNip = it.getString(ARG_GURU_NIP, "")
            guruNama = it.getString(ARG_GURU_NAMA, "")
            guruKelas = it.getString(ARG_GURU_KELAS, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileGuruBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        loadGuruProfile()
    }

    private fun loadGuruProfile() {
        if (guruId <= 0) {
            guruId = GuruSession.getGuruId(requireContext())
        }
        val guru = db.getGuruById(guruId)
        if (guru == null) {
            Toast.makeText(requireContext(), "Data guru tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        binding.ivAvatar.setImageResource(SiswaHelper.getFotoGuru(guru.nama, guru.id))
        binding.tvNama.text = guru.nama.uppercase()
        binding.tvNip.text = guru.nip
        binding.tvNoHp.text = guru.noTelp
        binding.tvEmail.text = guru.email.uppercase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
