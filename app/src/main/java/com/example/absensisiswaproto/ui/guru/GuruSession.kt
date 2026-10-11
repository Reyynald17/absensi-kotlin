package com.example.absensisiswaproto.ui.guru

import android.content.Context

object GuruSession {
    private const val PREF = "guru_session"
    private const val KEY_ID = "guru_id"
    private const val KEY_NIP = "guru_nip"
    private const val KEY_NAMA = "guru_nama"

    fun save(context: Context, id: Int, nip: String, nama: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_ID, id)
            .putString(KEY_NIP, nip)
            .putString(KEY_NAMA, nama)
            .apply()
    }

    fun getGuruId(context: Context): Int =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getInt(KEY_ID, -1)

    fun getNip(context: Context): String =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_NIP, "").orEmpty()

    fun getNama(context: Context): String =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_NAMA, "").orEmpty()

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}