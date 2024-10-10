package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import com.mithilakshar.mithilapanchang.databinding.ItemAlarmDataBinding

class caladapter(private val context: Context, private var list: List<Map<String, Any?>>) : RecyclerView.Adapter<caladapter.calviewholder>() {
    private var tempList: MutableList<Map<String, String>> = mutableListOf()

    class calviewholder(val binding: ItemAlarmDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Map<String, Any?>) {
            binding.apply {

                val tithi =model.get("tithi")
                val date =model.get("date")
                val month =model.get("month")
                val day =model.get("day")
                binding.taskTitle.text= tithi.toString()
                binding.taskDate.text= "$day"+" " +"$date "+" "+" $month "
                binding.taskTime.text= " "

                // Uncomment and adjust this if you want to load images using Picasso
                /* Picasso.get()
                    .load(model["imageUrl"])  // Replace with your image URL key
                    .into(binding.holidayImage) */
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): calviewholder {
        val binding = ItemAlarmDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return calviewholder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: calviewholder, position: Int) {
        val currentData = list[position]
        holder.bind(currentData)
    }

    fun updateHolidays(newHolidays: List<Map<String, String>>) {
        list = newHolidays
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
