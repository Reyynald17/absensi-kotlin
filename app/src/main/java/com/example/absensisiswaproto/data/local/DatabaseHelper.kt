package com.example.absensisiswaproto.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.absensisiswaproto.data.model.Absensi
import com.example.absensisiswaproto.data.model.Guru
import com.example.absensisiswaproto.data.model.User
import com.example.absensisiswaproto.ui.guru.EventItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "absensi.db"
        private const val DATABASE_VERSION = 10

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

        const val TABLE_GURU = "guru"
        const val COL_GURU_ID = "id"
        const val COL_GURU_NIP = "nip"
        const val COL_GURU_NAMA = "nama"
        const val COL_GURU_KELAS_MAPEL = "kelas_mapel"
        const val COL_GURU_NO_TELP = "no_telp"
        const val COL_GURU_EMAIL = "email"
        const val COL_GURU_STATUS = "status"

        const val TABLE_OPERATOR = "operator"
        const val TABLE_EVENT = "event"
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

        db.execSQL("""
            CREATE TABLE $TABLE_GURU (
                $COL_GURU_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_GURU_NIP TEXT UNIQUE NOT NULL,
                $COL_GURU_NAMA TEXT NOT NULL,
                $COL_GURU_KELAS_MAPEL TEXT NOT NULL,
                $COL_GURU_NO_TELP TEXT NOT NULL,
                $COL_GURU_EMAIL TEXT NOT NULL,
                $COL_GURU_STATUS TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_OPERATOR (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAMA TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("INSERT OR IGNORE INTO $TABLE_OPERATOR ($COL_NAMA, $COL_PASSWORD) VALUES ('optu1', 'sukingawi')")

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EVENT (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                judul TEXT NOT NULL,
                tanggal TEXT NOT NULL,
                jam_mulai TEXT NOT NULL,
                jam_selesai TEXT NOT NULL,
                lokasi TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("INSERT OR IGNORE INTO $TABLE_EVENT ($COL_ID, judul, tanggal, jam_mulai, jam_selesai, lokasi) VALUES (1, 'Rapat orang tua murid', '2026-10-06', '09:00', '12:00', 'Aula Sekolah')")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_EVENT ($COL_ID, judul, tanggal, jam_mulai, jam_selesai, lokasi) VALUES (2, 'Seminar SPMB', '2026-10-19', '09:00', '12:00', 'Aula Sekolah')")
        db.execSQL("INSERT OR IGNORE INTO $TABLE_EVENT ($COL_ID, judul, tanggal, jam_mulai, jam_selesai, lokasi) VALUES (3, 'Pembuka P5', '2026-10-29', '08:30', '11:00', 'Aula Sekolah')")

        seedData(db)
        seedGuru(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABSEN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GURU")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OPERATOR")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENT")
        onCreate(db)
    }

    private fun seedData(db: SQLiteDatabase) {
        insertUser(db, "12345", "Jacobi Ambasuki", "ngawi123", "siswa", "XII RPL 3")
        insertUser(db, "67890", "Arata Naoko", "sukiaselilohyah", "siswa", "XII DKV 2")
        insertUser(db, "admin01", "Guru", "admin123", "admin", "XI PSPT 1")
        insertUser(db, "11111", "Putri Ananda", "putri123", "siswa", "X DKV 1")
        insertUser(db, "22222", "Putra Wijaya", "putra123", "siswa", "X DKV 1")
        insertUser(db, "33333", "Nabila Safitri", "nabila123", "siswa", "X DKV 1")
        insertUser(db, "44444", "Rizki Pratama", "rizki123", "siswa", "X DKV 1")
        insertUser(db, "55555", "Salsa Aulia", "salsa123", "siswa", "X DKV 1")
    }

    private fun seedGuru(db: SQLiteDatabase) {
        insertGuru(
            db = db,
            nip = "6767",
            nama = "Fuad Nasir Shapiro",
            kelasMapel = "XII RPL 3",
            noTelp = "081234567890",
            email = "fuad@sekolah.id",
            status = "verified"
        )
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

    private fun insertGuru(
        db: SQLiteDatabase,
        nip: String,
        nama: String,
        kelasMapel: String,
        noTelp: String,
        email: String,
        status: String
    ) {
        val values = ContentValues().apply {
            put(COL_GURU_NIP, nip)
            put(COL_GURU_NAMA, nama)
            put(COL_GURU_KELAS_MAPEL, kelasMapel)
            put(COL_GURU_NO_TELP, noTelp)
            put(COL_GURU_EMAIL, email)
            put(COL_GURU_STATUS, status)
        }
        db.insert(TABLE_GURU, null, values)
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

    fun loginGuru(nip: String, nama: String): Guru? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_GURU WHERE $COL_GURU_NIP = ? AND $COL_GURU_NAMA = ? AND $COL_GURU_STATUS = 'verified'",
            arrayOf(nip, nama)
        )

        var guru: Guru? = null
        if (cursor.moveToFirst()) {
            guru = Guru(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GURU_ID)),
                nip = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NIP)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NAMA)),
                kelasMapel = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_KELAS_MAPEL)),
                noTelp = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NO_TELP)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_EMAIL)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_STATUS))
            )
        }
        cursor.close()
        return guru
    }

    fun loginOperator(nama: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_ID FROM $TABLE_OPERATOR WHERE $COL_NAMA = ? AND $COL_PASSWORD = ? LIMIT 1",
            arrayOf(nama, password)
        )
        val success = cursor.moveToFirst()
        cursor.close()
        return success
    }

    fun getOperatorByName(nama: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_NAMA FROM $TABLE_OPERATOR WHERE $COL_NAMA = ? LIMIT 1",
            arrayOf(nama)
        )
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }

    fun countGuruTotal(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_GURU", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun countGuruPending(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_GURU WHERE $COL_GURU_STATUS = 'pending'", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun countGuruVerified(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_GURU WHERE $COL_GURU_STATUS = 'verified'", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getEventMendatang(): List<EventItem> {
        val list = mutableListOf<EventItem>()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT judul, tanggal, jam_mulai, jam_selesai, lokasi FROM $TABLE_EVENT WHERE tanggal >= ? ORDER BY tanggal ASC",
            arrayOf(today)
        )
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                list.add(
                    EventItem(
                        judul = cursor.getString(0),
                        tanggal = cursor.getString(1),
                        jamMulai = cursor.getString(2),
                        jamSelesai = cursor.getString(3),
                        lokasi = cursor.getString(4)
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()

        if (list.isEmpty()) {
            val c2 = db.rawQuery(
                "SELECT judul, tanggal, jam_mulai, jam_selesai, lokasi FROM $TABLE_EVENT ORDER BY tanggal ASC LIMIT 5",
                null
            )
            if (c2.moveToFirst()) {
                while (!c2.isAfterLast) {
                    list.add(
                        EventItem(
                            judul = c2.getString(0),
                            tanggal = c2.getString(1),
                            jamMulai = c2.getString(2),
                            jamSelesai = c2.getString(3),
                            lokasi = c2.getString(4)
                        )
                    )
                    c2.moveToNext()
                }
            }
            c2.close()
        }
        return list
    }

    fun getGuruById(id: Int): Guru? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_GURU WHERE $COL_GURU_ID = ? LIMIT 1",
            arrayOf(id.toString())
        )
        var guru: Guru? = null
        if (cursor.moveToFirst()) {
            guru = Guru(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GURU_ID)),
                nip = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NIP)),
                nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NAMA)),
                kelasMapel = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_KELAS_MAPEL)),
                noTelp = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NO_TELP)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_EMAIL)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_STATUS))
            )
        }
        cursor.close()
        return guru
    }

    fun cekGuruExist(nip: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_GURU WHERE $COL_GURU_NIP = ?",
            arrayOf(nip)
        )
        val exist = cursor.count > 0
        cursor.close()
        return exist
    }

    fun registerGuru(
        nip: String,
        nama: String,
        noTelp: String,
        email: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_GURU_NIP, nip)
            put(COL_GURU_NAMA, nama)
            put(COL_GURU_KELAS_MAPEL, "-")
            put(COL_GURU_NO_TELP, noTelp)
            put(COL_GURU_EMAIL, email)
            put(COL_GURU_STATUS, "pending")
        }
        val result = db.insert(TABLE_GURU, null, values)
        return result != -1L
    }

    fun getPendingGuru(): List<Guru> {
        val list = mutableListOf<Guru>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_GURU WHERE $COL_GURU_STATUS = 'pending'",
            null
        )
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                list.add(
                    Guru(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_GURU_ID)),
                        nip = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NIP)),
                        nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NAMA)),
                        kelasMapel = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_KELAS_MAPEL)),
                        noTelp = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_NO_TELP)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_EMAIL)),
                        status = cursor.getString(cursor.getColumnIndexOrThrow(COL_GURU_STATUS))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return list
    }

    fun verifikasiGuru(guruId: Int, kelasMapel: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_GURU_KELAS_MAPEL, kelasMapel)
            put(COL_GURU_STATUS, "verified")
        }
        val result = db.update(
            TABLE_GURU,
            values,
            "$COL_GURU_ID = ?",
            arrayOf(guruId.toString())
        )
        return result > 0
    }

    fun tolakGuru(guruId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_GURU,
            "$COL_GURU_ID = ?",
            arrayOf(guruId.toString())
        )
        return result > 0
    }

    fun getSiswaByKelas(kelas: String): List<User> {
        val list = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_KELAS = ? AND $COL_ROLE = 'siswa' ORDER BY $COL_NAMA ASC",
            arrayOf(kelas)
        )
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                list.add(
                    User(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        nis = cursor.getString(cursor.getColumnIndexOrThrow(COL_NIS)),
                        nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA)),
                        password = cursor.getString(cursor.getColumnIndexOrThrow(COL_PASSWORD)),
                        role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                        kelas = cursor.getString(cursor.getColumnIndexOrThrow(COL_KELAS))
                    )
                )
                cursor.moveToNext()
            }
        }
        cursor.close()
        return list
    }

    fun getAllKelasWithCount(): List<Pair<String, Int>> {
        val list = mutableListOf<Pair<String, Int>>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_KELAS, COUNT(*) FROM $TABLE_USER WHERE $COL_ROLE = 'siswa' GROUP BY $COL_KELAS ORDER BY $COL_KELAS ASC",
            null
        )
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val kelas = cursor.getString(0)
                val count = cursor.getInt(1)
                list.add(kelas to count)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return list
    }

    fun getAbsensiByUserTanggal(userId: Int, tanggal: String): Absensi? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ABSEN WHERE $COL_ABSEN_USER_ID = ? AND $COL_ABSEN_TANGGAL = ? LIMIT 1",
            arrayOf(userId.toString(), tanggal)
        )
        var absensi: Absensi? = null
        if (cursor.moveToFirst()) {
            absensi = Absensi(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_USER_ID)),
                tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_TANGGAL)),
                jam = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_JAM)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_STATUS)),
                metode = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_METODE))
            )
        }
        cursor.close()
        return absensi
    }

    fun updateStatusAbsensi(userId: Int, tanggal: String, status: String): Boolean {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(tanggal)
        if (date != null) {
            val cal = Calendar.getInstance()
            cal.time = date
            val day = cal.get(Calendar.DAY_OF_WEEK)
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                return false
            }
        }

        val db = writableDatabase
        val existing = getAbsensiByUserTanggal(userId, tanggal)

        return if (existing != null) {
            val values = ContentValues().apply {
                put(COL_ABSEN_STATUS, status)
            }
            val result = db.update(
                TABLE_ABSEN,
                values,
                "$COL_ABSEN_USER_ID = ? AND $COL_ABSEN_TANGGAL = ?",
                arrayOf(userId.toString(), tanggal)
            )
            result > 0
        } else {
            val values = ContentValues().apply {
                put(COL_ABSEN_USER_ID, userId)
                put(COL_ABSEN_TANGGAL, tanggal)
                put(COL_ABSEN_JAM, "-")
                put(COL_ABSEN_STATUS, status)
                put(COL_ABSEN_METODE, "manual")
            }
            db.insert(TABLE_ABSEN, null, values) != -1L
        }
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

    fun getAbsenHariIni(userId: Int): Absensi? {
        val tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ABSEN WHERE $COL_ABSEN_USER_ID = ? AND $COL_ABSEN_TANGGAL = ? LIMIT 1",
            arrayOf(userId.toString(), tanggal)
        )

        var absensi: Absensi? = null
        if (cursor.moveToFirst()) {
            absensi = Absensi(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ABSEN_USER_ID)),
                tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_TANGGAL)),
                jam = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_JAM)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_STATUS)),
                metode = cursor.getString(cursor.getColumnIndexOrThrow(COL_ABSEN_METODE))
            )
        }
        cursor.close()
        return absensi
    }

    fun isHariLibur(): Boolean {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
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

    fun getUserByNis(nis: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COL_NIS = ? AND $COL_ROLE = 'siswa' LIMIT 1",
            arrayOf(nis)
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

    fun setHadirByNis(nis: String, metode: String = "qr_code"): Boolean {
        val user = getUserByNis(nis) ?: return false
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        val db = writableDatabase
        val existing = getAbsensiByUserTanggal(user.id, today)

        return if (existing != null) {
            val values = ContentValues().apply {
                put(COL_ABSEN_STATUS, "hadir")
                put(COL_ABSEN_JAM, now)
                put(COL_ABSEN_METODE, metode)
            }
            val result = db.update(
                TABLE_ABSEN,
                values,
                "$COL_ABSEN_USER_ID = ? AND $COL_ABSEN_TANGGAL = ?",
                arrayOf(user.id.toString(), today)
            )
            result > 0
        } else {
            val values = ContentValues().apply {
                put(COL_ABSEN_USER_ID, user.id)
                put(COL_ABSEN_TANGGAL, today)
                put(COL_ABSEN_JAM, now)
                put(COL_ABSEN_STATUS, "hadir")
                put(COL_ABSEN_METODE, metode)
            }
            db.insert(TABLE_ABSEN, null, values) != -1L
        }
    }
}