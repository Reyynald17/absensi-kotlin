package com.example.absensisiswaproto.ui.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.databinding.ItemRiwayatAbsenBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RiwayatAdapter(private val list: List<Absensi>) :
    RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRiwayatAbsenBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatAbsenBinding.inflate(
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

        val formattedTanggal = try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(item.tanggal)
            if (date != null) {
                val outputFormat = SimpleDateFormat("EEEE dd MMM yyyy", Locale("id", "ID"))
                outputFormat.format(date)
            } else {
                item.tanggal
            }
        } catch (e: Exception) {
            item.tanggal
        }

        binding.tvItemTanggal.text = formattedTanggal

        val statusLower = item.status.lowercase(Locale.getDefault())
        when {
            statusLower == "hadir" -> {
                binding.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                binding.ivItemStatusIcon.setImageResource(R.drawable.ic_check)
                binding.tvItemStatusLabel.text = "Hadir"
            }
            statusLower == "alpha" || statusLower == "absen" -> {
                binding.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_alpha_pill)
                binding.ivItemStatusIcon.setImageResource(R.drawable.ic_remove)
                binding.tvItemStatusLabel.text = "Alpha"
            }
            statusLower == "telat" -> {
                binding.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_telat_pill)
                binding.ivItemStatusIcon.setImageResource(R.drawable.ic_warning)
                binding.tvItemStatusLabel.text = "Telat"
            }
            statusLower == "izin" || statusLower == "sakit" -> {
                binding.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_telat_pill)
                binding.ivItemStatusIcon.setImageResource(R.drawable.ic_warning)
                binding.tvItemStatusLabel.text = "Izin"
            }
            else -> {
                binding.layoutStatusBadge.setBackgroundResource(R.drawable.bg_status_neutral_pill)
                binding.ivItemStatusIcon.setImageResource(R.drawable.ic_check)
                binding.tvItemStatusLabel.text = "-"
            }
        }
    }
}
