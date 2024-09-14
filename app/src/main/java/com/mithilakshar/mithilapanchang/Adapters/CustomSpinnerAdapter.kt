package com.mithilakshar.mithilapanchang.Adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mithilakshar.mithilapanchang.R

class CustomSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val items: List<SpinnerItem>
) : ArrayAdapter<CustomSpinnerAdapter.SpinnerItem>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val item = getItem(position) ?: return view

        val icon = view.findViewById<ImageView>(R.id.spinner_icon)
        val text = view.findViewById<TextView>(R.id.spinner_text)

        icon.setImageResource(item.iconResId)
        text.text = item.text

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_dropdown_item, parent, false)
        val item = getItem(position) ?: return view

        val icon = view.findViewById<ImageView>(R.id.spinner_dropdown_icon)
        val text = view.findViewById<TextView>(R.id.spinner_dropdown_text)

        icon.setImageResource(item.iconResId)
        text.text = item.text

        return view
    }

    // SpinnerItem.kt
    data class SpinnerItem(
        val iconResId: Int,
        val text: String
    )
}
