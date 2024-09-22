package com.mithilakshar.mithilapanchang.UI.View

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.databinding.ActivityHolidayBinding
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.InterstitialAdManager
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker
import com.mithilakshar.mithilapanchang.Utility.dbDownloader
import com.mithilakshar.mithilapanchang.Utility.dbDownloadersequence
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File

import java.util.Calendar
import java.util.Locale

class HolidayActivity : AppCompatActivity() {

    lateinit var binding: ActivityHolidayBinding
    private lateinit var fileExistenceLiveData: LiveData<Boolean>
    private lateinit var adView3: AdView
    private lateinit var updatesDao: UpdatesDao
    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var bhagwatgitaviewmodel: BhagwatGitaViewModel
    private  var selectedyear:Int = getCurrentYear()
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var dbDownloadersequence: dbDownloadersequence

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


        fileDownloader = FirebaseFileDownloader(this)
        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        dbDownloadersequence = dbDownloadersequence(updatesDao, fileDownloader)

        val filesWithIds = listOf(
            Pair("holiday2025",11),
            Pair("holiday2026",12),
            Pair("holiday2027",13),
            Pair("holiday2028",14),
            Pair("holiday2029",15),
            Pair("holiday2030",16),

        )
        lifecycleScope.launch {
            val updateChecker = UpdateChecker(updatesDao)
            val isUpdateNeeded = updateChecker.getUpdateStatus()
            val nonExistentFiles = mutableListOf<Pair<String, Int>>()
            val jobs = mutableListOf<Job>()
            for (filePair in filesWithIds) {
                val job = launch {
                    // Observe the LiveData returned by checkFileExistence
                    checkFileExistence("${filePair.first}.db").observeForever { exists ->
                        if (exists != null && !exists) {
                            nonExistentFiles.add(filePair)
                        }
                    }
                }
                jobs.add(job)
            }
            jobs.joinAll()


            Log.d("nonExistentFiles", " :  nonExistentFiles  $nonExistentFiles")
            if (isUpdateNeeded!="a" ) {

                Log.d("updatechecker", " :  needed $isUpdateNeeded")

                dbDownloadersequence.observeMultipleFileExistence(
                    filesWithIds,
                    this@HolidayActivity,
                    lifecycleScope,
                    homeActivity = this@HolidayActivity, // Your activity
                    progressCallback = { progress, filePair  ->



                        Log.d("update", "File: $filePair, Progress: $progress%")


                    },{

                       // observeFileExistence("holiday",2)
                        binding.spinner.visibility=View.VISIBLE
                        binding.monthview.visibility=View.VISIBLE
                        binding.lottieAnimationView.visibility=View.GONE
                        Log.d("update", " total update completed")


                    }
                )


            } else {

                dbDownloadersequence.observeMultipleFileExistence(
                    nonExistentFiles,
                    this@HolidayActivity,
                    lifecycleScope,
                    homeActivity = this@HolidayActivity, // Your activity
                    progressCallback = { progress, filePair  ->



                        Log.d("update", "File: $filePair, Progress: $progress%")


                    },{

                        // observeFileExistence("holiday",2)
                        binding.spinner.visibility=View.VISIBLE
                        binding.monthview.visibility=View.VISIBLE
                        binding.lottieAnimationView.visibility=View.GONE
                        Log.d("update", " total non update  completed")


                    }
                )


            }
        }








        val factory = BhagwatGitaViewModel.factory(fileDownloader)
        bhagwatgitaviewmodel =
            ViewModelProvider(this, factory).get(BhagwatGitaViewModel::class.java)






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









    fun checkFileExistence(fileName: String): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath = this.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }




}