package com.example.absensisiswaproto.data.model

data class NotifItem(
    val id: Int,
    val userId: Int,
    val judul: String,
    val pesan: String,
    val tanggal: String,
    val dibaca: Boolean
)