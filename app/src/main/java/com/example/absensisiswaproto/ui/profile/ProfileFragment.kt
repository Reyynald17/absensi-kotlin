package com.example.absensisiswaproto.ui.profile

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import java.io.File
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.data.local.RiwayatProvider
import com.example.absensisiswaproto.databinding.FragmentProfileBinding
import com.example.absensisiswaproto.ui.login.LoginActivity
import com.example.absensisiswaproto.ui.siswa.SiswaMainActivity
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper

    private var userId: Int = 0
    private var userNis: String = ""
    private var userNama: String = ""
    private var userRole: String = ""
    private var userKelas: String = ""

    companion object {
        private const val ARG_USER_ID = "ARG_USER_ID"
        private const val ARG_USER_NIS = "ARG_USER_NIS"
        private const val ARG_USER_NAMA = "ARG_USER_NAMA"
        private const val ARG_USER_ROLE = "ARG_USER_ROLE"
        private const val ARG_USER_KELAS = "ARG_USER_KELAS"

        fun newInstance(
            userId: Int,
            userNis: String,
            userNama: String,
            userRole: String,
            userKelas: String
        ): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_USER_ID, userId)
                    putString(ARG_USER_NIS, userNis)
                    putString(ARG_USER_NAMA, userNama)
                    putString(ARG_USER_ROLE, userRole)
                    putString(ARG_USER_KELAS, userKelas)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID, 0)
            userNis = it.getString(ARG_USER_NIS, "1234567890")
            userNama = it.getString(ARG_USER_NAMA, "Ananda Prayuda")
            userRole = it.getString(ARG_USER_ROLE, "siswa")
            userKelas = it.getString(ARG_USER_KELAS, "XII DKV 2")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        binding.overlayPelajaran.root.visibility = View.GONE
        binding.overlayJadwal.root.visibility = View.GONE

        setupBlurEffect()
        setupProfileData()
        setupStatusKehadiran()
        loadRekapKehadiran()
        setupActions()
        setupOverlayPelajaran()
        setupOverlayJadwal()
    }

    private fun setupBlurEffect() {
        val blurView: BlurView = binding.overlayJadwal.blurView
        val decorView = requireActivity().window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground: Drawable = decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)
            .setBlurAutoUpdate(true)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val intent = Intent(requireContext(), CropImageActivity::class.java).apply {
                putExtra("EXTRA_IMAGE_URI", uri.toString())
                putExtra("EXTRA_USER_ID", userId)
            }
            cropImageLauncher.launch(intent)
        }
    }

    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filePath = result.data?.getStringExtra("EXTRA_FILE_PATH")
            if (filePath != null) {
                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath)
                binding.ivProfilePhoto.setImageBitmap(bitmap)
                val prefs = requireContext().getSharedPreferences("profile_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("photo_path_$userId", filePath).apply()
                Toast.makeText(requireContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupProfileData() {
        binding.tvProfileName.text = userNama
        binding.tvProfileSchool.text = "SMK Negeri 9 Medan"
        binding.tvProfileNis.text = "NISN:$userNis"
        binding.tvProfileRole.text = "Kelas: $userKelas"

        val prefs = requireContext().getSharedPreferences("profile_prefs", android.content.Context.MODE_PRIVATE)
        val photoPath = prefs.getString("photo_path_$userId", null)
        if (photoPath != null && File(photoPath).exists()) {
            val bitmap = android.graphics.BitmapFactory.decodeFile(photoPath)
            binding.ivProfilePhoto.setImageBitmap(bitmap)
        } else {
            binding.ivProfilePhoto.setImageResource(getProfilePhoto(userId))
        }

        binding.ivProfilePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun getProfilePhoto(id: Int): Int {
        return when (id) {
            1 -> R.drawable.ppjacobiambasukixiirpl3
            2 -> R.drawable.pparatanaokoxiifm2
            else -> R.drawable.siswa_kanan
        }
    }

    private fun setupStatusKehadiran() {
        val now = Date()
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        binding.tvStatusTanggal.text = dateFormat.format(now)
        binding.tvStatusJam.text = timeFormat.format(now)

        if (db.isHariLibur()) {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_notif_circle_white)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_kalendar)
            binding.ivStatusHariIni.setColorFilter(0xFF123B5D.toInt())
            binding.tvStatusValue.text = "Libur"
            binding.tvStatusDesc.text = "Hari ini tidak ada absensi"
            return
        }

        val absenHariIni = db.getAbsenHariIni(userId)

        if (absenHariIni == null) {
            binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_notif_circle_white)
            binding.ivStatusHariIni.setImageResource(R.drawable.ic_remove)
            binding.ivStatusHariIni.setColorFilter(0xFF123B5D.toInt())
            binding.tvStatusValue.text = "Belum Absen"
            binding.tvStatusDesc.text = "Segera lakukan absensi hari ini"
            return
        }

        when (absenHariIni.status.lowercase()) {
            "hadir" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_check)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Hadir"
                binding.tvStatusDesc.text = "Terima kasih sudah disiplin"
            }
            "telat" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_telat_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_warning)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Telat"
                binding.tvStatusDesc.text = "Datang lebih awal besok ya"
            }
            "absen", "alpha" -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_alpha_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_remove)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Absen"
                binding.tvStatusDesc.text = "Kamu tidak hadir hari ini"
            }
            else -> {
                binding.ivStatusHariIni.setBackgroundResource(R.drawable.bg_status_hadir_pill)
                binding.ivStatusHariIni.setImageResource(R.drawable.ic_check)
                binding.ivStatusHariIni.setColorFilter(0xFFFFFFFF.toInt())
                binding.tvStatusValue.text = "Hadir"
                binding.tvStatusDesc.text = "Terima kasih sudah disiplin"
            }
        }
    }

    private fun loadRekapKehadiran() {
        viewLifecycleOwner.lifecycleScope.launch {
            val (hadir, telat, absen) = withContext(Dispatchers.IO) {
                val dataDb = db.getRiwayatAbsen(userId)
                RiwayatProvider.rekap(userId, dataDb)
            }
            if (_binding != null) {
                binding.tvRekapHadir.text = "$hadir hari"
                binding.tvRekapAbsen.text = "$absen hari"
                binding.tvRekapTelat.text = "$telat hari"
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupActions() {
        setupTouchEffect(binding.btnPelajaran) {
            binding.overlayPelajaran.root.visibility = View.VISIBLE
            binding.overlayPelajaran.root.bringToFront()
        }

        setupTouchEffect(binding.btnRiwayat) {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_RIWAYAT)
        }

        setupTouchEffect(binding.btnLihatSemua) {
            (activity as? SiswaMainActivity)?.navigateToTab(SiswaMainActivity.TAB_RIWAYAT)
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun setupOverlayPelajaran() {
        binding.overlayPelajaran.btnTutup.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
        }

        binding.overlayPelajaran.blok1.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
            binding.overlayJadwal.root.visibility = View.VISIBLE
            binding.overlayJadwal.root.bringToFront()
        }

        binding.overlayPelajaran.blok2.setOnClickListener {
            binding.overlayPelajaran.root.visibility = View.GONE
            binding.overlayJadwal.root.visibility = View.VISIBLE
            binding.overlayJadwal.root.bringToFront()
        }
    }

    private fun setupOverlayJadwal() {
        binding.overlayJadwal.btnTutupJadwal.setOnClickListener {
            binding.overlayJadwal.root.visibility = View.GONE
            binding.overlayPelajaran.root.visibility = View.VISIBLE
            binding.overlayPelajaran.root.bringToFront()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchEffect(view: View, onClick: () -> Unit) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.95f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.95f).setDuration(100).start()
                    false
                }
                MotionEvent.ACTION_UP -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 0.95f, 1f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 0.95f, 1f).setDuration(100).start()
                    onClick()
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    ObjectAnimator.ofFloat(v, "scaleX", 0.95f, 1f).setDuration(100).start()
                    ObjectAnimator.ofFloat(v, "scaleY", 0.95f, 1f).setDuration(100).start()
                    false
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
