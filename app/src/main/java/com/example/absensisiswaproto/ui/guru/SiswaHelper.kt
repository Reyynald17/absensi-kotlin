package com.example.absensisiswaproto.ui.guru

import com.example.absensisiswaproto.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SiswaHelper {

    fun getFoto(id: Int): Int {
        return when (id) {
            1 -> R.drawable.ppjacobiambasukixiirpl3
            2 -> R.drawable.pparatanaokoxiifm2
            else -> when (id % 3) {
                0 -> R.drawable.siswa_kanan
                1 -> R.drawable.siswa_kiri
                else -> R.drawable.siswa_kanan
            }
        }
    }

    fun getFotoGuru(nama: String, id: Int = 0): Int {
        val lower = nama.lowercase()
        return when {
            lower.contains("fuad") -> R.drawable.ppfuadnasirshapiro
            else -> when (id) {
                1 -> R.drawable.ppfuadnasirshapiro
                else -> R.drawable.gurucewek
            }
        }
    }

    fun isWeekend(tanggal: String): Boolean {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(tanggal) ?: return false
            val cal = Calendar.getInstance()
            cal.time = date
            val day = cal.get(Calendar.DAY_OF_WEEK)
            day == Calendar.SATURDAY || day == Calendar.SUNDAY
        } catch (e: Exception) {
            false
        }
    }
}