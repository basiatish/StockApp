package com.example.stocks.ui.fragments

import android.content.Context
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stocks.*
import com.example.stocks.databinding.FragmentStockSearchBinding
import com.example.stocks.models.remote.StockSearch
import com.example.stocks.utils.network.StockStatus

class StockSearchFragment() : Fragment(), OnItemClick {

    private val viewModel: StockSearchViewModel by viewModels()

    private var _binding: FragmentStockSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val adapter = StockSearchAdapter(this)
        binding.recycler.adapter = adapter

        val layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recycler.layoutManager = layoutManager

        viewModel.companies.observe(this.viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.cancelButton.setOnClickListener {
            hideKeyBoard()
            findNavController().navigateUp()
        }

        binding.searchBar.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (binding.searchBar.text.toString() != "") {
                    hideKeyBoard()
                    viewModel.findCompany(binding.searchBar.text.toString())
                        .observe(this.viewLifecycleOwner) {
                            adapter.submitList(it)
                        }
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

        viewModel.status.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                StockStatus.LOADING -> binding.progressBar.show()
                StockStatus.DONE -> binding.progressBar.hide()
                StockStatus.ERROR -> {
                    binding.progressBar.hide()
                    binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                }
            }
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity()
            .currentFocus?.windowToken, 0)
    }

    override fun onClick(item: StockSearch) {
        val action = StockSearchFragmentDirections
            .actionStockSearchFragmentToStockOverViewFragment(item.name!!, item.symbol!!)
        findNavController().navigate(action)
    }
}