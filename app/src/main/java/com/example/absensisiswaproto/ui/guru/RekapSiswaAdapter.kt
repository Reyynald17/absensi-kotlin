package com.example.absensisiswaproto.ui.guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.google.android.material.imageview.ShapeableImageView

data class RekapSiswaItem(
    val id: Int,
    val nis: String,
    val nama: String,
    val hadir: Int,
    val telat: Int,
    val alpha: Int
)

class RekapSiswaAdapter(
    private var list: List<RekapSiswaItem>,
    private val onClick: (RekapSiswaItem) -> Unit
) : RecyclerView.Adapter<RekapSiswaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFotoSiswa: ShapeableImageView = view.findViewById(R.id.ivFotoSiswa)
        val tvNamaSiswa: TextView = view.findViewById(R.id.tvNamaSiswa)
        val tvNisSiswa: TextView = view.findViewById(R.id.tvNisSiswa)
        val tvHadirCount: TextView = view.findViewById(R.id.tvHadirCount)
        val tvTelatCount: TextView = view.findViewById(R.id.tvTelatCount)
        val tvAlphaCount: TextView = view.findViewById(R.id.tvAlphaCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rekap_siswa, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvNamaSiswa.text = item.nama
        holder.tvNisSiswa.text = "NIS${item.nis}"
        holder.ivFotoSiswa.setImageResource(SiswaHelper.getFoto(item.id))

        holder.tvHadirCount.text = item.hadir.toString()
        holder.tvTelatCount.text = item.telat.toString()
        holder.tvAlphaCount.text = item.alpha.toString()

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<RekapSiswaItem>) {
        list = newList
        notifyDataSetChanged()
    }
}