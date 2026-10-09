package com.example.absensisiswaproto.ui.pelajaran

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.data.model.Pelajaran
import com.example.absensisiswaproto.databinding.ItemPelajaranBinding

class PelajaranAdapter(private val list: List<Pelajaran>) :
    RecyclerView.Adapter<PelajaranAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPelajaranBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPelajaranBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding

        binding.tvSubjectName.text = item.nama
        binding.tvTeacherName.text = "Pengajar: ${item.pengajar}"
        binding.tvClassRoom.text = "Ruang: ${item.ruang}"
        binding.tvTimeSlot.text = item.jam
    }
}
