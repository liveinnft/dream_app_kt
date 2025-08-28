package com.lionido.dream_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lionido.dream_app.R
import com.lionido.dream_app.model.DreamSymbol

class DreamSymbolAdapter : RecyclerView.Adapter<DreamSymbolAdapter.SymbolViewHolder>() {

    private var symbols: List<DreamSymbol> = emptyList()

    fun updateSymbols(newSymbols: List<DreamSymbol>) {
        symbols = newSymbols
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dream_symbol, parent, false)
        return SymbolViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolViewHolder, position: Int) {
        holder.bind(symbols[position])
    }

    override fun getItemCount(): Int = symbols.size

    inner class SymbolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symbolName: TextView = itemView.findViewById(R.id.symbol_name)
        private val symbolMeaning: TextView = itemView.findViewById(R.id.symbol_meaning)
        private val symbolFrequency: TextView = itemView.findViewById(R.id.symbol_frequency)

        fun bind(symbol: DreamSymbol) {
            symbolName.text = symbol.name
            symbolMeaning.text = symbol.meaning

            if (symbol.frequency > 1) {
                symbolFrequency.text = "×${symbol.frequency}"
                symbolFrequency.visibility = View.VISIBLE
            } else {
                symbolFrequency.visibility = View.GONE
            }
        }
    }
}