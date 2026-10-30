package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.databinding.ActivityRekapKelasBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RekapKelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRekapKelasBinding

    private var tanggalFilter: String = ""

    private val jurusanList = listOf("DKV", "RPL", "TKJ", "PSPT", "PEKSOS", "ANIMASI")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRekapKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        tanggalFilter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        setupHeader()
        setupDatePicker()
        setupRecyclerView()
        setupBottomNav()
    }

    private fun setupHeader() {
        binding.btnBackRekap.setOnClickListener { finish() }
    }

    private fun setupDatePicker() {
        updateTanggalText()

        binding.cardTanggalRekap.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Rekap")
                .setSelection(parseTanggalToMillis(tanggalFilter))
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                tanggalFilter = formatter.format(Date(selection))
                updateTanggalText()
            }

            picker.show(supportFragmentManager, "date_picker_rekap")
        }
    }

    private fun updateTanggalText() {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val date = parser.parse(tanggalFilter)
        binding.tvTanggalRekap.text = if (date != null) formatter.format(date) else tanggalFilter
    }

    private fun parseTanggalToMillis(tanggal: String): Long {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.parse(tanggal)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun setupRecyclerView() {
        val adapter = JurusanAdapter(jurusanList) { jurusan ->
            val intent = Intent(this, RekapKelasDetailActivity::class.java).apply {
                putExtra("EXTRA_JURUSAN", jurusan)
                putExtra("EXTRA_TANGGAL", tanggalFilter)
            }
            startActivity(intent)
        }
        binding.rvJurusan.layoutManager = LinearLayoutManager(this)
        binding.rvJurusan.adapter = adapter
    }

    private fun setupBottomNav() {
        binding.btnHomeGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_HOME)
            }
            startActivity(intent)
        }
        binding.btnSearchGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_SEARCH)
            }
            startActivity(intent)
        }
        binding.btnProfileGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_PROFILE)
            }
            startActivity(intent)
        }
    }
}
