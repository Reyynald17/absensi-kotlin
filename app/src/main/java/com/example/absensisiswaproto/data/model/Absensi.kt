package com.example.absensisiswaproto.data.model

data class Absensi(
    val id: Int,
    val userId: Int,
    val tanggal: String,
    val jam: String,
    val status: String,
    val metode: String
)