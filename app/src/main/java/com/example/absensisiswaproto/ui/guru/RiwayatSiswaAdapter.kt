package com.example.absensisiswaproto.ui.guru

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

class RiwayatSiswaAdapter(
    private var list: List<Absensi>
) : RecyclerView.Adapter<RiwayatSiswaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTanggalRiwayat: TextView = view.findViewById(R.id.tvTanggalRiwayat)
        val badgeRiwayat: LinearLayout = view.findViewById(R.id.badgeRiwayat)
        val ivRiwayatIcon: ImageView = view.findViewById(R.id.ivRiwayatIcon)
        val tvRiwayatLabel: TextView = view.findViewById(R.id.tvRiwayatLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat_siswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTanggalRiwayat.text = formatTanggal(item.tanggal)

        when (item.status.lowercase()) {
            "hadir" -> {
                holder.badgeRiwayat.setBackgroundResource(R.drawable.bg_status_hadir_small)
                holder.ivRiwayatIcon.setImageResource(R.drawable.ic_check)
                holder.tvRiwayatLabel.text = "Hadir"
                holder.tvRiwayatLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivRiwayatIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            "telat" -> {
                holder.badgeRiwayat.setBackgroundResource(R.drawable.bg_status_telat_small)
                holder.ivRiwayatIcon.setImageResource(R.drawable.ic_warning)
                holder.tvRiwayatLabel.text = "Telat"
                holder.tvRiwayatLabel.setTextColor(0xFF000000.toInt())
                holder.ivRiwayatIcon.setColorFilter(0xFF000000.toInt())
            }
            else -> {
                holder.badgeRiwayat.setBackgroundResource(R.drawable.bg_status_alpha_small)
                holder.ivRiwayatIcon.setImageResource(R.drawable.ic_remove)
                holder.tvRiwayatLabel.text = "Alpha"
                holder.tvRiwayatLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivRiwayatIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
        }
    }

    private fun formatTanggal(tanggal: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
            val date = input.parse(tanggal)
            if (date != null) output.format(date).replaceFirstChar { it.uppercase() } else tanggal
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