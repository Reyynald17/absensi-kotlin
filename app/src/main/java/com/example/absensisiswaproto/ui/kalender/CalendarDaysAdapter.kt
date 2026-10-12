package com.example.absensisiswaproto.ui.kalender

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R

class CalendarDaysAdapter(
    private var items: List<DayItem>
) : RecyclerView.Adapter<CalendarDaysAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (item.day.isEmpty()) {
            holder.tvDay.text = ""
            holder.tvDay.setBackgroundResource(0)
            holder.tvDay.setTextColor(Color.TRANSPARENT)
            return
        }

        holder.tvDay.text = item.day

        when {
            item.isRed -> {
                holder.tvDay.setBackgroundResource(R.drawable.bg_day_libur)
                holder.tvDay.setTextColor(Color.WHITE)
            }
            item.isGreen -> {
                holder.tvDay.setBackgroundResource(R.drawable.bg_day_upacara)
                holder.tvDay.setTextColor(Color.WHITE)
            }
            item.isBlue -> {
                holder.tvDay.setBackgroundResource(R.drawable.bg_day_event)
                holder.tvDay.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvDay.setBackgroundResource(0)
                holder.tvDay.setTextColor(Color.WHITE)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<DayItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}