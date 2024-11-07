package com.mithilakshar.mithilapanchang.UI.View


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.databinding.ActivityHolidayBinding
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R

import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.InterstitialAdManager
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker

import com.mithilakshar.mithilapanchang.Utility.dbDownloadersequence



import kotlinx.coroutines.Job

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File

import java.util.Calendar


class HolidayActivity : AppCompatActivity() {

    lateinit var binding: ActivityHolidayBinding
    private lateinit var adView3: AdView
    val handler = Handler(Looper.getMainLooper())
    val totalDuration = 1200L
    val interval = 500L

    private  var selectedyear:Int = getCurrentYear()
    private lateinit var interstitialAdManager: InterstitialAdManager


    private lateinit var spinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHolidayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkdialog = Networkdialog(this)
        val networkManager= NetworkManager(this)
        networkManager.observe(this, {
            if (!it){
                if (!networkdialog.isShowing){networkdialog.show()}

            }else{
                if (networkdialog.isShowing){networkdialog.dismiss()}

            }
        })
        val currentYear = getCurrentYear()
        binding.header.text="वार्षिक त्योहार कैलेंडर - $currentYear"

        adView3 = findViewById(R.id.adView3)
        val adRequest = AdRequest.Builder().build()
        // Set an AdListener to make the AdView visible when the ad is loaded
        adView3.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView3.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView3.loadAd(adRequest)

        interstitialAdManager = InterstitialAdManager(this)
        interstitialAdManager.loadAd {  }

        val lottieView: LottieAnimationView =  binding.lottieAnimationView
        val animationList = listOf(
            R.raw.a1 ,
            R.raw.a2  ,
            R.raw.k3  ,
            R.raw.a3  ,
            R.raw.a4  ,
            R.raw.om  ,
            R.raw.solar  ,
        )
        val randomAnimation = animationList.random()
        lottieView.setAnimation(randomAnimation) // Reference to new animation in raw folder
        lottieView.playAnimation()


        val startTime = System.currentTimeMillis()

        val runnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                if (elapsedTime < totalDuration) {
                    // Your task (e.g., update UI, show a Toast, or log something)


                    // Re-post the Runnable to run again after the interval
                    handler.postDelayed(this, interval)
                } else {
                    // Task completed after 10 seconds
                    binding.lottieAnimationView.visibility=View.GONE
                    binding.monthview .visibility=View.VISIBLE
                    binding.spinner .visibility=View.VISIBLE
                }
            }
        }


        handler.post(runnable)











        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2024"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2025"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2026"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2027"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2028"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2029"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2030"),
            )



        val adapter = CustomSpinnerAdapter(this, R.layout.spinner_item, items)
        spinner=binding.spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? CustomSpinnerAdapter.SpinnerItem
                selectedItem?.let {
                    // Access the properties of SpinnerItem
                    val iconResId = it.iconResId
                    val text = it.text
                    // Do something with the selected item

                    binding.header.text="वार्षिक त्योहार कैलेंडर - $text"
                    if (text == "2024") {
                        // Perform action for Item 1
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    } else if (text == "2025") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2026") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2027") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2028") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2029") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2030") {
                        // Perform action for Item 2
                        selectedyear=text.toInt()
                        // Update other UI elements or perform other actions
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when no item is selected (optional)
            }
        }





        binding.jan .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जनवरी ")
            intent.putExtra("monthEng", "january")
            intent.putExtra("intValue", 1)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.feb .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "फरवरी ")
            intent.putExtra("monthEng", "february")
            intent.putExtra("intValue", 2)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.mar .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "मार्च ")
            intent.putExtra("monthEng", "march")
            intent.putExtra("intValue", 3)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.apr .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अप्रैल ")
            intent.putExtra("monthEng", "april")
            intent.putExtra("intValue", 4)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.may .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "मई ")
            intent.putExtra("monthEng", "may")
            intent.putExtra("intValue", 5)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.jun .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जून ")
            intent.putExtra("monthEng", "june")
            intent.putExtra("intValue", 6)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.jul .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जुलाई ")
            intent.putExtra("monthEng", "july")
            intent.putExtra("intValue", 7)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.aug .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अगस्त ")
            intent.putExtra("monthEng", "august")
            intent.putExtra("intValue", 8)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.sep .setOnClickListener {
            val intent= Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "सितंबर ")
            intent.putExtra("monthEng", "september")
            intent.putExtra("intValue", 9)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.oct .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अक्टूबर ")
            intent.putExtra("monthEng", "october")
            intent.putExtra("intValue", 10)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }
        binding.nov .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "नवंबर ")
            intent.putExtra("monthEng", "november")
            intent.putExtra("intValue", 11)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }

        binding.dec .setOnClickListener {
            val intent= Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "दिसंबर ")
            intent.putExtra("monthEng", "december")
            intent.putExtra("intValue", 12)
            intent.putExtra("year", selectedyear)
            startActivity(intent)
            interstitialAdManager.showAd(this){
                startActivity(intent)
            }

        }



    }











    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }




}