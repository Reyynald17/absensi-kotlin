package com.example.absensisiswaproto.ui.operator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.databinding.ItemDokumenGuruBinding
import com.example.absensisiswaproto.ui.component.PressEffect

class DokumenAdapter(
    private val items: List<String>,
    private val onLihat: (String) -> Unit
) : RecyclerView.Adapter<DokumenAdapter.VH>() {

    inner class VH(val b: ItemDokumenGuruBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDokumenGuruBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.b.tvNamaDokumen.text = items[position]
        holder.b.tvLihatDokumen.setOnClickListener { onLihat(items[position]) }
        PressEffect.attach(holder.b.tvLihatDokumen)
    }

    override fun getItemCount() = items.size
}