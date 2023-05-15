package com.example.stocks.ui.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stocks.App
import com.example.stocks.R
import com.example.stocks.adapters.OnItemClick
import com.example.stocks.adapters.StockSearchAdapter
import com.example.stocks.databinding.FragmentStockSearchBinding
import com.example.stocks.models.remote.StockSearch
import com.example.stocks.utils.network.StockStatus
import com.example.stocks.viewmodels.StockSearchViewModel
import com.example.stocks.viewmodels.StockSearchViewModelFactory

class StockSearchFragment() : Fragment(), OnItemClick {

    private val viewModel: StockSearchViewModel by viewModels {
        StockSearchViewModelFactory((requireContext().applicationContext as App).stockDataBase.stockDao())
    }

    private var _binding: FragmentStockSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StockSearchAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var apiKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiKey = (activity?.applicationContext as App).getApiKey()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StockSearchAdapter(this, false)
        binding.recycler.adapter = adapter

        layoutManager = LinearLayoutManager(context)
        binding.recycler.layoutManager = layoutManager

        setupObservers()
        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.companies.isNotEmpty()) {
            viewModel.createCompaniesList()
        }
    }

    private fun setupObservers() {

        viewModel.requestStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                StockStatus.LOADING -> {
                    adapter.submitList(listOf())
                    binding.apply {
                        shimmer.visibility = View.VISIBLE
                        shimmer.startShimmer()
                    }
                }
                StockStatus.DONE -> {
                    viewModel.createCompaniesList()
                }
                StockStatus.ERROR -> {
                    adapter.submitList(listOf())
                    binding.apply {
                        shimmer.stopShimmer()
                        shimmer.visibility = View.GONE
                    }
                    binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                }
            }
        }

        viewModel.listStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                binding.apply {
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                }
                adapter.submitList(viewModel.companiesList)
            }
        }
    }

    private fun setupListeners() {
        binding.cancelButton.setOnClickListener {
            hideKeyBoard()
            findNavController().navigateUp()
        }

        binding.searchBar.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (binding.searchBar.text.toString() != "") {
                    hideKeyBoard()
                    viewModel.findCompany(binding.searchBar.text.toString(), apiKey ?: "")
                    binding.searchBar.clearFocus()
                }
                return@OnKeyListener true
            }
            false
        })

        binding.searchBar.doOnTextChanged { text, _, _, _ ->
            if (text?.length != 0) {
                binding.clearText.visibility = View.VISIBLE
            } else {
                binding.clearText.visibility = View.GONE
            }
        }

        binding.clearText.setOnClickListener {
            binding.searchBar.text?.clear()
            binding.searchBar.isFocused
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity()
            .currentFocus?.windowToken, 0)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adapter = StockSearchAdapter(this, true)
            binding.recycler.adapter = adapter
            adapter.submitList(viewModel.companies)
        } else {
            adapter = StockSearchAdapter(this, false)
            binding.recycler.adapter = adapter
            adapter.submitList(viewModel.companies)
        }
    }

    override fun onClick(item: StockSearch) {
        val action = StockSearchFragmentDirections
            .actionStockSearchFragmentToStockOverViewFragment(item.name!!, item.symbol!!)
        findNavController().navigate(action)
    }
}