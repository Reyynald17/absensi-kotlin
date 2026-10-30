package com.example.absensisiswaproto.ui.pelajaran

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.databinding.FragmentPelajaranBinding
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class PelajaranFragment : Fragment() {

    private var _binding: FragmentPelajaranBinding? = null
    private val binding get() = _binding!!

    private var currentStage: String = "X"
    private val majors = listOf("DKV", "RPL", "TKJ", "AN", "PSPT", "PS")
    private val locals = listOf("1", "2", "3")

    companion object {
        fun newInstance(userId: Int, userNis: String, userNama: String, userRole: String, userKelas: String): PelajaranFragment {
            return PelajaranFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPelajaranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.overlayJadwal.root.visibility = View.GONE

        setupBlurEffect()
        setupHeader()
        setupTabs()
        setupSearch()
        setupOverlayJadwal()

        renderKelasList()
    }

    private fun setupBlurEffect() {
        val blurView: BlurView = binding.overlayJadwal.blurView
        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private fun setupHeader() {
        binding.btnBack.setOnClickListener {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_HOME)
        }
    }

    private fun setupTabs() {
        binding.tabX.setOnClickListener { switchStage("X") }
        binding.tabXI.setOnClickListener { switchStage("XI") }
        binding.tabXII.setOnClickListener { switchStage("XII") }
    }

    private fun switchStage(stage: String) {
        currentStage = stage

        binding.tabX.setBackgroundResource(
            if (stage == "X") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabX.setTextColor(
            if (stage == "X") requireContext().getColor(R.color.color_white) else requireContext().getColor(R.color.color_primary_dark)
        )

        binding.tabXI.setBackgroundResource(
            if (stage == "XI") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabXI.setTextColor(
            if (stage == "XI") requireContext().getColor(R.color.color_white) else requireContext().getColor(R.color.color_primary_dark)
        )

        binding.tabXII.setBackgroundResource(
            if (stage == "XII") R.drawable.bg_tab_active else R.drawable.bg_tab_inactive
        )
        binding.tabXII.setTextColor(
            if (stage == "XII") requireContext().getColor(R.color.color_white) else requireContext().getColor(R.color.color_primary_dark)
        )

        renderKelasList()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderKelasList(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun renderKelasList(filter: String = "") {
        val container = binding.containerKelas
        container.removeAllViews()

        val list = mutableListOf<String>()
        for (major in majors) {
            for (local in locals) {
                val kelas = "$currentStage $major $local"
                if (filter.isEmpty() || kelas.contains(filter, ignoreCase = true)) {
                    list.add(kelas)
                }
            }
        }

        val inflater = LayoutInflater.from(requireContext())
        for (namaKelas in list) {
            val item = inflater.inflate(R.layout.item_kelas, container, false)
            item.findViewById<TextView>(R.id.tvNamaKelas).text = namaKelas

            item.setOnClickListener {
                bukaJadwal(namaKelas)
            }

            container.addView(item)
        }
    }

    private fun bukaJadwal(namaKelas: String) {
        binding.overlayJadwal.root.visibility = View.VISIBLE
        binding.overlayJadwal.root.bringToFront()
        Toast.makeText(requireContext(), "Jadwal $namaKelas", Toast.LENGTH_SHORT).show()
    }

    private fun setupOverlayJadwal() {
        binding.overlayJadwal.btnTutupJadwal.setOnClickListener {
            binding.overlayJadwal.root.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
