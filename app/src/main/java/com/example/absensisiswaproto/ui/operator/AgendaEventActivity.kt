package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.model.Event
import com.example.absensisiswaproto.databinding.ActivityAgendaEventBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgendaEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgendaEventBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: AgendaEventAdapter

    private var allEvents: List<Event> = emptyList()
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAgendaEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        binding.btnBack.setOnClickListener { finish() }

        setupRecycler()
        setupSearch()
        setupFAB()
        setupBottomNav()

        loadEvents()
    }

    private fun setupRecycler() {
        adapter = AgendaEventAdapter(
            onEdit = { ev -> openFormEdit(ev) },
            onDelete = { ev -> confirmDelete(ev) }
        )
        binding.rvAgenda.layoutManager = LinearLayoutManager(this)
        binding.rvAgenda.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { e ->
            currentQuery = e?.toString().orEmpty()
            applyFilter()
        }
    }

    private fun setupFAB() {
        PressEffect.attach(binding.fabAddEvent)
        binding.fabAddEvent.setOnClickListener { openFormAdd() }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener { }
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

    private fun loadEvents() {
        lifecycleScope.launch {
            allEvents = withContext(Dispatchers.IO) { db.getAllEvents() }
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = if (currentQuery.isBlank()) allEvents
        else allEvents.filter {
            it.judul.contains(currentQuery, ignoreCase = true) ||
            (it.lokasi?.contains(currentQuery, ignoreCase = true) == true)
        }

        binding.tvTotal.text = "${filtered.size} event terdaftar"
        adapter.submit(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openFormAdd() {
        @Suppress("DEPRECATION")
        startActivityForResult(
            Intent(this, FormEventActivity::class.java).putExtra("MODE", "add"),
            REQ_FORM
        )
    }

    private fun openFormEdit(ev: Event) {
        @Suppress("DEPRECATION")
        startActivityForResult(
            Intent(this, FormEventActivity::class.java)
                .putExtra("MODE", "edit")
                .putExtra("EVENT_ID", ev.id),
            REQ_FORM
        )
    }

    private fun confirmDelete(ev: Event) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Event")
            .setMessage("Yakin ingin menghapus \"${ev.judul}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    val ok = withContext(Dispatchers.IO) { db.deleteEvent(ev.id) }
                    if (ok) {
                        Toast.makeText(
                            this@AgendaEventActivity,
                            "Event dihapus", Toast.LENGTH_SHORT
                        ).show()
                        loadEvents()
                    } else {
                        Toast.makeText(
                            this@AgendaEventActivity,
                            "Gagal menghapus", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FORM && resultCode == RESULT_OK) loadEvents()
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    companion object {
        const val REQ_FORM = 300
    }
}
