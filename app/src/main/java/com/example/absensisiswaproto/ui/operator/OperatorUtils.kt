package com.example.absensisiswaproto.ui.operator

import java.text.SimpleDateFormat
import java.util.Locale

object OperatorUtils {
    fun extractJurusan(kelasMapel: String): String {
        val km = kelasMapel.uppercase()
        return when {
            km.contains("DKV") -> "DKV"
            km.contains("RPL") -> "RPL"
            km.contains("TKJ") -> "TKJ"
            km.contains("PSPT") -> "PSPT"
            km.contains("PEKSOS") -> "PEKSOS"
            km.contains("AN") || km.contains("ANIMASI") -> "AN"
            km.contains("PS") -> "PS"
            else -> kelasMapel
        }
    }

    fun formatNip(nip: String): String = "NIP.$nip"

    fun formatJurusanLabel(kelasMapel: String): String =
        "KEJURUAN(${extractJurusan(kelasMapel)})"

    fun formatBulanIndonesia(month: Int): String = when (month) {
        1 -> "Januari"; 2 -> "Februari"; 3 -> "Maret"; 4 -> "April"
        5 -> "Mei"; 6 -> "Juni"; 7 -> "Juli"; 8 -> "Agustus"
        9 -> "September"; 10 -> "Oktober"; 11 -> "November"; 12 -> "Desember"
        else -> "-"
    }

    fun formatTanggalIndo(yyyymmdd: String): String {
        return try {
            val inF = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outF = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outF.format(inF.parse(yyyymmdd)!!)
        } catch (e: Exception) {
            yyyymmdd
        }
    }
}