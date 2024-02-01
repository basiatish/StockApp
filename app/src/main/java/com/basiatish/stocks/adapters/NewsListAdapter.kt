package com.basiatish.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.basiatish.stocks.databinding.ItemNewsListBinding
import com.basiatish.stocks.models.remote.news.NewsContent
import com.basiatish.stocks.utils.formatters.Formatter
import com.basiatish.stocks.utils.images.loadNewsImage

class NewsListAdapter(private val clickListener: OnNewsClickListener): ListAdapter<NewsContent, NewsListAdapter.ViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, holder.itemView.context)

        holder.itemView.setOnClickListener {
            clickListener.onClick(position)
        }
    }

    inner class ViewHolder(private var binding: ItemNewsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsContent, context: Context) {
            binding.newsTitle.text = news.title
            binding.newsDate.text = Formatter().parseDate(news.date)
            binding.newsTicker.text = Formatter().parseTicker(news.tickers)
            loadNewsImage(context, news.image ?: "", binding.newsImage)
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<NewsContent>() {
        override fun areItemsTheSame(oldItem: NewsContent, newItem: NewsContent): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NewsContent, newItem: NewsContent): Boolean {
            return oldItem.link == newItem.link
        }
    }
}

interface OnNewsClickListener {

    fun onClick(id: Int)
}