package com.example.absensisiswaproto.data.model

data class Event(
    val id: Int,
    val tanggal: String,
    val judul: String,
    val tipe: String,
    val waktuMulai: String? = null,
    val waktuSelesai: String? = null,
    val lokasi: String? = null,
    val deskripsi: String? = null
)