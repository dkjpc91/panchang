package com.mithilakshar.mithilapanchang.Adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

import com.mithilakshar.mithilapanchang.databinding.ItemGitachapterBinding

class GitaVerseAdapter(private val rows: List<Map<String, Any?>>) : RecyclerView.Adapter<GitaVerseAdapter.RowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemGitachapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val rowData = rows[position]
        holder.bind(rowData)
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    inner class RowViewHolder(private val binding: ItemGitachapterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(rowData: Map<String, Any?>) {
            binding.chaptername.text = rowData["chaptername"].toString()
            binding.chapterdesc.text = rowData["shlok"].toString()
            binding.shlokaCount.text = "श्लोक संख्या: "+rowData["id"].toString()

            val verseids = rowData["id"].toString()
            // Set click listener to navigate to details fragment with integer data
            binding.root.setOnClickListener {

                //val action = BhagwatGitaVerseFragmentDirections.actionBhagwatGitaVerseFragmentToBhagwatGitaDetailsverseFragment(verseids.toInt())
               // binding.root.findNavController().navigate(action)
            }
        }
    }
}
