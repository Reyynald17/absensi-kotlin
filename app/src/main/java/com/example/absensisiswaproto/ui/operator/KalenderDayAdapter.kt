package com.example.absensisiswaproto.ui.operator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.databinding.ItemKalenderDayBinding

data class DayCell(
    val day: Int = 0,
    val tanggal: String = "",
    val tipe: String? = null,
    val isWeekend: Boolean = false,
    val empty: Boolean = false
)

class KalenderDayAdapter(
    private var items: List<DayCell> = emptyList()
) : RecyclerView.Adapter<KalenderDayAdapter.VH>() {

    fun submit(data: List<DayCell>) {
        items = data
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemKalenderDayBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemKalenderDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cell = items[position]
        val b = holder.b

        if (cell.empty) {
            b.tvDay.text = ""
            return
        }

        b.tvDay.text = cell.day.toString()

        val color = when {
            cell.tipe == "libur" -> Color.parseColor("#D40000")
            cell.tipe == "upacara" -> Color.parseColor("#22C55E")
            cell.tipe == "event" -> Color.parseColor("#2563EB")
            cell.isWeekend -> Color.parseColor("#D40000")
            else -> Color.parseColor("#1A1A1A")
        }
        b.tvDay.setTextColor(color)
    }

    override fun getItemCount() = items.size
}