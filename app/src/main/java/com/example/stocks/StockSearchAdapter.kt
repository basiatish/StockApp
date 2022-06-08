package com.example.stocks

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.databinding.ItemStockSearchBinding

class StockSearchAdapter(private val onItemClickListener: OnItemClick): ListAdapter<StockSearch, StockSearchAdapter.ViewHolder>(DiffCallBack) {

    class ViewHolder(private var binding: ItemStockSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var addButton: AppCompatImageButton

        fun bind(stockSearch: StockSearch) {
            addButton = binding.addBtn
            binding.compName.text = stockSearch.name
            binding.shortCompName.text = stockSearch.symbol
            binding.exchange.text = stockSearch.exchangeShortName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemStockSearchBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stock = getItem(position)
        holder.bind(stock)
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(stock)
        }
        holder.addButton.setOnClickListener {
            holder.addButton.isSelected = !holder.addButton.isSelected
        }
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<StockSearch>() {
        override fun areItemsTheSame(oldItem: StockSearch, newItem: StockSearch): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: StockSearch, newItem: StockSearch): Boolean {
            return oldItem.symbol == newItem.symbol
        }
    }
}

interface OnItemClick {
    fun onClick(item: StockSearch)
}