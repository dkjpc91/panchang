package com.mithilakshar.mithilapanchang.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.Dialog.Mantradialog

import com.mithilakshar.mithilapanchang.databinding.MantraitemBinding
import com.squareup.picasso.Picasso

class mantraadapter(var datalist:  List<Map<String, Any?>>,var context: Context) : RecyclerView.Adapter<mantraadapter.mantraviewholder>() {


    fun updateData(newData: List<Map<String, Any?>>) {
        datalist = newData
        notifyDataSetChanged()
    }

    class mantraviewholder(val binding:MantraitemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Map<String, Any?>,context: Context){
            binding.apply {

                mantraName.text= model.get("mantraName").toString()
                Picasso .get()
                    .load(model.get("mantraImageurl").toString())  // Replace with your image URL
                    .into(binding.mantraImage)

            }
            binding.root.setOnClickListener {

                val Mantradialog = Mantradialog(it.context)
                Mantradialog.setmantradialogtext(model.get("mantraName").toString())
                Mantradialog.setmantradialogtext1(model.get("mantraDesc").toString())
                Mantradialog.setmantraimage(model.get("mantraImageurl").toString())
                Mantradialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                Mantradialog.show()

            }

        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mantraviewholder {

        val binding = MantraitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return mantraviewholder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: mantraviewholder, position: Int) {

        val currentdata=datalist.get(position)

        if (currentdata != null) {
            holder.bind(currentdata,context)
        }

    }


}
