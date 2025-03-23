package com.mithilakshar.mithilapanchang.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.databinding.HolidayitemBinding

class TithiAdapter(
    private val context: Context,
    private var tithiList: List<Map<String, String>>
) : RecyclerView.Adapter<TithiAdapter.TithiViewHolder>() {

    // ViewHolder class
    class TithiViewHolder(val binding: HolidayitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Map<String, String>) {
            binding.apply {
                // Bind Tithi data to the views
                holidayName.text = model["Hindi Tithi"] // Tithi name
                holidayDesc.text = model["Hindi Timinig"] // Timing
                holidaydetails.text = model[""] // Hindi Tithi
                // If you have a field for Hindi Timing, you can bind it here
                // For example:
                // hindiTimingView.text = model["Hindi Timinig"]
            }
        }
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TithiViewHolder {
        val binding = HolidayitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TithiViewHolder(binding)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int {
        return tithiList.size
    }

    // Bind data to the views
    override fun onBindViewHolder(holder: TithiViewHolder, position: Int) {
        val currentData = tithiList[position]
        if (currentData != null) {
            holder.bind(currentData)
        }
    }

    // Update the Tithi list and notify the adapter
    fun updateTithiList(newTithiList: List<Map<String, String>>) {
        tithiList = newTithiList
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}