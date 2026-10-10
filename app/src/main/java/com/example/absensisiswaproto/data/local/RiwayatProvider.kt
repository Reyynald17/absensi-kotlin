package com.example.absensisiswaproto.data.local

import com.example.absensisiswaproto.data.model.Absensi

object RiwayatProvider {

    fun getAll(userId: Int, dataDb: List<Absensi>): List<Absensi> {
        val dataDummy = when (userId) {
            1 -> riwayatJacobi(userId)
            2 -> riwayatArata(userId)
            else -> riwayatGeneric(userId)
        }
        val tanggalDb = dataDb.map { it.tanggal }.toSet()
        val dummyFiltered = dataDummy.filter { it.tanggal !in tanggalDb }
        return (dataDb + dummyFiltered).sortedByDescending { it.tanggal }
    }

    fun rekap(userId: Int, dataDb: List<Absensi>): Triple<Int, Int, Int> {
        val semua = getAll(userId, dataDb)
        val hadir = semua.count { it.status.equals("hadir", ignoreCase = true) }
        val telat = semua.count { it.status.equals("telat", ignoreCase = true) }
        val absen = semua.count {
            it.status.equals("absen", ignoreCase = true) ||
            it.status.equals("alpha", ignoreCase = true)
        }
        return Triple(hadir, telat, absen)
    }

    private fun riwayatJacobi(userId: Int): List<Absensi> {
        return listOf(
            Absensi(1,  userId, "2026-08-01", "07:10", "hadir", "fingerprint"),
            Absensi(2,  userId, "2026-08-04", "07:12", "hadir", "fingerprint"),
            Absensi(3,  userId, "2026-08-05", "07:08", "hadir", "fingerprint"),
            Absensi(4,  userId, "2026-08-06", "07:15", "hadir", "fingerprint"),
            Absensi(5,  userId, "2026-08-07", "07:47", "telat", "fingerprint"),
            Absensi(6,  userId, "2026-08-08", "07:11", "hadir", "fingerprint"),
            Absensi(7,  userId, "2026-08-11", "07:09", "hadir", "fingerprint"),
            Absensi(8,  userId, "2026-08-12", "07:13", "hadir", "fingerprint"),
            Absensi(9,  userId, "2026-08-13", "07:14", "hadir", "fingerprint"),
            Absensi(10, userId, "2026-08-14", "07:10", "hadir", "fingerprint"),
            Absensi(11, userId, "2026-08-15", "07:08", "hadir", "fingerprint"),
            Absensi(12, userId, "2026-08-18", "07:12", "hadir", "fingerprint"),
            Absensi(13, userId, "2026-08-19", "07:11", "hadir", "fingerprint"),
            Absensi(14, userId, "2026-08-20", "07:09", "hadir", "fingerprint"),
            Absensi(15, userId, "2026-08-21", "07:13", "hadir", "fingerprint"),
            Absensi(16, userId, "2026-08-22", "07:44", "telat", "fingerprint"),
            Absensi(17, userId, "2026-08-25", "07:10", "hadir", "fingerprint"),
            Absensi(18, userId, "2026-08-26", "07:12", "hadir", "fingerprint"),
            Absensi(19, userId, "2026-08-27", "07:08", "hadir", "fingerprint"),
            Absensi(20, userId, "2026-08-28", "07:14", "hadir", "fingerprint"),
            Absensi(21, userId, "2026-08-29", "07:11", "hadir", "fingerprint"),
            Absensi(22, userId, "2026-09-01", "07:10", "hadir", "fingerprint"),
            Absensi(23, userId, "2026-09-02", "07:12", "hadir", "fingerprint"),
            Absensi(24, userId, "2026-09-03", "07:48", "telat", "fingerprint"),
            Absensi(25, userId, "2026-09-04", "07:10", "hadir", "fingerprint"),
            Absensi(26, userId, "2026-09-05", "07:08", "hadir", "fingerprint"),
            Absensi(27, userId, "2026-09-08", "07:14", "hadir", "fingerprint"),
            Absensi(28, userId, "2026-09-09", "07:11", "hadir", "fingerprint"),
            Absensi(29, userId, "2026-09-10", "07:09", "hadir", "fingerprint"),
            Absensi(30, userId, "2026-09-11", "07:45", "telat", "fingerprint"),
            Absensi(31, userId, "2026-09-12", "-",     "absen", "-"),
            Absensi(32, userId, "2026-09-15", "07:13", "hadir", "fingerprint"),
            Absensi(33, userId, "2026-09-16", "07:10", "hadir", "fingerprint"),
            Absensi(34, userId, "2026-09-17", "07:12", "hadir", "fingerprint"),
            Absensi(35, userId, "2026-09-18", "07:08", "hadir", "fingerprint"),
            Absensi(36, userId, "2026-09-19", "07:15", "hadir", "fingerprint"),
            Absensi(37, userId, "2026-09-22", "07:14", "hadir", "fingerprint"),
            Absensi(38, userId, "2026-09-23", "07:09", "hadir", "fingerprint"),
            Absensi(39, userId, "2026-09-24", "07:47", "telat", "fingerprint"),
            Absensi(40, userId, "2026-09-25", "07:11", "hadir", "fingerprint"),
            Absensi(41, userId, "2026-09-26", "07:13", "hadir", "fingerprint"),
            Absensi(42, userId, "2026-09-29", "07:10", "hadir", "fingerprint"),
            Absensi(43, userId, "2026-09-30", "07:12", "hadir", "fingerprint"),
            Absensi(44, userId, "2026-10-01", "07:08", "hadir", "fingerprint"),
            Absensi(45, userId, "2026-10-02", "07:14", "hadir", "fingerprint"),
            Absensi(46, userId, "2026-10-05", "07:11", "hadir", "fingerprint"),
            Absensi(47, userId, "2026-10-06", "07:10", "hadir", "fingerprint"),
            Absensi(48, userId, "2026-10-07", "07:09", "hadir", "fingerprint"),
            Absensi(49, userId, "2026-10-08", "07:13", "hadir", "fingerprint"),
            Absensi(50, userId, "2026-10-09", "-",     "absen", "-"),
            Absensi(51, userId, "2026-10-10", "07:15", "hadir", "fingerprint")
        )
    }

    private fun riwayatArata(userId: Int): List<Absensi> {
        return listOf(
            Absensi(1,  userId, "2026-08-01", "07:15", "hadir", "fingerprint"),
            Absensi(2,  userId, "2026-08-04", "07:52", "telat", "fingerprint"),
            Absensi(3,  userId, "2026-08-05", "07:10", "hadir", "fingerprint"),
            Absensi(4,  userId, "2026-08-06", "-",     "absen", "-"),
            Absensi(5,  userId, "2026-08-07", "07:11", "hadir", "fingerprint"),
            Absensi(6,  userId, "2026-08-08", "07:48", "telat", "fingerprint"),
            Absensi(7,  userId, "2026-08-11", "07:14", "hadir", "fingerprint"),
            Absensi(8,  userId, "2026-08-12", "07:09", "hadir", "fingerprint"),
            Absensi(9,  userId, "2026-08-13", "-",     "absen", "-"),
            Absensi(10, userId, "2026-08-14", "07:12", "hadir", "fingerprint"),
            Absensi(11, userId, "2026-08-15", "07:50", "telat", "fingerprint"),
            Absensi(12, userId, "2026-08-18", "07:10", "hadir", "fingerprint"),
            Absensi(13, userId, "2026-08-19", "07:13", "hadir", "fingerprint"),
            Absensi(14, userId, "2026-08-20", "07:11", "hadir", "fingerprint"),
            Absensi(15, userId, "2026-08-21", "07:55", "telat", "fingerprint"),
            Absensi(16, userId, "2026-09-01", "07:13", "hadir", "fingerprint"),
            Absensi(17, userId, "2026-09-02", "07:52", "telat", "fingerprint"),
            Absensi(18, userId, "2026-09-03", "07:11", "hadir", "fingerprint"),
            Absensi(19, userId, "2026-09-04", "-",     "absen", "-"),
            Absensi(20, userId, "2026-09-05", "07:10", "hadir", "fingerprint"),
            Absensi(21, userId, "2026-09-08", "07:14", "hadir", "fingerprint"),
            Absensi(22, userId, "2026-09-09", "07:49", "telat", "fingerprint"),
            Absensi(23, userId, "2026-09-10", "07:12", "hadir", "fingerprint"),
            Absensi(24, userId, "2026-09-11", "07:11", "hadir", "fingerprint"),
            Absensi(25, userId, "2026-09-12", "-",     "absen", "-"),
            Absensi(26, userId, "2026-09-15", "07:10", "hadir", "fingerprint"),
            Absensi(27, userId, "2026-09-16", "07:13", "hadir", "fingerprint"),
            Absensi(28, userId, "2026-09-17", "07:47", "telat", "fingerprint"),
            Absensi(29, userId, "2026-09-18", "07:15", "hadir", "fingerprint"),
            Absensi(30, userId, "2026-09-19", "07:11", "hadir", "fingerprint"),
            Absensi(31, userId, "2026-09-22", "07:12", "hadir", "fingerprint"),
            Absensi(32, userId, "2026-09-23", "07:10", "hadir", "fingerprint"),
            Absensi(33, userId, "2026-09-24", "-",     "absen", "-"),
            Absensi(34, userId, "2026-10-01", "07:50", "telat", "fingerprint"),
            Absensi(35, userId, "2026-10-02", "07:13", "hadir", "fingerprint"),
            Absensi(36, userId, "2026-10-05", "07:11", "hadir", "fingerprint"),
            Absensi(37, userId, "2026-10-06", "-",     "absen", "-"),
            Absensi(38, userId, "2026-10-07", "07:52", "telat", "fingerprint"),
            Absensi(39, userId, "2026-10-08", "07:10", "hadir", "fingerprint")
        )
    }

    private fun riwayatGeneric(userId: Int): List<Absensi> {
        return listOf(
            Absensi(1, userId, "2026-09-21", "07:15", "hadir", "fingerprint"),
            Absensi(2, userId, "2026-09-22", "07:12", "hadir", "fingerprint"),
            Absensi(3, userId, "2026-09-23", "07:30", "telat", "fingerprint")
        )
    }
}
