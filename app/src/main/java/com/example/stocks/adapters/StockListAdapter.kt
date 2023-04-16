package com.example.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stocks.R
import com.example.stocks.database.stocksdatabase.Stock
import com.example.stocks.databinding.ItemStockListBinding

class StockListAdapter : ListAdapter<Stock, StockListAdapter.ViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, holder.itemView.context)
    }

    inner class ViewHolder(private var binding: ItemStockListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stock: Stock, context: Context) {
            binding.apply {
                shortCompName.text = stock.shortName
                compName.text = stock.name
                compPrice.text = context.getString(R.string.price, stock.price.toString())
                priceChange.apply {
                    text = if(stock.priceChange < 0) stock.priceChange.toString() else "+${stock.priceChange}"
                    if (stock.priceChange >= 0) setTextColor(resources.getColor(R.color.green, context.theme))
                    else setTextColor(resources.getColor(R.color.red, context.theme))
                }
                percentChange.apply {
                    text = context.getString(R.string.price_percent, stock.priceChangePercent.toString())
                    if (stock.priceChangePercent >= 0) setTextColor(resources.getColor(R.color.green, context.theme))
                    else setTextColor(resources.getColor(R.color.red, context.theme))
                }
                Glide.with(context).load(stock.url).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_warning)
                    .centerCrop().into(binding.compLogo)
            }
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.shortName == newItem.shortName
        }
    }
}