package com.example.absensisiswaproto.ui.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.Absensi
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatAdapter(
    private var list: List<Absensi>
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutStatusBadge: LinearLayout = view.findViewById(R.id.layoutStatusBadge)
        val ivItemStatusIcon: ImageView = view.findViewById(R.id.ivItemStatusIcon)
        val tvItemStatusLabel: TextView = view.findViewById(R.id.tvItemStatusLabel)
        val tvItemTanggal: TextView = view.findViewById(R.id.tvItemTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_absen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val statusLower = item.status.lowercase(Locale.getDefault())

        when (statusLower) {
            "hadir" -> {
                holder.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                holder.ivItemStatusIcon.setImageResource(R.drawable.ic_check)
                holder.tvItemStatusLabel.text = "Hadir"
            }
            "telat" -> {
                holder.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_telat_pill)
                holder.ivItemStatusIcon.setImageResource(R.drawable.ic_warning)
                holder.tvItemStatusLabel.text = "Telat"
            }
            "absen", "alpha" -> {
                holder.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_alpha_pill)
                holder.ivItemStatusIcon.setImageResource(R.drawable.ic_remove)
                holder.tvItemStatusLabel.text = "Absen"
            }
            else -> {
                holder.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                holder.ivItemStatusIcon.setImageResource(R.drawable.ic_check)
                holder.tvItemStatusLabel.text = "Hadir"
            }
        }

        holder.tvItemTanggal.text = formatTanggal(item.tanggal)
    }

    private fun formatTanggal(tanggal: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("EEEE dd MMM yyyy", Locale("id", "ID"))
            val date = input.parse(tanggal)
            if (date != null) {
                val formatted = output.format(date)
                formatted.replaceFirstChar { it.uppercase() }
            } else tanggal
        } catch (e: Exception) {
            tanggal
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Absensi>) {
        list = newList
        notifyDataSetChanged()
    }
}