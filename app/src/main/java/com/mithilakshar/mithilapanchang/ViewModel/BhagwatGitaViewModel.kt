package com.mithilakshar.mithilapanchang.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader




class BhagwatGitaViewModel(private val firebaseFileDownloader: FirebaseFileDownloader): ViewModel() {



    val downloadProgressLiveData: LiveData<Int> = firebaseFileDownloader.downloadProgressLiveData


    fun retrieveAndDownloadFile(documentPath: String, action: String, urlFieldName: String) {
        firebaseFileDownloader.retrieveURL(documentPath, action, urlFieldName) { file ->
            // Handle the downloaded file if needed
        }
    }

    class factory(private val firebaseFileDownloader: FirebaseFileDownloader) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BhagwatGitaViewModel(firebaseFileDownloader) as T
        }
    }
}