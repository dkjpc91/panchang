package com.mithilakshar.mithilapanchang.UI.View

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager

import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase

import com.mithilakshar.mithilapanchang.databinding.ActivityCalendarBinding
import com.mithilakshar.mithilapanchang.UI.Fragments.mayfragment
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel
import kotlinx.coroutines.launch
import java.io.File

class CalendarActivity : AppCompatActivity() {

    lateinit var binding: ActivityCalendarBinding

    private lateinit var fileExistenceLiveData: LiveData<Boolean>

    private lateinit var updatesDao: UpdatesDao

    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var adView4: AdView

    private lateinit var bhagwatgitaviewmodel: BhagwatGitaViewModel




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCalendarBinding.inflate(layoutInflater)
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

        adView4 = findViewById(R.id.adView4)
        val adRequest = AdRequest.Builder().build()
        // Set an AdListener to make the AdView visible when the ad is loaded
        adView4.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView4.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView4.loadAd(adRequest)


        fileDownloader = FirebaseFileDownloader(this)
        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        val factory = BhagwatGitaViewModel.factory(fileDownloader)
        bhagwatgitaviewmodel =
            ViewModelProvider(this, factory).get(BhagwatGitaViewModel::class.java)


        observeFileExistence("calander")


        replaceFragment(mayfragment())





    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager // Get FragmentManager from Activity
        val transaction = fragmentManager.beginTransaction() // Begin transaction
        transaction.replace(R.id.fragmentContainer, fragment) // Replace container with fragment
        transaction.commit() // Commit transaction
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

    fun checkFileExistence(fileName: String): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath = this.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }

    private fun observeFileExistence(month:String) {
        fileExistenceLiveData = checkFileExistence("calander.db")
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document("calander")
        fileExistenceLiveData.observe(this) { fileExists ->
            if (fileExists) {


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "calander.db"
                        lifecycleScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                //readFileContent()
                                binding.lottieAnimationView .visibility=View.GONE
                                binding.loadingstatus.visibility=View.GONE


                            } else {
                                val holidayupdate = updatesDao.findById(3)
                                holidayupdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }


                                val storagePath = "SQLdb/calander"
                                downloadFile(storagePath, "delete", "calander.db", progressCallback = { progress ->
                                    // Update your progress UI, e.g., a ProgressBar or TextView
                                    Log.d("DownloadProgress", "Download is $progress% done")
                                })
                                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this@CalendarActivity, {

                                    if (it >=100){

                                        binding.lottieAnimationView .visibility=View.GONE
                                        binding.loadingstatus.visibility=View.GONE
                                        replaceFragment(mayfragment())

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

                val storagePath = "SQLdb/calander"
                downloadFile(storagePath, "delete", "calander.db", progressCallback = { progress ->
                    // Update your progress UI, e.g., a ProgressBar or TextView
                    Log.d("DownloadProgress", "Download is $progress% done")
                })
                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this, {

                    if (it >=100){

                        binding.lottieAnimationView .visibility=View.GONE
                        binding.loadingstatus.text="लोडिंग पूर्ण"
                        replaceFragment(mayfragment())
                    }

                })

                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "calander.db"
                        lifecycleScope.launch {

                            val calander = Updates(id = 3, fileName = "calander.db", uniqueString = "calander")
                            updatesDao.insert(calander)
                            val holidayupdate = updatesDao.findById(3)
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

    }





