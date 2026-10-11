package com.example.absensisiswaproto.ui.operator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.data.model.Event
import com.example.absensisiswaproto.databinding.ItemAgendaEventBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import java.text.SimpleDateFormat
import java.util.Locale

class AgendaEventAdapter(
    private var items: List<Event> = emptyList(),
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<AgendaEventAdapter.VH>() {

    fun submit(data: List<Event>) {
        items = data
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemAgendaEventBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemAgendaEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ev = items[position]
        val b = holder.b

        try {
            val inF = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dayF = SimpleDateFormat("dd", Locale.getDefault())
            val monF = SimpleDateFormat("MMM", Locale("id", "ID"))
            val d = inF.parse(ev.tanggal)!!
            b.tvDay.text = dayF.format(d)
            b.tvMonth.text = monF.format(d).uppercase()
        } catch (e: Exception) {
            b.tvDay.text = if (ev.tanggal.length >= 10) ev.tanggal.substring(8, 10) else ev.tanggal
            b.tvMonth.text = "-"
        }

        b.tvJudul.text = ev.judul
        b.tvWaktu.text = "${ev.waktuMulai ?: "-"} - ${ev.waktuSelesai ?: "-"}"
        b.tvLokasi.text = ev.lokasi?.ifBlank { "-" } ?: "-"

        when (ev.tipe) {
            "libur" -> {
                b.tvKategori.text = "Libur"
                b.tvKategori.setBackgroundColor(Color.parseColor("#D40000"))
            }
            "upacara" -> {
                b.tvKategori.text = "Upacara"
                b.tvKategori.setBackgroundColor(Color.parseColor("#22C55E"))
            }
            else -> {
                b.tvKategori.text = "Event"
                b.tvKategori.setBackgroundColor(Color.parseColor("#2563EB"))
            }
        }

        b.btnEdit.setOnClickListener { onEdit(ev) }
        b.btnDelete.setOnClickListener { onDelete(ev) }
        b.root.setOnClickListener { onEdit(ev) }
        PressEffect.attach(b.root)
        PressEffect.attach(b.btnEdit)
        PressEffect.attach(b.btnDelete)
    }

    override fun getItemCount() = items.size
}