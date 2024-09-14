package com.mithilakshar.mithilapanchang.UI.View

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.mithilakshar.mithilapanchang.Utility.dbDownloader
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel
import kotlinx.coroutines.launch
import java.io.File

import java.util.Calendar
class HolidayActivity : AppCompatActivity() {

    lateinit var binding: ActivityHolidayBinding
    private lateinit var fileExistenceLiveData: LiveData<Boolean>
    private lateinit var adView3: AdView
    private lateinit var updatesDao: UpdatesDao
    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var bhagwatgitaviewmodel: BhagwatGitaViewModel
    private  var selectedyear:Int = getCurrentYear()

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


        fileDownloader = FirebaseFileDownloader(this)
        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        val factory = BhagwatGitaViewModel.factory(fileDownloader)
        bhagwatgitaviewmodel =
            ViewModelProvider(this, factory).get(BhagwatGitaViewModel::class.java)


        observeFileExistence("holiday",2)


        fileDownloader = FirebaseFileDownloader(this)

        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        val dbDownloader= dbDownloader(updatesDao,fileDownloader)

        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2024"),
            CustomSpinnerAdapter.SpinnerItem(R.drawable.festival, "2025"),
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
                    } else {
                        // Default action

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when no item is selected (optional)
            }
        }



        dbDownloader.observeFileExistence("holiday2025",this,lifecycleScope,11,this,progressCallback = { progress ->
            runOnUiThread {
                // Update UI with the progress
                if (progress==100){

                binding.spinner.visibility=View.VISIBLE
                binding.monthview.visibility=View.VISIBLE

                }
            }
        })

        binding.jan .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जनवरी ")
            intent.putExtra("monthEng", "january")
            intent.putExtra("intValue", 1)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.feb .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "फरवरी ")
            intent.putExtra("monthEng", "february")
            intent.putExtra("intValue", 2)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }
        binding.mar .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "मार्च ")
            intent.putExtra("monthEng", "march")
            intent.putExtra("intValue", 3)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.apr .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अप्रैल ")
            intent.putExtra("monthEng", "april")
            intent.putExtra("intValue", 4)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }
        binding.may .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "मई ")
            intent.putExtra("monthEng", "may")
            intent.putExtra("intValue", 5)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.jun .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जून ")
            intent.putExtra("monthEng", "june")
            intent.putExtra("intValue", 6)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }
        binding.jul .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "जुलाई ")
            intent.putExtra("monthEng", "july")
            intent.putExtra("intValue", 7)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.aug .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अगस्त ")
            intent.putExtra("monthEng", "august")
            intent.putExtra("intValue", 8)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }
        binding.sep .setOnClickListener {
            val intent= Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "सितंबर ")
            intent.putExtra("monthEng", "september")
            intent.putExtra("intValue", 9)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.oct .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "अक्टूबर ")
            intent.putExtra("monthEng", "october")
            intent.putExtra("intValue", 10)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }
        binding.nov .setOnClickListener {
            val intent = Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "नवंबर ")
            intent.putExtra("monthEng", "november")
            intent.putExtra("intValue", 11)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }

        binding.dec .setOnClickListener {
            val intent= Intent(this, HolidayListActivity::class.java)
            intent.putExtra("month", "दिसंबर ")
            intent.putExtra("monthEng", "december")
            intent.putExtra("intValue", 12)
            intent.putExtra("year", selectedyear)
            startActivity(intent)

        }



    }



    private fun downloadFile(storagePath: String, action: String, localFileName: String,progressCallback: (Int) -> Unit) {
        if (::fileDownloader.isInitialized) {
            fileDownloader.retrieveURL(storagePath, action, localFileName, { downloadedFile ->
                if (downloadedFile != null) {
                    // File downloaded successfully, do something with the file if needed
                    Log.d(ContentValues.TAG, "File downloaded successfully: $downloadedFile")

                    // Notify UI or perform tasks with downloaded file
                    handleDownloadedFile(downloadedFile)
                } else {
                    // Handle the case where download failed
                    Log.d(ContentValues.TAG, "Download failed for file: $localFileName")
                }
            },progressCallback)
        } else {
            Log.e(ContentValues.TAG, "fileDownloader is not initialized.")
        }
    }
    private fun handleDownloadedFile(downloadedFile: File) {

        //readFileContent()

    }



    private fun observeFileExistence(month:String,id:Int) {
        fileExistenceLiveData = checkFileExistence("$month.db")
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document("$month")
        fileExistenceLiveData.observe(this) { fileExists ->
            if (fileExists) {


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "$month.db"
                        lifecycleScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                //readFileContent()
                                binding.lottieAnimationView .visibility=View.GONE
                                binding.loadingstatus.visibility=View.GONE


                            } else {
                                val holidayupdate = updatesDao.findById(id)
                                holidayupdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }


                                val storagePath = "SQLdb/$month"
                                downloadFile(storagePath, "delete", "$month.db",progressCallback = { progress ->
                                    // Update your progress UI, e.g., a ProgressBar or TextView
                                    Log.d("DownloadProgress", "Download is $progress% done")
                                })
                                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this@HolidayActivity, {

                                    if (it >=100){

                                        binding.lottieAnimationView .visibility=View.GONE
                                        binding.loadingstatus.visibility=View.GONE


                                    }

                                })


                            }
                        }


                        // File exists, proceed with reading its content


                    } else {


                    }


                }

                // File does not exist, handle accordingly
            } else {

                val storagePath = "SQLdb/$month"
                downloadFile(storagePath, "delete", "$month.db",progressCallback = { progress ->
                    // Update your progress UI, e.g., a ProgressBar or TextView
                    Log.d("DownloadProgress", "Download is $progress% done")
                })
                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this, {

                    if (it >=100){

                        binding.lottieAnimationView .visibility=View.GONE
                        binding.loadingstatus.text="लोडिंग पूर्ण भेल"

                    }

                })

                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "$month.db"
                        lifecycleScope.launch {

                            val holiday = Updates(id = id, fileName = "$month.db", uniqueString = "holiday")
                            updatesDao.insert(holiday)

                            val holidayupdate = updatesDao.findById(id)
                            holidayupdate.let {
                                it.uniqueString = actions
                                updatesDao.update(it)
                            }

                        }

                    }


                }

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