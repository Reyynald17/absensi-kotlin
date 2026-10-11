package com.example.absensisiswaproto.ui.operator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.data.model.Event
import com.example.absensisiswaproto.databinding.ItemInfoEventBinding
import com.example.absensisiswaproto.ui.component.PressEffect

class InfoEventAdapter(
    private var items: List<Event> = emptyList(),
    private val onClick: (Event) -> Unit = {}
) : RecyclerView.Adapter<InfoEventAdapter.VH>() {

    fun submit(data: List<Event>) {
        items = data
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemInfoEventBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemInfoEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ev = items[position]
        holder.b.tvInfoDay.text = ev.tanggal.substring(8, 10).trimStart('0')
        holder.b.tvInfoJudul.text = ev.judul
        holder.b.root.setOnClickListener { onClick(ev) }
        PressEffect.attach(holder.b.root)
    }

    override fun getItemCount() = items.size
}