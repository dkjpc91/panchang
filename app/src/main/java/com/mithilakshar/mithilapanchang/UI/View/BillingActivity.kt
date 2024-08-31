package com.mithilakshar.mithilapanchang.UI.View

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.Adapters.ProductAdapter
import com.mithilakshar.mithilapanchang.Models.Product
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Utility.BillingManager
import com.mithilakshar.mithilapanchang.databinding.ActivityBillingBinding
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant

class BillingActivity : AppCompatActivity() {
    lateinit var binding: ActivityBillingBinding

    private lateinit var updatesDao: UpdatesDao

    private lateinit var billingManager: BillingManager
    private lateinit var productAdapter: ProductAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityBillingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.upiid.setOnClickListener {
            copyTextViewContentToClipboard(this,binding.upiid)

        }



       // billingManager = BillingManager(this)




       // setupUI()



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        val recyclerView: RecyclerView = binding.billingrecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList()){

            purchaseProduct(it)
        }
        recyclerView.adapter = productAdapter

    }

    fun updateProductList(products: List<Product>) {

        Log.d("BillingManager", "Updating product list with ${products.size} items")

        // Log details of each product
        products.forEach { product ->
            Log.d("BillingManager", "Product Details - Title: ${product.title}, Description: ${product.description}, Price: ${product.price}, SKU: ${product.sku}")
        }

      runOnUiThread {
          productAdapter.updateData(products)

      }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun purchaseProduct(product: Product) {
       billingManager.launchPurchaseFlow(this, product.sku)

    }

    fun updatebillingdao(updates: Updates){

        lifecycleScope.launch {
            updatesDao.insert(updates)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTimestamp(): Long {
        return Instant.now().toEpochMilli()
    }

    fun copyTextViewContentToClipboard(context: Context, textView: TextView) {
        // Get the text from the TextView
        val textToCopy = textView.text.toString()

        // Get the Clipboard Manager
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a ClipData object with the text to copy
        val clipData = ClipData.newPlainText("label", textToCopy)

        // Set the ClipData to the Clipboard
        clipboardManager.setPrimaryClip(clipData)

        // Show a toast message to indicate that the text has been copied
        Toast.makeText(context, "mithilakshar@upi  यू पी आई आईडी कॉपी भ गेल। ", Toast.LENGTH_SHORT).show()
    }





}


