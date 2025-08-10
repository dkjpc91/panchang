package com.mithilakshar.mithilapanchang.Utility

import com.google.firebase.firestore.FirebaseFirestore

class DocList {

    private val db = FirebaseFirestore.getInstance()

    fun getHomeDocuments(
        callback: (List<Map<String, Any>>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("SQLdb")
            .get()
            .addOnSuccessListener { result ->
                val documents = result.map { it.data }
                callback(documents)
            }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
