package com.mithilakshar.mithilapanchang.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

import com.mithilakshar.mithilapanchang.Repository.FirestoreRepo
import kotlinx.coroutines.tasks.await

class HomeViewModel :ViewModel() {


    val FirestoreRepo= FirestoreRepo()

     fun getBannerurlList(path:String): LiveData<ArrayList<String>> {
        return FirestoreRepo.getBannerurlList(path)
    }







/*    suspend fun getCalendarList(path: String): List<calendardatamodel> {
        return FirestoreRepo.getCalendarList(path)
    }*/

    suspend fun gethomeBroadcast(): String {
        return FirestoreRepo.gethomeBroadcast()
    }

    suspend fun getappbarImagelist(path:String): List<String> {
        return FirestoreRepo.getappbarImagelist(path)
    }




}