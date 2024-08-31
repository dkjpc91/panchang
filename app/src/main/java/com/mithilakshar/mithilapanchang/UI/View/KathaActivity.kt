package com.mithilakshar.mithilapanchang.UI.View

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Adapters.kathaapapter
import com.mithilakshar.mithilapanchang.Adapters.mantraadapter


import com.mithilakshar.mithilapanchang.Dialog.Networkdialog

import com.mithilakshar.mithilapanchang.Notification.NetworkManager

import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel

import com.mithilakshar.mithilapanchang.databinding.ActivityKathaBinding
import kotlinx.coroutines.launch
import java.io.File

class KathaActivity : AppCompatActivity() {

    lateinit var binding: ActivityKathaBinding
    private lateinit var dbHelper: dbHelper
    private lateinit var kathaapapter: kathaapapter

    private lateinit var fileExistenceLiveData: LiveData<Boolean>

    private lateinit var updatesDao: UpdatesDao

    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var bhagwatgitaviewmodel: BhagwatGitaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKathaBinding.inflate(layoutInflater)
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

        observeFileExistence("vrat")

        dbHelper=dbHelper(this,"vrat.db")


        val vratkathadata= dbHelper.getAllTableData("vrat")
        kathaapapter=kathaapapter(vratkathadata)
        binding.kathaRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@KathaActivity)
            adapter = kathaapapter
        }





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
        fileExistenceLiveData = checkFileExistence("vrat.db")
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document("vrat")
        fileExistenceLiveData.observe(this) { fileExists ->
            if (fileExists) {


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "vrat.db"
                        lifecycleScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                //readFileContent()
                                binding.lottieAnimationView .visibility=View.GONE
                                binding.loadingstatus.visibility=View.GONE


                            } else {
                                val holidayupdate = updatesDao.findById(6)
                                holidayupdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }


                                val storagePath = "SQLdb/vrat"
                                downloadFile(storagePath, "delete", "vrat.db")
                                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this@KathaActivity, {

                                    if (it >=100){

                                        binding.lottieAnimationView .visibility=View.GONE
                                        binding.loadingstatus.visibility=View.GONE
                                        recreate()

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

                val storagePath = "SQLdb/vrat"
                downloadFile(storagePath, "delete", "vrat.db")
                bhagwatgitaviewmodel.downloadProgressLiveData.observe(this, {

                    if (it >=100){

                        binding.lottieAnimationView .visibility=View.GONE
                        binding.loadingstatus.text="लोडिंग पूर्ण भेल"
                        recreate()
                    }

                })

                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "vrat.db"
                        lifecycleScope.launch {

                            val vrat = Updates(id = 6, fileName = "vrat.db", uniqueString = "vrat")
                            updatesDao.insert(vrat)
                            val holidayupdate = updatesDao.findById(6)
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