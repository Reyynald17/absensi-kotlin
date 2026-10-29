# 📘 PROJECT HANDBOOK: Absensi Siswa Proto

> **Tujuan dokumen:** Referensi tunggal bagi AI Agent/developer untuk memahami seluruh fungsi project.
> **Versi:** 1.0.0 | **Terakhir update:** 2025-02-24 | **Owner:** Development Team

## 1. Ringkasan Eksekutif
- **Apa project ini?** Aplikasi Android native berbasis Kotlin untuk sistem absensi siswa dan manajemen kehadiran sekolah, mencakup peran Siswa, Guru, dan Operator sekolah dengan fitur verifikasi guru, pemindaian QR/Face, kalender, riwayat, dan notifikasi.
- **Masalah yang diselesaikan:** Digitalisasi pencatatan kehadiran siswa dan verifikasi akun tenaga pengajar secara terpusat di perangkat mobile menggunakan SQLite lokal.
- **Target pengguna:** Siswa, Guru, dan Operator/Admin sekolah.
- **Status:** Development / Prototyping
- **Repo:** Lokal (`Absensi Siswa Proto`) | **Branch utama:** `main`

## 2. Tech Stack
| Layer | Teknologi | Versi | Catatan |
|-------|-----------|-------|---------|
| Bahasa | Kotlin | 1.9+ | Target SDK 36, Compile SDK 37 (minor 1) |
| UI Framework | Android Views (XML) + ViewBinding | - | Menggunakan AppCompat, ConstraintLayout, CardView |
| Database | SQLite (SQLiteOpenHelper / DatabaseHelper) | - | Penyimpanan lokal persisten |
| Kamera & ML | CameraX + Google ML Kit (Face Detection, Barcode Scanning) | 1.3.4 / 16.1.7 / 17.3.0 | Digunakan untuk absen sidik jari / scan QR |
| Animasi & Efek | BlurView (`Dimezis:BlurView`) | 2.0.3 | Efek kaca blur pada UI |
| Build System | Android Gradle Plugin (AGP) + Gradle KTS | 9.4.1 | - |

## 3. Struktur Direktori (Anotasi)
```text
absensi-kotlin/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/absensisiswaproto/
│           │   ├── data/
│           │   │   ├── local/
│           │   │   │   ├── DatabaseHelper.kt      # Manajemen SQLite & Seeder data
│           │   │   │   └── RiwayatProvider.kt     # Provider rekap data riwayat absensi
│           │   │   └── model/
│           │   │       ├── Guru.kt                # Data class Guru
│           │   │       └── NotifGuru.kt           # Data class Notifikasi Guru
│           │   └── ui/
│           │       ├── absen/                     # Modul Absensi Siswa (Scan/Fingerprint)
│           │       ├── beranda/                   # Modul Beranda Siswa
│           │       ├── component/                 # Komponen UI reusable (PressEffect, dll)
│           │       ├── guru/                      # Modul Guru (Beranda, Login, Profil, Absensi Siswa)
│           │       ├── kalender/                  # Modul Kalender Sekolah
│           │       ├── notifikasi/                # Modul Notifikasi Siswa
│           │       ├── operator/                  # Modul Operator (Verifikasi Guru, Detail, Beranda)
│           │       ├── pelajaran/                 # Modul Jadwal Pelajaran
│           │       ├── profile/                   # Modul Profil Siswa
│           │       └── riwayat/                   # Modul Riwayat Kehadiran Siswa
│           └── res/
│               ├── drawable/                      # Aset gambar, ikon, background shape
│               ├── layout/                        # File XML layout antarmuka
│               └── values/                        # Strings, colors, styles
```

## 4. Arsitektur & Alur Data
- **Pola arsitektur:** Model-View (Activity-based dengan ViewBinding & helper class pendukung).
- **Alur Data:** 
  1. Activity/UI meminta data dari `DatabaseHelper`.
  2. `DatabaseHelper` melakukan query ke SQLite database lokal.
  3. Hasil query dikembalikan ke UI atau diproses melalui provider (misal: `RiwayatProvider`).
- **Dependency eksternal:** AndroidX Core KTX, AppCompat, ConstraintLayout, Material Components, CameraX, ML Kit, BlurView, JitPack.

## 5. 📋 Katalog Fungsi (INTI DOKUMEN)

### 5.1 Modul: `DatabaseHelper` (`com.example.absensisiswaproto.data.local.DatabaseHelper`)
| Nama Fungsi | Tipe | Input | Output | Side Effect | File:Lokasi |
|-------------|------|-------|--------|-------------|-------------|
| `simpanAbsen` | Method | `userId: Int`, `metode: String` | `Boolean` | Menulis data absen ke SQLite | `DatabaseHelper.kt` |
| `getAbsensiByTanggal` | Method | `userId: Int`, `tanggal: String` | `Absensi?` | Membaca database | `DatabaseHelper.kt` |
| `updateStatusGuru` | Method | `nip: String`, `status: String` | `Boolean` | Memperbarui status guru | `DatabaseHelper.kt` |
| `loginGuru` | Method | `nip: String`, `nama: String` | `Guru?` | Query database guru terverifikasi | `DatabaseHelper.kt` |

**Detail fungsi kunci:**
#### `simpanAbsen`
- **Signature:** `fun simpanAbsen(userId: Int, metode: String): Boolean`
- **Tujuan:** Menyimpan catatan absensi harian siswa berdasarkan tanggal hari ini.
- **Parameter:** `userId` (ID siswa), `metode` (metode absen misal "fingerprint" / "qr").
- **Return:** `true` jika berhasil disimpan, `false` jika gagal.
- **Error:** SQLException jika gagal menulis.
- **Bergantung pada:** SQLiteDatabase writable.
- **Dipakai oleh:** `BerandaActivity`, `AbsenActivity`.

### 5.2 Modul: `BerandaActivity` (`com.example.absensisiswaproto.ui.beranda.BerandaActivity`)
- **Tujuan:** Halaman utama siswa, menampilkan ringkasan kehadiran 5 hari, status absen hari ini, navigasi menu, dan interaksi presensi (tahan tombol sidik jari 1.5 detik dengan animasi & getar).
- **Lokasi file:** `app/src/main/java/com/example/absensisiswaproto/ui/beranda/BerandaActivity.kt`

### 5.3 Modul: `VerifikasiGuruActivity` & `GuruVerifikasiAdapter` (`com.example.absensisiswaproto.ui.operator`)
- **Tujuan:** Memungkinkan operator sekolah melihat daftar guru yang mendaftar, memfilter status (Semua, Menunggu, Terverifikasi), dan memverifikasi guru.
- **Fitur Khusus:** Guru yang terverifikasi otomatis menampilkan badge hijau (`#1E8827` / `#22C55E`) dengan latar belakang `bg_badge_terverifikasi`.
- **Lokasi file:** 
  - `VerifikasiGuruActivity.kt`
  - `GuruVerifikasiAdapter.kt`

### 5.4 Modul: `SiswaHelper` (`com.example.absensisiswaproto.ui.guru.SiswaHelper`)
- **Tujuan:** Helper untuk pengambilan foto profil guru dan siswa berdasarkan nama atau ID.
- **Fungsi Khusus:** `getFotoGuru(nama: String, id: Int)` mendeteksi nama seperti `"Nasir Wijaya Putra"` / `"Fuad"` dan mengembalikan resource foto `R.drawable.ppfuadnasirshapiro`.
- **Lokasi file:** `app/src/main/java/com/example/absensisiswaproto/ui/guru/SiswaHelper.kt`

## 6. Data Model
| Tabel / Entitas | Kolom Utama | Relasi |
|-----------------|-------------|--------|
| `users` | `id`, `nis`, `nama`, `role`, `kelas`, `password` | Tabel akun siswa/user |
| `guru` | `id`, `nip`, `nama`, `kelas_mapel`, `no_telp`, `email`, `status` | Tabel guru (status: `pending` / `verified`) |
| `absensi` | `id`, `user_id`, `tanggal`, `status`, `jam`, `metode` | Relasi ke `users` |
| `notifikasi` | `id`, `user_id`, `judul`, `pesan`, `tanggal`, `dibaca` | Relasi ke `users` |

## 7. Environment Variables
- Project ini sepenuhnya offline berbasis SQLite lokal, tidak memerlukan file `.env` eksternal. Konfigurasi SDK diatur melalui `app/build.gradle.kts` dan `libs.versions.toml`.

## 8. Alur Bisnis Kunci (Flow)
1. **Login Siswa / Guru / Operator:** Pengguna memasukkan kredensial, divalidasi oleh `DatabaseHelper`, lalu diarahkan ke Beranda masing-masing peran.
2. **Absensi Siswa (Beranda):** Siswa menekan tombol fingerprint, menahan selama 1.5 detik dengan feedback animasi skala dan getar (`VibrationEffect`), lalu data absensi disimpan.
3. **Verifikasi Guru (Operator):** Operator membuka menu Verifikasi Guru, melihat daftar dengan status "Menunggu" (merah), lalu menekan tombol "Verifikasi" yang mengubah status guru menjadi "Terverifikasi" (hijau).

## 9. API Contract
- Tidak menggunakan REST API eksternal (100% lokal SQLite). Kontrak data didefinisikan di dalam skema `DatabaseHelper.kt` dan data class di package `data.model`.

## 10. Testing & Quality
- **Jalankan test:** `./gradlew test` (Unit test) dan `./gradlew connectedAndroidTest` (Instrumented test).
- **Linter:** Android Studio Lint / Gradle build checks.
- **CI/CD:** Belum dikonfigurasi (Local Android Studio build).

## 11. Cara Menjalankan
1. Clone / buka project di Android Studio (Koala / Jellyfish / Ladybug atau versi terbaru).
2. Lakukan Gradle Sync (`File > Sync Project with Gradle Files`).
3. Hubungkan perangkat Android fisik (minSdk 24) atau jalankan Emulator Android (rekomendasi API 34+).
4. Klik tombol **Run 'app'` (Shift + F10) di Android Studio.

## 12. Known Issues & TODO
- [ ] Implementasi integrasi backend server (opsional untuk produksi masa depan).
- [ ] Penambahan unit test komprehensif untuk Dao/DatabaseHelper.

## 13. Kontak & Referensi
- **Owner:** Development Team / AI Assistant
- **Dokumen terkait:** `README.md`, `app/build.gradle.kts`
- **Changelog:** 
  - v1.0.0: Implementasi tombol absen 1.5s hold + getar, migrasi drawable logo/background, perbaikan status hijau verifikasi guru, penambahan pemetaan foto guru Nasir Wijaya Putra.
