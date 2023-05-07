package com.example.stocks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.stocks.R
import com.example.stocks.databinding.FragmentNewsBinding
import com.example.stocks.models.remote.news.NewsContent
import com.example.stocks.utils.images.loadNewsImage
import com.example.stocks.viewmodels.NewsViewModel


class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val navArg: NewsFragmentArgs by navArgs()

    private val viewModel: NewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUniqueNews(navArg.id)
        setupObservers()
        setupListener()
    }

    private fun setupObservers() {
        viewModel.news.observe(this.viewLifecycleOwner) { news ->
            if (!news.content.isNullOrEmpty()) {
                bind(news)
            }
        }
    }

    private fun setupListener() {
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bind(news: NewsContent) {
        binding.apply {
            title.text = news.title
            loadNewsImage(requireContext(), news.image!!, binding.newsImage)
            newsTicker.text = viewModel.formatTicker(news.tickers!!)
            newsDate.text = viewModel.formatDate(news.date!!)
            newsContent.text = viewModel.formatText(news.content!!)
            author.text = resources.getString(R.string.author, news.author)
        }
    }
}