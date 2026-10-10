package com.example.absensisiswaproto.ui.guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.google.android.material.imageview.ShapeableImageView

class SiswaAbsensiAdapter(
    private var list: List<SiswaAbsensiItem>,
    private val onClick: (SiswaAbsensiItem) -> Unit
) : RecyclerView.Adapter<SiswaAbsensiAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFotoSiswa: ShapeableImageView = view.findViewById(R.id.ivFotoSiswa)
        val tvNamaSiswa: TextView = view.findViewById(R.id.tvNamaSiswa)
        val tvNisSiswa: TextView = view.findViewById(R.id.tvNisSiswa)
        val badgeStatus: LinearLayout = view.findViewById(R.id.badgeStatus)
        val ivStatusIcon: ImageView = view.findViewById(R.id.ivStatusIcon)
        val tvStatusLabel: TextView = view.findViewById(R.id.tvStatusLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_siswa_absensi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvNamaSiswa.text = item.nama
        holder.tvNisSiswa.text = "NIS${item.nis}"
        holder.ivFotoSiswa.setImageResource(SiswaHelper.getFoto(item.id))

        when (item.status.lowercase()) {
            "hadir" -> {
                holder.badgeStatus.setBackgroundResource(R.drawable.bg_status_hadir_small)
                holder.ivStatusIcon.setImageResource(R.drawable.ic_check)
                holder.tvStatusLabel.text = "Hadir"
                holder.tvStatusLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            "telat" -> {
                holder.badgeStatus.setBackgroundResource(R.drawable.bg_status_telat_small)
                holder.ivStatusIcon.setImageResource(R.drawable.ic_warning)
                holder.tvStatusLabel.text = "Telat"
                holder.tvStatusLabel.setTextColor(0xFF000000.toInt())
                holder.ivStatusIcon.setColorFilter(0xFF000000.toInt())
            }
            "absen", "alpha" -> {
                holder.badgeStatus.setBackgroundResource(R.drawable.bg_status_alpha_small)
                holder.ivStatusIcon.setImageResource(R.drawable.ic_remove)
                holder.tvStatusLabel.text = "Alpha"
                holder.tvStatusLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            "libur" -> {
                holder.badgeStatus.setBackgroundResource(R.drawable.bg_status_libur_small)
                holder.ivStatusIcon.setImageResource(R.drawable.ic_kalendar)
                holder.tvStatusLabel.text = "Libur"
                holder.tvStatusLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            else -> {
                holder.badgeStatus.setBackgroundResource(R.drawable.bg_status_belum_absen_small)
                holder.ivStatusIcon.setImageResource(R.drawable.ic_remove)
                holder.tvStatusLabel.text = "Belum"
                holder.tvStatusLabel.setTextColor(0xFFFFFFFF.toInt())
                holder.ivStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<SiswaAbsensiItem>) {
        list = newList
        notifyDataSetChanged()
    }
}