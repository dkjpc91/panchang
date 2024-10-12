package com.mithilakshar.mithilapanchang.UI.View

import android.os.Bundle

import android.util.Log
import android.widget.Toast

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope

import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase

import com.mithilakshar.mithilapanchang.Utility.SupabaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker

import com.mithilakshar.mithilapanchang.Utility.dbSupabaseDownloadeSequence
import com.mithilakshar.mithilapanchang.databinding.ActivityTestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors


class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    private lateinit var updatesDao: UpdatesDao
    private lateinit var dbSupabaseDownloadeSequence: dbSupabaseDownloadeSequence
    private lateinit var downloader: SupabaseFileDownloader

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            binding=ActivityTestBinding.inflate(layoutInflater)
            setContentView(binding.root)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            downloader = SupabaseFileDownloader(this)
            updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()
            dbSupabaseDownloadeSequence = dbSupabaseDownloadeSequence(updatesDao, downloader)

            val filesWithIds = listOf(
                Pair("calander", 3),
            )


            binding.delete.setOnClickListener {
                performPreUpdateTasks{

                    runOnUiThread {
                        Toast.makeText(this, "files deleted", Toast.LENGTH_SHORT).show()
                    }
                }

            }



            binding.download.setOnClickListener {

                lifecycleScope.launch {
                    // Initialize UpdateChecker and check for updates
                    val updateChecker = UpdateChecker(updatesDao)
                    val isUpdateNeeded = updateChecker.getUpdateStatus()



                    Log.d("supabase", "isUpdateNeeded: $isUpdateNeeded")
                    withContext(Dispatchers.Main) {
                    // Perform actions based on whether an update is needed
                    if (isUpdateNeeded != "a") {
                        Log.d("supabase", "Update needed: $isUpdateNeeded")

                        dbSupabaseDownloadeSequence.observeMultipleFileExistence(
                            filesWithIds,
                            this@TestActivity,
                            lifecycleScope,
                            homeActivity = this@TestActivity, // Your activity
                            progressCallback = { progress, filePair ->
                                Log.d("supabase", "File: ${filePair.first()}.db, Progress: $progress%")
                            },
                            {
                                Log.d("supabase", "File: completed when update required")
                            }
                        )

                    }else {
                       val nonExistentFiles= checkFilesExistence(filesWithIds)
                        Log.d("supabase", "File: non exist  $nonExistentFiles")

                   dbSupabaseDownloadeSequence.observeMultipleFileExistence(
                            nonExistentFiles,
                            this@TestActivity,
                            lifecycleScope,
                            homeActivity = this@TestActivity,
                            progressCallback = { progress, filePair ->
                                Log.d("supabase", "File: ${filePair.first()}.db, Progress: $progress%")
                            },
                            {
                                Log.d("supabase", "File: completed when non-existent required")
                            }
                        )
                    }

                }
                }
            }





}



    suspend fun checkFilesExistence(filesWithIds: List<Pair<String, Int>>): List<Pair<String, Int>> {
        val nonExistentFiles = mutableListOf<Pair<String, Int>>() // List to hold non-existent files
        val dbFolderPath = this.getExternalFilesDir(null)?.absolutePath + File.separator + "test" // Directory path
        val dbFolder = File(dbFolderPath)

        if (!dbFolder.exists()) {
            dbFolder.mkdirs() // Create the directory if it doesn't exist
        }

        // Create a coroutine scope for concurrent checks
        coroutineScope {
            val jobs = filesWithIds.map { filePair ->
                launch(Dispatchers.IO) {
                    try {
                        val dbFile = File(dbFolder, "${filePair.first}.db") // Construct the file path
                        // Check if the file exists
                        if (!dbFile.exists()) {
                            synchronized(nonExistentFiles) { // Ensure thread safety while modifying the list
                                nonExistentFiles.add(filePair) // Add to the list of non-existent files
                            }
                            Log.d("supabase", "File does not exist: ${filePair.first}.db, ID: ${filePair.second}")
                        } else {
                            Log.d("supabase", "File exists: ${filePair.first}.db, ID: ${filePair.second}")
                        }
                    } catch (e: Exception) {
                        Log.e("supabase", "Error checking file existence: ${filePair.first}.db, ID: ${filePair.second}", e)
                    }
                }
            }
            // Await all jobs to finish
            jobs.joinAll()
        }

        Log.d("supabase", "Non-existent files: $nonExistentFiles")
        return nonExistentFiles // Return the list of non-existent files
    }

    private fun performPreUpdateTasks(onComplete: () -> Unit) {
        // Directory to delete
        val downloadDirectory = File(this.getExternalFilesDir(null), "test")

        // Function to delete the directory and its contents
        fun deleteDirectory(directory: File) {
            if (directory.exists()) {
                directory.listFiles()?.forEach {
                    if (it.isDirectory) {
                        deleteDirectory(it)
                    } else {
                        if (!it.delete()) {
                            Log.e("DeleteFile", "Failed to delete file: ${it.absolutePath}")
                        }
                    }
                }
                if (!directory.delete()) {
                    Log.e("DeleteDir", "Failed to delete directory: ${directory.absolutePath}")
                }
            }
        }

        // Perform directory deletion asynchronously
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                deleteDirectory(downloadDirectory)
            } catch (e: Exception) {
                Log.e("DeleteDir", "Error during directory deletion", e)
            } finally {
                // Proceed with the update
                onComplete()
            }
        }
    }

}
