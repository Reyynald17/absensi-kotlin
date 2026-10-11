package com.example.absensisiswaproto.ui.operator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.databinding.ItemEventOperatorBinding
import com.example.absensisiswaproto.ui.component.PressEffect
import java.text.SimpleDateFormat
import java.util.Locale

data class EventItem(
    val judul: String,
    val tanggal: String,
    val jamMulai: String,
    val jamSelesai: String,
    val lokasi: String
)

class EventAdapter(
    private var items: List<EventItem> = emptyList(),
    private val onClick: (EventItem) -> Unit = {}
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    fun submit(data: List<EventItem>) {
        items = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemEventOperatorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemEventOperatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvDateDay.text = formatDay(item.tanggal)
        holder.binding.tvEventTitle.text = item.judul
        holder.binding.tvEventTime.text = "${item.jamMulai} - ${item.jamSelesai}"
        holder.binding.tvEventLocation.text = item.lokasi
        holder.binding.root.setOnClickListener { onClick(item) }
        PressEffect.attach(holder.binding.root)
    }

    override fun getItemCount(): Int = items.size

    private fun formatDay(yyyymmdd: String): String {
        return try {
            val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFmt = SimpleDateFormat("dd\nMMM", Locale("id", "ID"))
            val d = inFmt.parse(yyyymmdd)
            outFmt.format(d!!).uppercase()
        } catch (e: Exception) {
            "—"
        }
    }
}