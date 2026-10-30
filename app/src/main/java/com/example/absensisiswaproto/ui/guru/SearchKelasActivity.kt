package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivitySearchKelasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchKelasActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchKelasBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: KelasSearchAdapter

    private var allKelas: List<KelasSearchItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchKelasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        setupHeader()
        setupSearch()
        setupRecyclerView()
        setupBottomNav()

        loadKelasAsync()
    }

    private fun setupHeader() {
        binding.btnBackSearch.setOnClickListener { finish() }
    }

    private fun setupSearch() {
        binding.etSearchKelas.doAfterTextChanged { editable ->
            filterKelas(editable?.toString().orEmpty())
        }
    }

    private fun setupRecyclerView() {
        adapter = KelasSearchAdapter(emptyList()) { kelas ->
            val intent = Intent(this, SearchKelasDetailActivity::class.java).apply {
                putExtra("EXTRA_KELAS", kelas.nama)
            }
            startActivity(intent)
        }
        binding.rvKelasSearch.layoutManager = LinearLayoutManager(this)
        binding.rvKelasSearch.adapter = adapter
    }

    private fun loadKelasAsync() {
        lifecycleScope.launch {
            val raw = withContext(Dispatchers.IO) {
                db.getAllKelasWithCount()
            }
            allKelas = raw.map { KelasSearchItem(it.first, it.second) }
            filterKelas(binding.etSearchKelas.text.toString())
        }
    }

    private fun filterKelas(keyword: String) {
        val filtered = if (keyword.isBlank()) {
            allKelas
        } else {
            allKelas.filter {
                it.nama.contains(keyword, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.layoutEmptySearch.visibility = View.VISIBLE
            binding.rvKelasSearch.visibility = View.GONE
        } else {
            binding.layoutEmptySearch.visibility = View.GONE
            binding.rvKelasSearch.visibility = View.VISIBLE
        }
    }

    private fun setupBottomNav() {
        binding.btnSearchGuru.setImageResource(com.example.absensisiswaproto.R.drawable.ic_qr_scan)
        binding.btnHomeGuru.setOnClickListener {
            val intent = Intent(this, GuruMainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_OPEN_TAB", GuruMainActivity.TAB_HOME)
            }
            startActivity(intent)
        }
        binding.btnSearchGuru.setOnClickListener {
            startActivity(Intent(this, ScanQrActivity::class.java))
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
