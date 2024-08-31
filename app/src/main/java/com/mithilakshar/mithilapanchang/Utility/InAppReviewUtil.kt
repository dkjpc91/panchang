package com.mithilakshar.mithilapanchang.Utility
import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
class InAppReviewUtil (private val activity: Activity) {

    private val reviewManager: ReviewManager = ReviewManagerFactory.create(activity)

    fun startReviewFlow() {
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow: Task<Void> = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished, handle the result
                    // You can log the result or perform additional actions if needed
                }
            } else {
                // There was some problem, log or handle the error code
                // task.exception could provide more information
            }
        }
    }
}