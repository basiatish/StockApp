package com.example.stocks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.databinding.ItemDividendsBinding
import com.example.stocks.models.remote.StockDividends

class StockDividendsAdapter : ListAdapter<StockDividends, StockDividendsAdapter.ViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDividendsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dividend = getItem(position)
        holder.bind(dividend)
    }

    class ViewHolder(private var binding: ItemDividendsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stockDividends: StockDividends) {
            binding.apply {
                divDate.text = stockDividends.label
                divValue.text = stockDividends.dividend.toString()
            }
        }

    }

    companion object DiffCallBack : DiffUtil.ItemCallback<StockDividends>() {
        override fun areItemsTheSame(oldItem: StockDividends, newItem: StockDividends): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: StockDividends, newItem: StockDividends): Boolean {
            return oldItem == newItem
        }
    }
}