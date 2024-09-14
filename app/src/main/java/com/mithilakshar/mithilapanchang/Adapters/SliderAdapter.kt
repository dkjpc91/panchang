package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.font.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.databinding.HolidaybanneritemBinding

class SliderAdapter(private val itemList: List<Map<String, String>>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    // Create a new list where each item is duplicated
  /*  private val modifiedList: List<Map<String, String>> = itemList.flatMap { item ->
        listOf(item, item) // Each item appears twice
    }*/

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
