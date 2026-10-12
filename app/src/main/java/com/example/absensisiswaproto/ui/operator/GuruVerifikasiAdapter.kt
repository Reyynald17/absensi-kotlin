package com.example.absensisiswaproto.ui.operator

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.model.Guru
import com.example.absensisiswaproto.databinding.ItemGuruVerifikasiBinding
import com.example.absensisiswaproto.ui.component.PressEffect

class GuruVerifikasiAdapter(
    private var items: List<Guru> = emptyList(),
    private val onDetail: (Guru) -> Unit,
    private val onVerifikasi: (Guru) -> Unit
) : RecyclerView.Adapter<GuruVerifikasiAdapter.VH>() {

    fun submit(data: List<Guru>) {
        items = data
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemGuruVerifikasiBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGuruVerifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val g = items[position]
        val b = holder.b

        b.tvNama.text = g.nama.uppercase()
        b.tvNip.text = OperatorUtils.formatNip(g.nip)
        b.tvJurusan.text = OperatorUtils.formatJurusanLabel(g.kelasMapel)

        val isVerified = g.status.equals("verified", ignoreCase = true)

        if (isVerified) {
            b.tvBadgeStatus.text = "Terverifikasi"
            b.tvBadgeStatus.setBackgroundResource(R.drawable.bg_badge_terverifikasi)
            b.tvBadgeStatus.setTextColor(Color.parseColor("#22C55E"))

            b.tvBadgeAksi.text = "Berhasil"
            b.tvBadgeAksi.setBackgroundResource(R.drawable.bg_badge_berhasil_btn)
            b.tvBadgeAksi.setTextColor(Color.parseColor("#FFFFFF"))
            b.tvBadgeAksi.isEnabled = false
            b.tvBadgeAksi.isClickable = false
            b.tvBadgeAksi.setOnClickListener(null)
        } else {
            b.tvBadgeStatus.text = "Menunggu"
            b.tvBadgeStatus.setBackgroundResource(R.drawable.bg_badge_menunggu)
            b.tvBadgeStatus.setTextColor(Color.parseColor("#B00000"))

            b.tvBadgeAksi.text = "Verifikasi"
            b.tvBadgeAksi.setBackgroundResource(R.drawable.bg_badge_verifikasi_btn)
            b.tvBadgeAksi.setTextColor(Color.parseColor("#FFFFFF"))
            b.tvBadgeAksi.isEnabled = true
            b.tvBadgeAksi.isClickable = true
            b.tvBadgeAksi.setOnClickListener { onVerifikasi(g) }
        }

        b.btnDetail.setOnClickListener { onDetail(g) }
        PressEffect.attach(b.btnDetail)
        PressEffect.attach(b.tvBadgeAksi)
    }

    override fun getItemCount() = items.size
}