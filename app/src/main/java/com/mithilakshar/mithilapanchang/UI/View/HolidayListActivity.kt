package com.mithilakshar.mithilapanchang.UI.View


import android.content.ContentValues
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Adapters.holidayadapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.DownloadManager
import com.mithilakshar.mithilapanchang.Utility.FileDownloaderProgress
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel

import com.mithilakshar.mithilapanchang.databinding.ActivityHolidaylistBinding
import kotlinx.coroutines.launch
import java.io.File


class HolidayListActivity : AppCompatActivity() {

    private lateinit var downloadmanager: DownloadManager

    lateinit var binding:ActivityHolidaylistBinding
    private lateinit var fileExistenceLiveData: LiveData<Boolean>
    private lateinit var updatesDao: UpdatesDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: holidayadapter
    private var holidays: MutableList<Map<String, String>> = mutableListOf()

    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var bhagwatgitaviewmodel: BhagwatGitaViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityHolidaylistBinding.inflate(layoutInflater)
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

        fileDownloader = FirebaseFileDownloader(this)
        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        val factory = BhagwatGitaViewModel.factory(fileDownloader)
        bhagwatgitaviewmodel =
            ViewModelProvider(this, factory).get(BhagwatGitaViewModel::class.java)


        downloadmanager=DownloadManager(this)
        downloadmanager.getDownloadStatus().observe(this, Observer { isSuccess ->
            if (isSuccess) {
                // Handle successful download
               // binding.title.text = "Download completed successfully."
            } else {
                // Handle failed download
               // binding.title.text = "Download completed successfully."
            }
        })

        downloadmanager.getDownloadProgress().observe(this, Observer { progress ->
            // Update the ProgressBar with the download progress

           // binding.title.text =  "Download progress: $progress%"
        })

        // Trigger the download
        val fileUrl = "https://sharedby.blomp.com/IMN1j3"
        val fileName = "file.db"
        val deleteOrReturn = "delete" // or "return" based on your requirement

        //downloadmanager.downloadFile(fileUrl, fileName, deleteOrReturn)


        recyclerView = binding.holidayrecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = holidayadapter(this, holidays)
        recyclerView.adapter = adapter


        // Notify adapter that data set has changed
        adapter.notifyDataSetChanged()

        val intent = intent
        val month = intent.getStringExtra("month").toString()
        val monthEng = intent.getStringExtra("monthEng").toString()
        val intValue = intent.getIntExtra("intValue", 1)


        observeFileExistence(monthEng)















        binding.title.text="हिन्दू त्योहार - "+"$month"+"2024"





    }




    private fun readFileContent(month:String) {
        val dbHelper = dbHelper(applicationContext, "holiday.db")
        val av = dbHelper.getHolidaysByMonth(month)
        holidays.addAll(av)
        adapter.notifyDataSetChanged()

    }


    private fun downloadFile(storagePath: String, action: String, localFileName: String) {
        if (::fileDownloader.isInitialized) {
            fileDownloader.retrieveURL(storagePath, action, localFileName) { downloadedFile ->
                if (downloadedFile != null) {
                    // File downloaded successfully, do something with the file if needed
                    Log.d(ContentValues.TAG, "File downloaded successfully: $downloadedFile")

                    // Notify UI or perform tasks with downloaded file
                    handleDownloadedFile(downloadedFile)
                } else {
                    // Handle the case where download failed
                    Log.d(ContentValues.TAG, "Download failed for file: $localFileName")
                }
            }
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
        fileExistenceLiveData = checkFileExistence("holiday.db")
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document("holiday")
        fileExistenceLiveData.observe(this) { fileExists ->
            if (fileExists) {


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "holiday.db"
                        lifecycleScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                //readFileContent()
                                binding.lottieAnimationView .visibility=View.GONE
                                readFileContent(month)


                            } else {
                                val holidayupdate = updatesDao.findById(2)
                                holidayupdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }


                                val storagePath = "SQLdb/holiday"
                                downloadFile(storagePath, "delete", "holiday.db")
                                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this@HolidayListActivity, {

                                    if (it >=100){

                                        binding.lottieAnimationView .visibility=View.GONE
                                        readFileContent(month)
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

                val storagePath = "SQLdb/holiday"
                downloadFile(storagePath, "delete", "holiday.db")
                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this, {

                    if (it >=100){

                        binding.lottieAnimationView .visibility=View.GONE
                        readFileContent(month)
                    }

                })

                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "holiday.db"
                        lifecycleScope.launch {
                            val holidayupdate = updatesDao.findById(2)
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