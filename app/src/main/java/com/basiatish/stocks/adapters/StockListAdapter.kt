package com.basiatish.stocks.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.basiatish.stocks.R
import com.basiatish.stocks.database.stocksdatabase.Stock
import com.basiatish.stocks.databinding.ItemStockListBinding
import com.basiatish.stocks.utils.images.loadLogo

class StockListAdapter(
    private val onClick: OnClickListener,
    private val landscape: Boolean,
    private val reselectList: List<Stock>
) :
    ListAdapter<Stock, StockListAdapter.ViewHolder>(DiffCallBack) {

    private var selection: Boolean = false
    private var numSelected: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        if (reselectList.isNotEmpty()) {
            numSelected = reselectList.size
            selection = true
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, holder.itemView.context)

        holder.itemView.setOnClickListener {
            if (selection) holder.selectStock(current)
            else onClick.onItemClick(current)
        }

        holder.itemView.setOnLongClickListener {
            selection = true
            holder.selectStock(current)
            true
        }
    }

    inner class ViewHolder(private var binding: ItemStockListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(stock: Stock, context: Context) {
            binding.apply {
                shortCompName.text = stock.shortName
                compPrice.text = context.getString(R.string.price, stock.price.toString())
                if (!landscape && stock.name?.length!! > 22) {
                    compName.text = context.getString(R.string.stock_item_desc, stock.name?.subSequence(0, 22)?.trim())
                } else {
                    compName.text = stock.name
                }
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
                loadLogo(context, stock.url, stock.shortName, binding.compLogo)
                if (reselectList.contains(stock)) {
                    item.isSelected = true
                }
            }
        }

        fun selectStock(stock: Stock) {
            val isSelected = binding.item.isSelected
            if (isSelected) {
                Log.i("Remove", "Selected before")
                binding.item.isSelected = false
                numSelected--
                onClick.onLongClick(stock, false)
                if (numSelected == 0) selection = false
            } else {
                Log.i("Remove", "New selection")
                binding.item.isSelected = true
                numSelected++
                Log.i("Remove", "Num selected: $numSelected")
                onClick.onLongClick(stock, true)
            }
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<Stock>,
        currentList: MutableList<Stock>
    ) {
        numSelected = 0
        selection = false
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

interface OnClickListener {
    fun onItemClick(stock: Stock)

    fun onLongClick(stock: Stock, state: Boolean)
}