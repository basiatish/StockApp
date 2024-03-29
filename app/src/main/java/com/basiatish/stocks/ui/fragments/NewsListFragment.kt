package com.basiatish.stocks.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.basiatish.stocks.App
import com.basiatish.stocks.R
import com.basiatish.stocks.adapters.NewsListAdapter
import com.basiatish.stocks.adapters.OnNewsClickListener
import com.basiatish.stocks.databinding.FragmentNewsListBinding
import com.basiatish.stocks.utils.network.StockStatus
import com.basiatish.stocks.viewmodels.NewsViewModel

class NewsListFragment : Fragment(), OnNewsClickListener {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adapter: NewsListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var page = 1
    private var prevPage = 1
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
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pageInput.text = Editable.Factory.getInstance().newEditable(resources.getString(R.string.page_text, page))
        hideKeyBoard()
        if (viewModel.newsList.isEmpty()) {
            binding.apply {
                shimmer.visibility = View.VISIBLE
                shimmer.startShimmer()
            }
            viewModel.getNews(0, apiKey ?: "")
        }

        adapter = NewsListAdapter(this)
        binding.recycler.adapter = adapter
        layoutManager = LinearLayoutManager(context)
        binding.recycler.layoutManager = layoutManager

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.newsStatus.observe(this.viewLifecycleOwner) { status ->
            if (status == StockStatus.DONE) {
                binding.apply {
                    refresh.isRefreshing = false
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                }
                adapter.submitList(viewModel.newsList[0].content)
            } else if (status == StockStatus.ERROR) {
                binding.apply {
                    refresh.isRefreshing = false
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                }
                Toast.makeText(requireContext(), resources.getText(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {

        binding.btnNext.setOnClickListener {
            hideKeyBoard()
            page++
            prevPage = page
            binding.pageInput.text = Editable.Factory.getInstance().newEditable(resources.getString(
                R.string.page_text, page))
            viewModel.getNews(page - 1, apiKey ?: "")
        }

        binding.btnPrev.setOnClickListener {
            hideKeyBoard()
            if (page > 1) page--
            prevPage = page
            binding.pageInput.text = Editable.Factory.getInstance().newEditable(resources.getString(
                R.string.page_text, page))
            viewModel.getNews(page - 1, apiKey ?: "")
        }

        binding.pageInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyBoard()
                binding.pageInput.text = Editable.Factory.getInstance().newEditable("$page")
                binding.pageInput.setSelection("$page".length)
            }
            else {
                binding.pageInput.text = Editable.Factory.getInstance().newEditable(resources.getString(
                    R.string.page_text, page))
                hideKeyBoard()
            }
        }

        binding.pageInput.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                hideKeyBoard()
                return@OnKeyListener true
            }
            false
        })

        binding.pageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                prevPage = page
                page = binding.pageInput.text?.toString()?.toInt() ?: 0
                page = if (page > 0) binding.pageInput.text?.toString()?.toInt() ?: 0
                else prevPage

                if (prevPage != page) viewModel.getNews(page - 1, apiKey ?: "")

                hideKeyBoard()
            }
            true
        }

        binding.refresh.setOnRefreshListener {
            viewModel.getNews(page - 1, apiKey ?: "")
        }

    }

    private fun showKeyBoard() {
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.showSoftInput(binding.pageInput, 0)
    }

    private fun hideKeyBoard() {
        binding.pageInput.clearFocus()
        val inputMethodManager = requireContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.pageInput.windowToken, 0)
    }

    override fun onClick(id: Int) {
        val action = NewsListFragmentDirections.actionNewsListFragmentToNewsFragment(id)
        findNavController().navigate(action)
    }
}