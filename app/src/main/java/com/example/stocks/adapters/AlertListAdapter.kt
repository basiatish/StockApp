package com.example.stocks.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.R
import com.example.stocks.database.alertdatabase.Alert
import com.example.stocks.databinding.ItemAlertListBinding

class AlertListAdapter(private val onClick: OnItemClickListener) : ListAdapter<Alert, AlertListAdapter.ViewHolder>(DiffCallBack) {

    private var selection: Boolean = false
    private var numSelected: Int = 0

    inner class ViewHolder(private var binding: ItemAlertListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert, context: Context) {
            binding.apply {
                price.text = context.getString(R.string.alert_price, alert.price)

                if (alert.above) {
                    title.setText(R.string.price_is_higher)
                } else {
                    title.setText(R.string.price_is_lower)
                }

                binding.alarmSwitch.isChecked = alert.status
            }
            binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                onClick.onSwitchStateChange(alert, isChecked)
            }
        }

        fun selectAlert(alert: Alert) {
            val isSelected = binding.alertContainer.isSelected
            if (isSelected) {
                Log.i("Remove", "Selected before")
                binding.alertContainer.isSelected = false
                numSelected--
                onClick.onLongClick(alert, false)
                if (numSelected == 0) selection = false
            } else {
                Log.i("Remove", "New selection")
                binding.alertContainer.isSelected = true
                numSelected++
                Log.i("Remove", "Num selected: $numSelected")
                onClick.onLongClick(alert, true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, holder.itemView.context)
        holder.itemView.setOnClickListener {
            if (selection) holder.selectAlert(current)
            else onClick.onItemClick(current)
        }
        holder.itemView.setOnLongClickListener {
            selection = true
            holder.selectAlert(current)
            true
        }
    }

    override fun onCurrentListChanged(previousList: MutableList<Alert>, currentList: MutableList<Alert>) {
        numSelected = 0
        selection = false
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Alert>() {

        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(alert: Alert)

    fun onSwitchStateChange(alert: Alert, state: Boolean)

    fun onLongClick(alert: Alert, deleteFlag: Boolean)
}