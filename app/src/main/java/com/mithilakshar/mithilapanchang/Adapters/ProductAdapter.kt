package com.mithilakshar.mithilapanchang.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.Models.Product
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.databinding.PurchaseitemBinding

class ProductAdapter(private var datalist: List<Product>,private val onBuyButtonClick: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: PurchaseitemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, onBuyButtonClick: (Product) -> Unit) {
            Log.d("ProductAdapter", "Binding product: Title: ${product.title}, Description: ${product.description}")

            binding.apply {
                productTitle.text = TranslationUtils.producttranslate(product.title)
                productPrice.text = product.price


                // Uncomment and use Picasso if needed
                // Picasso.get()
                //     .load(product.imageUrl)  // Load image using Picasso
                //     .into(binding.productImage)
            }
            binding.buyButton.setOnClickListener {
                Log.d("ProductViewHolder", "Buy button clicked for SKU: ${product.sku}")
                onBuyButtonClick(product)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        Log.d("ProductAdapter", "Creating new ViewHolder")
        val binding = PurchaseitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        Log.d("ProductAdapter", "Binding ViewHolder at position: $position")
        val product = datalist[position]
        holder.bind(product,onBuyButtonClick)
    }

    override fun getItemCount(): Int {
        val size = datalist.size
        Log.d("ProductAdapter", "getItemCount called: $size")
        return size
    }


    fun updateData(newData: List<Product>) {
        Log.d("ProductAdapter", "Updating data. New data size: ${newData.size}")

        // Log details of each product being updated
        newData.forEach { product ->
            Log.d("ProductAdapter", "Updated product: Title: ${product.title}, Description: ${product.description}, Price: ${product.price}, SKU: ${product.sku}")
        }

        datalist = newData
        notifyDataSetChanged()
        Log.d("ProductAdapter", "New item count: ${getItemCount()}")
    }
}
