package com.mithilakshar.mithilapanchang.UI.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.InterstitialAdManager
import com.mithilakshar.mithilapanchang.databinding.ActivityNakshatraBinding
import com.mithilakshar.mithilapanchang.databinding.ActivityTithiBinding
import com.mithilakshar.mithilapanchang.databinding.ActivityYogBinding
import java.io.File
import java.util.Calendar

class YogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYogBinding
    private lateinit var adView3: AdView
    private val handler = Handler(Looper.getMainLooper())
    private val totalDuration = 1200L // Total duration for the Lottie animation
    private val interval = 500L // Interval for the Handler
    private  var selectedyear:Int = getCurrentYear()
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var spinner: Spinner
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityYogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Network dialog and manager
        val networkDialog = Networkdialog(this)
        val networkManager = NetworkManager(this)
        networkManager.observe(this) { isConnected ->
            if (!isConnected) {
                if (!networkDialog.isShowing) networkDialog.show()
            } else {
                if (networkDialog.isShowing) networkDialog.dismiss()
            }
        }

        // Set header text
        val currentYear = getCurrentYear()
        binding.header.text = "वार्षिक योग कैलेंडर - $currentYear"

        // Initialize AdView
        adView3 = findViewById(R.id.adView3)
        val adRequest = AdRequest.Builder().build()
        adView3.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView3.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Handle ad load failure
            }
        }
        adView3.loadAd(adRequest)

        // Initialize InterstitialAdManager
        interstitialAdManager = InterstitialAdManager(this, getString(R.string.interstitialholiday))
        interstitialAdManager.loadAd { }

        // Lottie Animation
        val lottieView: LottieAnimationView = binding.lottieAnimationView
        val animationList = listOf(
            R.raw.a1, R.raw.a2, R.raw.k3, R.raw.a3, R.raw.a4, R.raw.om, R.raw.solar
        )
        val randomAnimation = animationList.random()
        lottieView.setAnimation(randomAnimation)
        lottieView.playAnimation()

        // Handler and Runnable to stop Lottie animation after totalDuration
        val startTime = System.currentTimeMillis()
        runnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                if (elapsedTime < totalDuration) {
                    handler.postDelayed(this, interval)
                } else {
                    // Stop the Lottie animation and update UI
                    lottieView.pauseAnimation()
                    lottieView.visibility = View.GONE
                    binding.monthview.visibility = View.VISIBLE
                    binding.spinner.visibility = View.VISIBLE

                    // Remove the Runnable from the Handler
                    handler.removeCallbacks(this)
                }
            }
        }
        handler.post(runnable)

        val downloadDirectory = File(this.getExternalFilesDir(null), "test")
        val itemsy = downloadDirectory.listFiles()
            ?.map { it.nameWithoutExtension }
            ?.filter { name ->
                name.startsWith("y", ignoreCase = true) &&
                        name.drop(1).all(Char::isDigit) // everything after 'k' must be digits
            }
            ?.onEach { kName ->
                Log.d("MyTagy", "Found y file: $kName")
            }
            ?.map { name ->
                val year = name.drop(1) // remove the 'k'
                CustomSpinnerAdapter.SpinnerItem(R.drawable.kalash, year)
            }
            ?: emptyList()

        Log.d("MyTagk", "Spinner Items: $itemsy")

        // Spinner setup
        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.kalash, "2025"),

            )
        val adapter = CustomSpinnerAdapter(this, R.layout.spinner_item, itemsy)
        spinner = binding.spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? CustomSpinnerAdapter.SpinnerItem
                selectedItem?.let {
                    binding.header.text = "वार्षिक योग कैलेंडर - ${it.text}"
                    selectedyear = it.text.toInt()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }

        // Month click listeners


        binding.jan .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "जनवरी ")
            intent.putExtra("monthEng", "Jan")
            intent.putExtra("intValue", 1)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.feb .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "फरवरी ")
            intent.putExtra("monthEng", "Feb")
            intent.putExtra("intValue", 2)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.mar .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "मार्च ")
            intent.putExtra("monthEng", "Mar")
            intent.putExtra("intValue", 3)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.apr .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "अप्रैल ")
            intent.putExtra("monthEng", "Apr")
            intent.putExtra("intValue", 4)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.may .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "मई ")
            intent.putExtra("monthEng", "May")
            intent.putExtra("intValue", 5)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.jun .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "जून ")
            intent.putExtra("monthEng", "Jun")
            intent.putExtra("intValue", 6)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.jul .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "जुलाई ")
            intent.putExtra("monthEng", "Jul")
            intent.putExtra("intValue", 7)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.aug .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "अगस्त ")
            intent.putExtra("monthEng", "Aug")
            intent.putExtra("intValue", 8)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.sep .setOnClickListener {
            val intent= Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "सितंबर ")
            intent.putExtra("monthEng", "Sep")
            intent.putExtra("intValue", 9)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.oct .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "अक्टूबर ")
            intent.putExtra("monthEng", "Oct")
            intent.putExtra("intValue", 10)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.nov .setOnClickListener {
            val intent = Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "नवंबर ")
            intent.putExtra("monthEng", "Nov")
            intent.putExtra("intValue", 11)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.dec .setOnClickListener {
            val intent= Intent(this, YogListActivity::class.java)
            intent.putExtra("month", "दिसंबर ")
            intent.putExtra("monthEng", "Dec")
            intent.putExtra("intValue", 12)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

    }




    override fun onPause() {
        super.onPause()
        // Stop the animation and remove callbacks when the activity is paused
        binding.lottieAnimationView.pauseAnimation()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the handler to avoid memory leaks
        handler.removeCallbacksAndMessages(null)
    }

    private fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }
}