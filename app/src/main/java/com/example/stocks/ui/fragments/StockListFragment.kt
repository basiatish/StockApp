package com.example.stocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stocks.App
import com.example.stocks.adapters.StockListAdapter
import com.example.stocks.databinding.FragmentStockListBinding
import com.example.stocks.utils.network.StockStatus
import com.example.stocks.viewmodels.StockListViewModel
import com.example.stocks.viewmodels.StockListViewModelFactory

class StockListFragment() : Fragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StockListViewModel by viewModels {
        StockListViewModelFactory((requireContext().applicationContext as App).stockDataBase.stockDao())
    }

    private lateinit var adapter: StockListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StockListAdapter()
        binding.recycler.adapter = adapter
        layoutManager = LinearLayoutManager(context)
        binding.recycler.layoutManager = layoutManager

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStocks()
    }

    private fun setupObservers() {

        viewModel.stockListStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                adapter.submitList(viewModel.stockList)
            }
        }
    }
}