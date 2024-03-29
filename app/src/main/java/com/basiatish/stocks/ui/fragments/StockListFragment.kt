package com.basiatish.stocks.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.basiatish.stocks.App
import com.basiatish.stocks.R
import com.basiatish.stocks.adapters.OnClickListener
import com.basiatish.stocks.adapters.StockListAdapter
import com.basiatish.stocks.database.stocksdatabase.Stock
import com.basiatish.stocks.databinding.FragmentStockListBinding
import com.basiatish.stocks.utils.network.StockStatus
import com.basiatish.stocks.viewmodels.StockListViewModel
import com.basiatish.stocks.viewmodels.StockListViewModelFactory

class StockListFragment() : Fragment(), OnClickListener {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StockListViewModel by viewModels {
        StockListViewModelFactory((requireContext().applicationContext as App).stockDataBase.stockDao())
    }

    private lateinit var adapter: StockListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var updated = false

    private var apiKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val navArg: StockListFragmentArgs by navArgs()
            if (!navArg.apiKey.isNullOrEmpty()) {
                apiKey = navArg.apiKey
                (activity?.applicationContext as App).saveApiKey(apiKey!!)
            }
        } catch (e: Exception) {
            apiKey = (activity?.applicationContext as App).getApiKey()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        binding.shimmer.apply {
            startShimmer()
        }
        viewModel.getStocks()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTextView()
        binding.topBarText.setText(getString(R.string.your_watchlist))
        adapter = StockListAdapter(this, false, viewModel.removeStockList)
        binding.recycler.adapter = adapter
        layoutManager = LinearLayoutManager(context)
        binding.recycler.layoutManager = layoutManager

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.stockListStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                if (viewModel.stockList.isNotEmpty() && !updated) {
                    Log.e("UPDATE", "Updated again")
                    viewModel.getStockPrice(apiKey ?: "")
                    updated = true
                } else {
                    binding.shimmer.stopShimmer()
                    binding.shimmer.visibility = View.GONE
                    binding.emptyList.visibility = View.GONE
                    binding.refresh.isRefreshing = false
                    adapter.submitList(viewModel.stockList)
                    binding.refresh.isEnabled = viewModel.stockList.isNotEmpty()
                    updated = true
                    if (viewModel.stockList.isEmpty()) binding.emptyList.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isRemoveListEmpty.observe(this.viewLifecycleOwner) { status ->
            if (status) {
                binding.topBarText.setText(getString(R.string.your_watchlist))
                binding.deleteBtn.visibility = View.GONE
            } else {
                binding.topBarText.setText(getString(R.string.delete_stocks))
                binding.deleteBtn.visibility = View.VISIBLE
            }
        }

        viewModel.quoteStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                viewModel.updateStock()
            }
            if (status == StockStatus.ERROR) {
                Toast.makeText(requireContext(), resources.getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                viewModel.getStocks()
            }
        }
    }

    private fun setupListeners() {
        binding.deleteBtn.setOnClickListener {
            viewModel.deleteStock()
        }

        binding.refresh.setOnRefreshListener {
            adapter.submitList(listOf())
            binding.shimmer.visibility = View.VISIBLE
            binding.shimmer.startShimmer()
            viewModel.getStockPrice(apiKey ?: "")
        }
    }

    private fun setupTextView() {
        binding.topBarText.setFactory {
            val textView = TextView(context)
            textView.apply {
                gravity = Gravity.CENTER_VERTICAL
                textSize = 24F
                setTextAppearance(R.style.DefaultTextApp)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adapter = StockListAdapter(this, true, viewModel.removeStockList)
            binding.recycler.adapter = adapter
            adapter.submitList(viewModel.stockList)
        } else {
            adapter = StockListAdapter(this, false, viewModel.removeStockList)
            binding.recycler.adapter = adapter
            adapter.submitList(viewModel.stockList)
        }
    }

    override fun onItemClick(stock: Stock) {
        val action = StockListFragmentDirections.actionStockListFragmentToStockOverViewFragment(
            stock.name,
            stock.shortName
        )
        findNavController().navigate(action)
    }

    override fun onLongClick(stock: Stock, state: Boolean) {
        viewModel.updateStockList(stock, state)
    }
}