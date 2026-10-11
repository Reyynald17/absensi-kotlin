package com.example.absensisiswaproto.ui.operator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.absensisiswaproto.databinding.ItemDropdownKategoriBinding

data class KategoriItem(val nama: String, val colorRes: Int)

class KategoriAdapter(
    private val ctx: Context,
    private val items: List<KategoriItem>
) : ArrayAdapter<KategoriItem>(ctx, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        createView(position, convertView, parent)

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        createView(position, convertView, parent)

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemDropdownKategoriBinding.inflate(
            LayoutInflater.from(ctx), parent, false
        )
        val item = items[position]
        binding.tvKategori.text = item.nama
        binding.dotKategori.setBackgroundResource(item.colorRes)
        return binding.root
    }
}