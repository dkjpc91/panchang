package com.mithilakshar.mithilapanchang.Utility

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.UI.View.HomeActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File


class dbDownloader(
    private val updatesDao: UpdatesDao,
    firebaseFileDownloader: FirebaseFileDownloader
) {


    private var firebaseFileDownloader: FirebaseFileDownloader = firebaseFileDownloader

     fun observeFileExistence(
         filename: String,
         lifecycleOwner: LifecycleOwner,
         coroutineScope: CoroutineScope,
         updatedaoid: Int,
         homeActivity: HomeActivity
     ) {
        var fileExistenceLiveData = checkFileExistence("$filename.db",homeActivity)
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document(filename)
        fileExistenceLiveData.observe(lifecycleOwner) { fileExists ->
            if (fileExists) {

                Log.d("dbd", "fileexist} ")
                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "$filename.db"
                        Log.d("dbd", "$filename} ")
                        coroutineScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                //readFileContent()
                                //binding.lottieAnimationView .visibility=View.GONE
                                //binding.loadingstatus.visibility=View.GONE

                                Log.d("dbd", "actionmatch $actions, uiqueid ${updates.get(0).uniqueString} ")
                            } else {

                                Log.d("dbd", "actionnotmatch $actions, uiqueid ${updates.get(0).uniqueString} ")
                                Log.d("dbd", "$filename} ")
                                val holidayupdate = updatesDao.findById(updatedaoid)
                                Log.d("dbd", "$updatedaoid} ")
                                holidayupdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }

                                Log.d("dbd", "actionmismatch new file download} ")
                                val storagePath = "SQLdb/$filename"
                                downloadFile(storagePath, "delete", "$filename.db")


                            }
                        }


                        // File exists, proceed with reading its content


                    } else {


                    }


                }

                // File does not exist, handle accordingly
            } else {

                Log.d("dbd", "no file exist ")

                val storagePath = "SQLdb/$filename"
                downloadFile(storagePath, "delete", "$filename.db")


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "$filename.db"
                        coroutineScope.launch {

                            Log.d("dbd", "no file exist newfile download")

                            val holiday = Updates(id = updatedaoid.toLong(),fileName = "$filename.db", uniqueString = "${filename}")
                            updatesDao.insert(holiday)

                            val holidayupdate = updatesDao.findById(updatedaoid)
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



    fun checkFileExistence(fileName: String, homeActivity: HomeActivity): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath = homeActivity.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }


    private fun downloadFile(storagePath: String, action: String, localFileName: String) {

            firebaseFileDownloader.retrieveURL(storagePath, action, localFileName) { downloadedFile ->
                if (downloadedFile != null) {
                    // File downloaded successfully, do something with the file if needed
                    Log.d(ContentValues.TAG, "File downloaded successfully: $downloadedFile")

                    // Notify UI or perform tasks with downloaded file
                } else {
                    // Handle the case where download failed
                    Log.d(ContentValues.TAG, "Download failed for file: $localFileName")
                }
            }
        }
    }

