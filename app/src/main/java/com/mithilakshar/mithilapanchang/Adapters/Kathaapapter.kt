package com.mithilakshar.mithilapanchang.Adapters

import android.content.Intent
import android.view.LayoutInflater

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.mithilakshar.mithilapanchang.UI.View.KathaDescriptionActivity
import com.mithilakshar.mithilapanchang.databinding.EclipseitemBinding
import com.mithilakshar.mithilapanchang.databinding.KathaitemBinding
import com.squareup.picasso.Picasso

class kathaapapter(var datalist: List<Map<String, Any?>>) : RecyclerView.Adapter<kathaapapter.kathaviewholder>() {

    class kathaviewholder(val binding:KathaitemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Map<String, Any?>){
            binding.apply {

                kathaName.text=model.get("kathaname").toString()
                kathaStory.text=model.get("kathadescription").toString()
                Picasso .get()
                    .load(model.get("kathaimage").toString())  // Replace with your image URL
                    .into(binding.kathaImage)

            }

            binding.root.setOnClickListener {

                val i = Intent(it.context, KathaDescriptionActivity::class.java)
                i.putExtra("kathaName", model.get("kathaname").toString())
                i.putExtra("kathaStory", model.get("kathadescription").toString())
                i.putExtra("kathaUrl", model.get("kathaimage").toString())
                i.putExtra("audioURL", model.get("mantraName").toString())
                it.context.startActivity(i)

            }
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): kathaviewholder {
        val binding = KathaitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return kathaviewholder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: kathaviewholder, position: Int) {

        val currentdata=datalist.get(position)

        if (currentdata != null) {
            holder.bind(currentdata)
        }

    }


}
