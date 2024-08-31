package com.mithilakshar.mithilapanchang.Utility

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileDownloaderProgress(private val context: Context) {

    private val client = OkHttpClient()
    private val downloadStatus = MutableLiveData<Boolean>()
    private val downloadProgress = MutableLiveData<Int>()

    fun getDownloadStatus(): LiveData<Boolean> = downloadStatus
    fun getDownloadProgress(): LiveData<Int> = downloadProgress

    fun downloadFile(url: String, fileName: String, deleteOrReturn: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Get the external files directory for the app

            val externalDir = File(context.getExternalFilesDir(null), "test")
            if (!externalDir.exists()) {
                externalDir.mkdirs()
            }

            if (externalDir.exists()) {
                val file = File(externalDir, fileName)

                // Check if the file already exists
                if (file.exists()) {
                    if (deleteOrReturn=="delete") {
                        file.delete()
                        println("Deleted existing file: $fileName")
                    } else {
                        println("File already exists and deleteOrReturn is 'return': $fileName")
                        downloadStatus.postValue(true)
                        downloadProgress.postValue(100)
                        return@launch
                    }
                }


                val request = Request.Builder().url(url).build()
                try {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Unexpected code ${response.code}")

                    response.body?.let { body ->
                        val inputStream: InputStream = body.byteStream()
                        val outputStream = FileOutputStream(file)
                        val contentLength = body.contentLength() // Get content length

                        var downloadedBytes = 0L
                        inputStream.use { input ->
                            outputStream.use { output ->
                                val buffer = ByteArray(1024)
                                var readBytes: Int
                                while (true) { // Loop continues indefinitely
                                    readBytes = input.read(buffer)
                                    if (readBytes == -1) break  // Exit loop if end of stream reached
                                    downloadedBytes += readBytes.toLong()
                                    output.write(buffer, 0, readBytes)
                                    updateDownloadProgress((downloadedBytes * 100 / contentLength).toInt()) // Calculate and update progress
                                }
                            }
                        }
                        println("File downloaded successfully: $fileName")
                        downloadStatus.postValue(true)
                        return@launch
                    } ?: run {
                        println("Failed to download file: $fileName")
                        downloadStatus.postValue(false)
                        return@launch
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    downloadStatus.postValue(false)
                    return@launch
                }
            } else {
                println("Failed to get external storage directory")
                downloadStatus.postValue(false)
                return@launch
            }
        }
    }

    private fun updateDownloadProgress(progress: Int) {
        downloadProgress.postValue(progress)
    }


}