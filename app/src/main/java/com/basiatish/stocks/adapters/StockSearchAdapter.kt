package com.basiatish.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.basiatish.stocks.databinding.ItemStockSearchBinding
import com.basiatish.stocks.models.remote.StockSearch
import com.basiatish.stocks.utils.images.loadLogo

class StockSearchAdapter(private val onItemClickListener: OnItemClick, private val landscape: Boolean) :
    ListAdapter<StockSearch, StockSearchAdapter.ViewHolder>(
        DiffCallBack
    ) {

    class ViewHolder(private var binding: ItemStockSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stockSearch: StockSearch, context: Context, landscape: Boolean) {
            binding.apply {
                if (!landscape && stockSearch.name?.length!! > 12) {
                    compName.text = "${stockSearch.name?.subSequence(0, 12)?.trim()}..."
                } else {
                    compName.text = stockSearch.name
                }
                shortCompName.text = stockSearch.symbol
                exchangeShortName.text = stockSearch.exchangeShortName
                exchangeName.text = stockSearch.stockExchange
                if (stockSearch.favorite) {
                    favorite.visibility = View.VISIBLE
                } else {
                    favorite.visibility = View.GONE
                }
            }
            loadLogo(
                context,
                "https://financialmodelingprep.com/image-stock/" + "${stockSearch.symbol}.png",
                stockSearch.symbol!!,
                binding.compLogo
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stock = getItem(position)
        holder.bind(stock, holder.itemView.context, landscape)
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(stock)
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<StockSearch>() {
        override fun areItemsTheSame(oldItem: StockSearch, newItem: StockSearch): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: StockSearch, newItem: StockSearch): Boolean {
            return oldItem.symbol == newItem.symbol
        }
    }
}

interface OnItemClick {
    fun onClick(item: StockSearch)
}