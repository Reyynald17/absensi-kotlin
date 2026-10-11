package com.example.absensisiswaproto.ui.guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.NotifGuru
import com.google.android.material.imageview.ShapeableImageView

class NotifGuruAdapter(
    private var list: List<NotifGuru>,
    private val onClick: (NotifGuru) -> Unit
) : RecyclerView.Adapter<NotifGuruAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFotoNotif: ShapeableImageView = view.findViewById(R.id.ivFotoNotif)
        val tvNotifNama: TextView = view.findViewById(R.id.tvNotifNama)
        val tvNotifDeskripsi: TextView = view.findViewById(R.id.tvNotifDeskripsi)
        val ivNotifStatusIcon: ImageView = view.findViewById(R.id.ivNotifStatusIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_guru, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.ivFotoNotif.setImageResource(item.fotoRes)
        holder.tvNotifNama.text = item.namaSiswa
        holder.tvNotifDeskripsi.text = item.deskripsi

        when (item.tipeStatus.lowercase()) {
            "alpha" -> {
                holder.ivNotifStatusIcon.setBackgroundResource(R.drawable.bg_icon_status_alpha)
                holder.ivNotifStatusIcon.setImageResource(R.drawable.ic_remove)
                holder.ivNotifStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            "telat" -> {
                holder.ivNotifStatusIcon.setBackgroundResource(R.drawable.bg_icon_status_telat)
                holder.ivNotifStatusIcon.setImageResource(R.drawable.ic_warning)
                holder.ivNotifStatusIcon.setColorFilter(0xFF000000.toInt())
            }
            "hadir" -> {
                holder.ivNotifStatusIcon.setBackgroundResource(R.drawable.bg_icon_status_hadir)
                holder.ivNotifStatusIcon.setImageResource(R.drawable.ic_check)
                holder.ivNotifStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
            else -> {
                holder.ivNotifStatusIcon.setBackgroundResource(R.drawable.bg_icon_status_alpha)
                holder.ivNotifStatusIcon.setImageResource(R.drawable.ic_remove)
                holder.ivNotifStatusIcon.setColorFilter(0xFFFFFFFF.toInt())
            }
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<NotifGuru>) {
        list = newList
        notifyDataSetChanged()
    }
}