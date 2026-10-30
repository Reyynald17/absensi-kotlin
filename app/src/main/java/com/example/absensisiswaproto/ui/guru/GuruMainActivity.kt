package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.absensisiswaproto.R
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.ActivityGuruMainBinding
import com.example.absensisiswaproto.ui.component.PressEffect

class GuruMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuruMainBinding
    private lateinit var db: DatabaseHelper

    var guruId: Int = 0
        private set
    var guruNip: String = ""
        private set
    var guruNama: String = ""
        private set
    var guruKelas: String = ""
        private set

    private var currentTab: String = TAB_HOME

    companion object {
        const val TAB_HOME = "tab_home"
        const val TAB_SEARCH = "tab_search"
        const val TAB_PROFILE = "tab_profile"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGuruMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        db = DatabaseHelper(this)

        guruId = intent.getIntExtra("EXTRA_GURU_ID", 0)
        guruNip = intent.getStringExtra("EXTRA_GURU_NIP") ?: ""
        guruNama = intent.getStringExtra("EXTRA_GURU_NAMA") ?: "Guru"
        guruKelas = intent.getStringExtra("EXTRA_GURU_KELAS") ?: "-"

        if (guruId == 0) {
            guruId = GuruSession.getGuruId(this)
            if (guruId <= 0) {
                startActivity(Intent(this, LoginGuruActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
                return
            } else {
                guruNip = GuruSession.getNip(this)
                guruNama = GuruSession.getNama(this)
            }
        } else {
            GuruSession.save(this, guruId, guruNip, guruNama)
        }

        val openTab = intent.getStringExtra("EXTRA_OPEN_TAB") ?: TAB_HOME
        if (savedInstanceState == null) {
            showFragment(openTab, getFragmentForTab(openTab))
        }

        setupBottomNav()
        setupOnBackPressed()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val openTab = intent.getStringExtra("EXTRA_OPEN_TAB") ?: TAB_HOME
        navigateToTab(openTab)
    }

    private fun getFragmentForTab(tabName: String): Fragment {
        return when (tabName) {
            TAB_SEARCH -> SearchKelasFragment.newInstance()
            TAB_PROFILE -> ProfileGuruFragment.newInstance(guruId, guruNip, guruNama, guruKelas)
            else -> BerandaGuruFragment.newInstance(guruId, guruNip, guruNama, guruKelas)
        }
    }

    private fun setupBottomNav() {
        PressEffect.attach(binding.bottomNav.btnHomeGuru, 0.88f)
        PressEffect.attach(binding.bottomNav.btnSearchGuru, 0.88f)
        PressEffect.attach(binding.bottomNav.btnProfileGuru, 0.88f)

        binding.bottomNav.btnHomeGuru.setOnClickListener {
            if (currentTab != TAB_HOME) {
                showFragment(TAB_HOME, getFragmentForTab(TAB_HOME))
            }
        }

        binding.bottomNav.btnSearchGuru.setOnClickListener {
            if (currentTab != TAB_SEARCH) {
                showFragment(TAB_SEARCH, getFragmentForTab(TAB_SEARCH))
            }
        }

        binding.bottomNav.btnProfileGuru.setOnClickListener {
            if (currentTab != TAB_PROFILE) {
                showFragment(TAB_PROFILE, getFragmentForTab(TAB_PROFILE))
            }
        }
    }

    fun navigateToTab(tabName: String) {
        showFragment(tabName, getFragmentForTab(tabName))
    }

    private fun showFragment(tag: String, fragment: Fragment) {
        currentTab = tag
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.guruFragmentContainer, fragment, tag)
            .commit()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentTab != TAB_HOME) {
                    navigateToTab(TAB_HOME)
                } else {
                    finish()
                }
            }
        })
    }
}
