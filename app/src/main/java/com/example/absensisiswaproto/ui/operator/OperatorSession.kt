package com.example.absensisiswaproto.ui.operator

import android.content.Context

object OperatorSession {
    private const val PREF = "operator_session"
    private const val KEY_NAMA = "op_nama"

    fun save(context: Context, namaLogin: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putString(KEY_NAMA, namaLogin).apply()
    }

    fun getNama(context: Context): String =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_NAMA, "").orEmpty()

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}