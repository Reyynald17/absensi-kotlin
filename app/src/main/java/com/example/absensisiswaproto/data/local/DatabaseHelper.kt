package com.example.absensisiswaproto.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.data.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "absensi.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_USER = "users"
        const val COL_ID = "id"
        const val COL_NIS = "nis"
        const val COL_NAMA = "nama"
        const val COL_PASSWORD = "password"
        const val COL_ROLE = "role"
        const val COL_KELAS = "kelas"

        const val TABLE_ABSEN = "absensi"
        const val COL_ABSEN_ID = "id"
        const val COL_ABSEN_USER_ID = "user_id"
        const val COL_ABSEN_TANGGAL = "tanggal"
        const val COL_ABSEN_JAM = "jam"
        const val COL_ABSEN_STATUS = "status"
        const val COL_ABSEN_METODE = "metode"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
    CREATE TABLE $TABLE_USER (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_NIS TEXT UNIQUE NOT NULL,
        $COL_NAMA TEXT NOT NULL,
        $COL_PASSWORD TEXT NOT NULL,
        $COL_ROLE TEXT NOT NULL,
        $COL_KELAS TEXT NOT NULL
    )
""".trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_ABSEN (
                $COL_ABSEN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ABSEN_USER_ID INTEGER NOT NULL,
                $COL_ABSEN_TANGGAL TEXT NOT NULL,
                $COL_ABSEN_JAM TEXT NOT NULL,
                $COL_ABSEN_STATUS TEXT NOT NULL,
                $COL_ABSEN_METODE TEXT NOT NULL
            )
        """.trimIndent())

        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABSEN")
        onCreate(db)
    }

    private fun seedData(db: SQLiteDatabase) {
        insertUser(db, "12345", "Jacobi Ambasuki", "ngawi123", "siswa", "XII RPL 3")
        insertUser(db, "67890", "Arata Naoko", "sukiaselilohyah", "siswa", "XII DKV 2")
        insertUser(db, "admin01", "Guru", "admin123", "admin", "XI PSPT 1")
    }

    private fun insertUser(
        db: SQLiteDatabase,
        nis: String,
        nama: String,
        password: String,
        role: String,
        kelas: String
    ) {
        val values = ContentValues().apply {
            put(COL_NIS, nis)
            put(COL_NAMA, nama)
            put(COL_PASSWORD, password)
            put(COL_ROLE, role)
            put(COL_KELAS, kelas)
        }
        db.insert(TABLE_USER, null, values)
    }

    fun login(nis: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_NIS = ? AND $COL_PASSWORD = ?",
            arrayOf(nis, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                nis = cursor.getString(cursor.getColumnIndexOrThrow(COL_NIS)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                kelas = cursor.getString(cursor.getColumnIndexOrThrow(COL_KELAS))
            )
        }
        cursor.close()
        return user
    }

    fun simpanAbsen(userId: Int, metode: String): Boolean {
        val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val jam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ABSEN_USER_ID, userId)
            put(COL_ABSEN_TANGGAL, tanggal)
            put(COL_ABSEN_JAM, jam)
            put(COL_ABSEN_STATUS, "hadir")
            put(COL_ABSEN_METODE, metode)
        }

        val result = db.insert(TABLE_ABSEN, null, values)
        return result != -1L
    }

    fun sudahAbsenHariIni(userId: Int): Boolean {
        val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ABSEN WHERE $COL_ABSEN_USER_ID = ? AND $COL_ABSEN_TANGGAL = ?",
            arrayOf(userId.toString(), tanggal)
        )
        val sudah = cursor.count > 0
        cursor.close()
        return sudah
    }

    fun getRiwayatAbsen(userId: Int): List<Absensi> {
        val list = mutableListOf<Absensi>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ABSEN WHERE $COL_ABSEN_USER_ID = ? ORDER BY $COL_ABSEN_TANGGAL DESC",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                list.add(
                    Absensi(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_ID)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_USER_ID)),
                        tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_TANGGAL)),
                        jam = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_JAM)),
                        status = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_STATUS)),
                        metode = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_METODE))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return list
    }
}