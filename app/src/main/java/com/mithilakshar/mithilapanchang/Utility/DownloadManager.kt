package com.mithilakshar.mithilapanchang.Utility

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class DownloadManager(private val context: Context) {

    private val downloadStatus = MutableLiveData<Boolean>()
    private val downloadProgress = MutableLiveData<Int>()
    private var downloadId: Long = -1L

    fun getDownloadStatus(): LiveData<Boolean> = downloadStatus
    fun getDownloadProgress(): LiveData<Int> = downloadProgress

    fun downloadFile(url: String, fileName: String, deleteOrReturn: String) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)

        if (file.exists()) {
            if (deleteOrReturn == "delete") {
                file.delete()
                println("Deleted existing file: $fileName")
            } else {
                println("File already exists and deleteOrReturn is 'return': $fileName")
                downloadStatus.postValue(true)
                downloadProgress.postValue(100)
                return
            }
        }

        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading file...")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)
        monitorDownloadProgress(downloadManager, downloadId)
    }

    private fun monitorDownloadProgress(downloadManager: DownloadManager, downloadId: Long) {
        val query = DownloadManager.Query().setFilterById(downloadId)
        var downloading = true

        CoroutineScope(Dispatchers.IO).launch {
            while (downloading) {
                delay(1000)
                val cursor: Cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val totalSizeIndex =
                        cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    val downloadedSizeIndex =
                        cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)

                    if (statusIndex != -1 && totalSizeIndex != -1 && downloadedSizeIndex != -1) {
                        val status = cursor.getInt(statusIndex)
                        val totalSize = cursor.getInt(totalSizeIndex)
                        val downloadedSize = cursor.getInt(downloadedSizeIndex)

                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false
                            downloadStatus.postValue(true)
                            downloadProgress.postValue(100)
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            downloading = false
                            downloadStatus.postValue(false)
                        } else if (totalSize > 0) {
                            val progress = (downloadedSize * 100L / totalSize).toInt()
                            downloadProgress.postValue(progress)
                        }
                    }
                }
                cursor.close()
            }
        }
    }
}