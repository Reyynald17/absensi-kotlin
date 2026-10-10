package com.example.absensisiswaproto.ui.kalender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.google.android.material.button.MaterialButton

class MonthAdapter(
    private val months: List<String>,
    private val onMonthClick: (String) -> Unit
) : RecyclerView.Adapter<MonthAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnMonth: MaterialButton = view.findViewById(R.id.btnMonth)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_month, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val month = months[position]
        holder.btnMonth.text = month.uppercase()
        holder.btnMonth.setOnClickListener {
            onMonthClick(month)
        }
    }

    override fun getItemCount(): Int = months.size
}