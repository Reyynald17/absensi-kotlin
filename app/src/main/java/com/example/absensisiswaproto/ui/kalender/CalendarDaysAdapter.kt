package com.example.absensisiswaproto.ui.kalender

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.databinding.ItemCalendarDayBinding

data class DayItem(
    val dayNumber: String,
    val isRed: Boolean = false,
    val isGreen: Boolean = false
)

class CalendarDaysAdapter(private val daysList: List<DayItem>) :
    RecyclerView.Adapter<CalendarDaysAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = daysList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = daysList[position]
        val binding = holder.binding

        binding.tvDayNumber.text = item.dayNumber
        binding.tvDayNumber.background = null

        if (item.dayNumber.isEmpty()) {
            return
        }

        when {
            item.isRed -> {
                binding.tvDayNumber.setTextColor(Color.parseColor("#FF1744"))
            }
            item.isGreen -> {
                binding.tvDayNumber.setTextColor(Color.parseColor("#00E676"))
            }
            else -> {
                binding.tvDayNumber.setTextColor(Color.WHITE)
            }
        }
    }
}
