package com.mithilakshar.mithilapanchang.Utility
import android.app.Activity
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.android.billingclient.api.*
import com.mithilakshar.mithilapanchang.Models.Product
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.UI.View.BillingActivity

class BillingManager(private val activity: Activity) : PurchasesUpdatedListener, BillingClientStateListener {

    //1.Initialize Billing Client:
    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases()
        .build()
    private val skuDetailsMap = mutableMapOf<String, SkuDetails>()

    init {
        startConnection()
    }

    //2.Start Connection:
    private fun startConnection() {
        billingClient.startConnection(this)
    }

    //3.Billing Setup:
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            queryAvailableProducts()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to Google Play by calling startConnection()
    }

    //4.Query Available Products:
    private fun queryAvailableProducts() {
        val skuList = listOf("mithila_panchang_silver", "mithila_panchang_gold","mithila_panchang_diamond" ) // Replace with your product IDs
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                skuDetailsList.forEach { skuDetails ->
                    Log.d("BillingManager", "SKU Details: ${skuDetails.sku}, ${skuDetails.title}, ${skuDetails.price}")
                    skuDetailsMap[skuDetails.sku] = skuDetails
                }
                updateUIWithAvailableProducts()
            } else {
                Log.e("BillingManager", "Error querying SKU details: ${billingResult.debugMessage}")
            }
        }
    }

    //5.Update UI:
    private fun updateUIWithAvailableProducts() {
        val products = skuDetailsMap.values.map {
            Product(
                title = it.title,
                description = it.description,
                price = it.price,
                sku = it.sku
            )
        }
        Log.d("BillingManager", "Updating product list with ${products.size} items")

        // Log details of each product
        products.forEach { product ->
            Log.d("BillingManager", "Product Details - Title: ${product.title}, Description: ${product.description}, Price: ${product.price}, SKU: ${product.sku}")
        }
        (activity as? BillingActivity)?.updateProductList(products)
    }

    //6.Launch Purchase Flow:
    fun launchPurchaseFlow(activity: Activity, skuId: String) {
        val skuDetails = skuDetailsMap[skuId]
        if (skuDetails != null) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        } else {
            // Handle the error: SKU details not found
        }
    }

    //7.Handle Purchase Updates:
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {

                val productDetails = Updates(id = 9, fileName = "iap", uniqueString = purchase.purchaseTime.toString())
                handlePurchase(purchase,productDetails)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            showPurchaseCancelledDialog()
        } else {
            // Handle other errors
        }
    }

    //8.Acknowledge Purchase:
    private fun handlePurchase(purchase: Purchase, productDetails: Updates) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        //Handle successful acknowledgment if needed
                        showPurchaseSuccessDialog()
                        (activity as? BillingActivity)?.updatebillingdao(productDetails)
                    }
                }
            }
        }
    }

    //9 Query Purchases
    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    val productDetails = Updates(id = 10, fileName = "iap", uniqueString = "25")
                    handlePurchase(purchase, productDetails)
                }
            }
        }
    }

    fun onDestroy() {
        billingClient.endConnection()
    }

    private fun showPurchaseCancelledDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("भुगतान रद्द क देल गेल:")
        builder.setMessage("अहाँ भुगतान रद्द क'देलहुं,एहि लेल भुगतान रद्द भ' गेल अछि. अहाँ बाद मे पुनः प्रयास क सकैत छी। \nमिथिला पंचांग के प्रयोग करबाक लेल धन्यवाद।")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    private fun showPurchaseSuccessDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("भुगतान पूर्ण भ गेल:")
        builder.setMessage("अपनेक भुगतान के लिये धन्यवाद।' गेल अछि. अहाँक सहयोग सँ मिथिला पंचांग एकटा व्यापक समुदायक लेल सुलभ होयत। \nमिथिला पंचांग के प्रयोग करबाक लेल धन्यवाद।")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
