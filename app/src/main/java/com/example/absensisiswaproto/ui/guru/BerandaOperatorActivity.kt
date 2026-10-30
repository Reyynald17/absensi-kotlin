package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityBerandaOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BerandaOperatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBerandaOperatorBinding
    private lateinit var db: DatabaseHelper
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBerandaOperatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        setupRecycler()
        setupClickable()
        loadDataAsync()
    }

    private fun setupRecycler() {
        eventAdapter = EventAdapter { event ->
            Toast.makeText(this, "Detail: ${event.judul}", Toast.LENGTH_SHORT).show()
        }
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = eventAdapter
    }

    private fun setupClickable() {
        PressEffect.attach(binding.cardVerifikasi)
        PressEffect.attach(binding.cardKalender)
        PressEffect.attach(binding.btnMenu)
        PressEffect.attach(binding.btnNavAgenda)
        PressEffect.attach(binding.btnNavBeranda)
        PressEffect.attach(binding.btnNavProfil)

        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Menu segera hadir", Toast.LENGTH_SHORT).show()
        }
        binding.cardVerifikasi.setOnClickListener {
            startActivity(Intent(this, VerifikasiGuruActivity::class.java))
        }
        binding.cardKalender.setOnClickListener {
            startActivity(Intent(this, KalenderOperatorActivity::class.java))
        }
        binding.btnNavAgenda.setOnClickListener {
            startActivity(Intent(this, AgendaEventActivity::class.java))
        }
        binding.btnNavBeranda.setOnClickListener {
            // Already on Beranda Operator
        }
        binding.btnNavProfil.setOnClickListener {
            startActivity(Intent(this, ProfileOperatorActivity::class.java))
        }
    }

    private fun loadDataAsync() {
        lifecycleScope.launch {
            val stats = withContext(Dispatchers.IO) {
                Triple(
                    db.countGuruTotal(),
                    db.countGuruPending(),
                    db.countGuruVerified()
                )
            }
            val events = withContext(Dispatchers.IO) {
                db.getEventMendatang()
            }

            binding.tvStatTotal.text = stats.first.toString()
            binding.tvStatPending.text = stats.second.toString()
            binding.tvStatVerified.text = stats.third.toString()
            binding.tvCountPending.text = "${stats.second} Menunggu Verifikasi"

            binding.tvCountEvent.text = "${events.size} Event Bulan ini"
            eventAdapter.submit(events)
        }
    }
}
