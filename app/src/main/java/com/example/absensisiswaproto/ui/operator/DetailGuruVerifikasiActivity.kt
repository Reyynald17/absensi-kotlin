package com.example.absensisiswaproto.ui.operator

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityDetailGuruVerifikasiBinding

class DetailGuruVerifikasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailGuruVerifikasiBinding
    private lateinit var db: DatabaseHelper
    private lateinit var dokumenAdapter: DokumenAdapter
    private var guruNip: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailGuruVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        guruNip = intent.getStringExtra("NIP").orEmpty()
        binding.btnBack.setOnClickListener { finish() }

        setupDokumenList()
        setupBottomNav()
        loadGuru()
    }

    private fun setupDokumenList() {
        val dokumenList = listOf(
            "Ijasah S1", "Sertifikat Pendidik", "SK Pengangkatan",
            "KTP", "Dokumen", "Ijasah S1"
        )
        dokumenAdapter = DokumenAdapter(dokumenList) { nama ->
            Toast.makeText(this, "Buka: $nama", Toast.LENGTH_SHORT).show()
        }
        binding.rvDokumen.layoutManager = LinearLayoutManager(this)
        binding.rvDokumen.adapter = dokumenAdapter
    }

    private fun loadGuru() {
        val g = db.getGuruByNip(guruNip) ?: run { finish(); return }
        binding.tvNamaDetail.text = g.nama
        binding.tvNipDetail.text = "NIP. ${g.nip}"
        binding.tvJurusanDetail.text = "Kejuruan: ${OperatorUtils.extractJurusan(g.kelasMapel)}"
        binding.tvSekolahDetail.text = "SMK Negri 9 Medan"

        if (g.status.equals("verified", ignoreCase = true)) {
            binding.tvBadgeDetail.text = "Berhasil verifikasi"
            binding.tvBadgeDetail.setBackgroundResource(R.drawable.bg_badge_terverifikasi)
            binding.tvBadgeDetail.setTextColor(Color.parseColor("#22C55E"))
            binding.btnVerifikasiDetail.visibility = View.GONE
        } else {
            binding.tvBadgeDetail.text = "Menunggu verifikasi"
            binding.tvBadgeDetail.setBackgroundResource(R.drawable.bg_badge_menunggu)
            binding.tvBadgeDetail.setTextColor(Color.parseColor("#B00000"))
            binding.btnVerifikasiDetail.visibility = View.VISIBLE
            binding.btnVerifikasiDetail.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Verifikasi Guru")
                    .setMessage("Yakin memverifikasi ${g.nama}?")
                    .setPositiveButton("Verifikasi") { _, _ ->
                        val ok = db.updateStatusGuruById(g.id, "verified")
                        if (ok) {
                            Toast.makeText(this, "Berhasil diverifikasi", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal verifikasi", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }
    }

    private fun setupBottomNav() {
        binding.bottomNav.btnNavAgenda.setOnClickListener {
            startActivity(Intent(this, AgendaEventActivity::class.java)); finish()
        }
        binding.bottomNav.btnNavBeranda.setOnClickListener {
            startActivity(Intent(this, BerandaOperatorActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish()
        }
        binding.bottomNav.btnNavProfil.setOnClickListener {
            startActivity(Intent(this, ProfileOperatorActivity::class.java))
        }
    }
}
