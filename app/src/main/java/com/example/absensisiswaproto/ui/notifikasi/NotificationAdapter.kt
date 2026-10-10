package com.example.absensisiswaproto.ui.notifikasi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.Notif

class NotificationAdapter(
    private val list: List<Notif>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val frameIcon: FrameLayout = view.findViewById(R.id.frameIcon)
        val ivNotifIcon: ImageView = view.findViewById(R.id.ivNotifIcon)
        val tvNotifTitle: TextView = view.findViewById(R.id.tvNotifTitle)
        val tvNotifSubtitle: TextView = view.findViewById(R.id.tvNotifSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.frameIcon.setBackgroundResource(item.bgRes)
        holder.ivNotifIcon.setImageResource(item.iconRes)
        holder.ivNotifIcon.setColorFilter(item.iconTint)
        holder.tvNotifTitle.text = item.title
        holder.tvNotifSubtitle.text = item.subtitle
    }

    override fun getItemCount(): Int = list.size
}
