package com.mithilakshar.mithilapanchang.Adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.databinding.HolidaybanneritemBinding

class SliderAdapter(private val itemList: List<Map<String, String>>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private val TAG = "SliderAdapter"

    init {
        logInputItemList() // Log the input item list when the adapter is initialized
    }

    private fun logInputItemList() {
        Log.d(TAG, "Input item list: $itemList") // Log the entire item list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = HolidaybanneritemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {

        val itemIndex = position / 2
        val item = itemList[itemIndex]
        val useCustomFont = position % 2 != 0
        holder.bind(item,useCustomFont)
    }

    override fun getItemCount(): Int = itemList.size

    class SliderViewHolder(private val binding: HolidaybanneritemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Map<String, String>,useCustomFont: Boolean) {
            val name = item["name"] ?: ""
            val date = item["date"] ?: ""
            val description = item["desc"] ?: ""
            Log.d(TAG, "Binding item: $item")
            binding.holidayname.text = name
            binding.holidaygreeting.text = "$date\n$description"

            val typeface = if (useCustomFont) {
                // Get the custom typeface from res/font
                ResourcesCompat.getFont(binding.root.context, R.font.kalam)
            } else {

                ResourcesCompat.getFont(binding.root.context, R.font.kalam)

            }

            binding.holidayname.typeface = typeface
            binding.holidaygreeting.typeface = typeface

        }
    }
}
