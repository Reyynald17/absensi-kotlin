package com.example.absensisiswaproto.ui.guru

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.absensisiswaproto.data.local.DatabaseHelper
import com.example.absensisiswaproto.databinding.FragmentSearchKelasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchKelasFragment : Fragment() {

    private var _binding: FragmentSearchKelasBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: KelasSearchAdapter
    private var allKelas: List<KelasSearchItem> = emptyList()

    companion object {
        fun newInstance(): SearchKelasFragment {
            return SearchKelasFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchKelasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        setupSearch()
        setupRecyclerView()
        loadKelasAsync()
    }

    private fun setupSearch() {
        binding.etSearchKelas.doAfterTextChanged { editable ->
            filterKelas(editable?.toString().orEmpty())
        }
    }

    private fun setupRecyclerView() {
        adapter = KelasSearchAdapter(emptyList()) { kelas ->
            val intent = Intent(requireContext(), SearchKelasDetailActivity::class.java).apply {
                putExtra("EXTRA_KELAS", kelas.nama)
            }
            startActivity(intent)
        }
        binding.rvKelasSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvKelasSearch.adapter = adapter
    }

    private fun loadKelasAsync() {
        viewLifecycleOwner.lifecycleScope.launch {
            val raw = withContext(Dispatchers.IO) {
                db.getAllKelasWithCount()
            }
            allKelas = raw.map { KelasSearchItem(it.first, it.second) }
            if (_binding != null) {
                filterKelas(binding.etSearchKelas.text.toString())
            }
        }
    }

    private fun filterKelas(keyword: String) {
        val filtered = if (keyword.isBlank()) {
            allKelas
        } else {
            allKelas.filter {
                it.nama.contains(keyword, ignoreCase = true)
            }
        }

        adapter.updateData(filtered)

        if (filtered.isEmpty()) {
            binding.layoutEmptySearch.visibility = View.VISIBLE
            binding.rvKelasSearch.visibility = View.GONE
        } else {
            binding.layoutEmptySearch.visibility = View.GONE
            binding.rvKelasSearch.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
