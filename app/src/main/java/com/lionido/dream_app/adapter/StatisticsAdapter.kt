package com.lionido.dream_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lionido.dream_app.R

class StatisticsAdapter : RecyclerView.Adapter<StatisticsAdapter.StatViewHolder>() {

    data class StatItem(
        val name: String,
        val count: Int
    )

    private var items: List<StatItem> = emptyList()

    fun updateItems(newItems: List<StatItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statistic, parent, false)
        return StatViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class StatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.stat_item_name)
        private val itemCount: TextView = itemView.findViewById(R.id.stat_item_count)

        fun bind(item: StatItem) {
            itemName.text = item.name
            itemCount.text = item.count.toString()
        }
    }
}