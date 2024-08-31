package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.databinding.HolidayitemBinding
import com.squareup.picasso.Picasso
import android.content.Context

class holidayadapter(private val context: Context, private val holidays: List<Map<String, String>>) :  RecyclerView.Adapter<holidayadapter.holidayviewholder>() {

    class holidayviewholder(val binding:HolidayitemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Map<String, String>){
            binding.apply {

                holidayDesc.text=model.get("name")
                holidayName.text=model.get("date")
                holidaydetails.text=model.get("desc")
             /*  Picasso .get()
                    .load(model.imageUrl)  // Replace with your image URL
                    .into(binding.holidayImage)*/

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holidayviewholder {
        val binding = HolidayitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return holidayviewholder(binding)
    }

    override fun getItemCount(): Int {
        return holidays.size
    }

    override fun onBindViewHolder(holder: holidayviewholder, position: Int) {
        val currentdata=holidays.get(position)

        if (currentdata != null) {
            holder.bind(currentdata)
        }

    }


}
