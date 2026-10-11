package com.example.absensisiswaproto.ui.operator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityFormEventBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FormEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormEventBinding
    private lateinit var db: DatabaseHelper

    private var mode: String = "add"
    private var eventId: Int = -1
    private var selectedDate: String = ""
    private var waktuMulai: String = ""
    private var waktuSelesai: String = ""
    private var selectedKategori: String = "Libur"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHelper(this)

        mode = intent.getStringExtra("MODE") ?: "add"
        eventId = intent.getIntExtra("EVENT_ID", -1)

        binding.btnBack.setOnClickListener { finish() }
        PressEffect.attach(binding.btnSimpan)
        PressEffect.attach(binding.btnHapus)

        setupKategoriDropdown()
        setupDatePicker()
        setupTimePicker()
        setupButtons()

        if (mode == "add") setupAddMode() else setupEditMode()
    }

    private fun setupAddMode() {
        binding.tvHeaderTitle.text = "Tambahkan Event"
        binding.btnSimpan.text = "Tambahkan Event"
        binding.btnHapus.visibility = View.GONE

        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
        binding.tvTanggal.text = OperatorUtils.formatTanggalIndo(selectedDate)
        binding.tvWaktu.text = "Pilih waktu"
    }

    private fun setupEditMode() {
        binding.tvHeaderTitle.text = "Edit Event"
        binding.btnSimpan.text = "Simpan Perubahan"
        binding.btnHapus.visibility = View.VISIBLE

        val event = db.getEventById(eventId) ?: run { finish(); return }
        binding.etJudul.setText(event.judul)
        binding.etLokasi.setText(event.lokasi.orEmpty())
        binding.etDeskripsi.setText(event.deskripsi.orEmpty())
        selectedDate = event.tanggal
        binding.tvTanggal.text = OperatorUtils.formatTanggalIndo(event.tanggal)
        waktuMulai = event.waktuMulai.orEmpty()
        waktuSelesai = event.waktuSelesai.orEmpty()
        binding.tvWaktu.text = if (waktuMulai.isNotBlank() && waktuSelesai.isNotBlank())
            "$waktuMulai - $waktuSelesai" else "Pilih waktu"
        selectedKategori = event.tipe.replaceFirstChar { it.uppercase() }
        binding.spKategori.setText(selectedKategori, false)
    }

    private fun setupKategoriDropdown() {
        val list = listOf(
            KategoriItem("Libur", R.drawable.bg_legend_circle_red),
            KategoriItem("Upacara", R.drawable.bg_legend_circle_green),
            KategoriItem("Event", R.drawable.bg_legend_circle_blue)
        )
        binding.spKategori.setAdapter(KategoriAdapter(this, list))
        binding.spKategori.setOnItemClickListener { _, _, position, _ ->
            selectedKategori = list[position].nama
        }
        binding.spKategori.setText("Libur", false)
    }

    private fun setupDatePicker() {
        binding.tvTanggal.setOnClickListener {
            val cal = Calendar.getInstance()
            if (selectedDate.isNotBlank()) {
                val p = selectedDate.split("-")
                if (p.size == 3) {
                    cal.set(p[0].toInt(), p[1].toInt() - 1, p[2].toInt())
                }
            }
            DatePickerDialog(this, { _, y, m, d ->
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d)
                binding.tvTanggal.text = OperatorUtils.formatTanggalIndo(selectedDate)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePicker() {
        binding.tvWaktu.setOnClickListener {
            TimePickerDialog(this, { _, sh, sm ->
                val start = String.format(Locale.getDefault(), "%02d:%02d", sh, sm)
                TimePickerDialog(this, { _, eh, em ->
                    val end = String.format(Locale.getDefault(), "%02d:%02d", eh, em)
                    if (toMin(end) <= toMin(start)) {
                        Toast.makeText(this, "Jam selesai harus setelah jam mulai",
                            Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }
                    waktuMulai = start; waktuSelesai = end
                    binding.tvWaktu.text = "$start - $end"
                }, sh, sm + 30, true).show()
            }, 8, 0, true).show()
        }
    }

    private fun setupButtons() {
        binding.btnSimpan.setOnClickListener { save() }
        binding.btnHapus.setOnClickListener { confirmDelete() }
    }

    private fun save() {
        val judul = binding.etJudul.text.toString().trim()
        val lokasi = binding.etLokasi.text.toString().trim()
        val deskripsi = binding.etDeskripsi.text.toString().trim()

        if (judul.length < 3) {
            Toast.makeText(this, "Judul minimal 3 karakter", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDate.isBlank()) {
            Toast.makeText(this, "Pilih tanggal", Toast.LENGTH_SHORT).show()
            return
        }
        if (waktuMulai.isBlank() || waktuSelesai.isBlank()) {
            Toast.makeText(this, "Pilih waktu", Toast.LENGTH_SHORT).show()
            return
        }
        val tipe = selectedKategori.lowercase()

        val ok = if (mode == "add") {
            db.insertEvent(selectedDate, judul, tipe, waktuMulai, waktuSelesai, lokasi, deskripsi)
        } else {
            db.updateEvent(eventId, selectedDate, judul, tipe, waktuMulai, waktuSelesai, lokasi, deskripsi)
        }

        if (ok) {
            Toast.makeText(this, if (mode == "add") "Event ditambahkan" else "Event diperbarui",
                Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK); finish()
        } else {
            Toast.makeText(this, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Event").setMessage("Yakin ingin menghapus event ini?")
            .setPositiveButton("Hapus") { _, _ ->
                db.deleteEvent(eventId)
                Toast.makeText(this, "Event dihapus", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK); finish()
            }
            .setNegativeButton("Batal", null).show()
    }

    private fun toMin(hhmm: String): Int {
        val p = hhmm.split(":")
        return p[0].toInt() * 60 + p[1].toInt()
    }
}