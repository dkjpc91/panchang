package com.mithilakshar.mithilapanchang.Utility

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mithilakshar.mithilapanchang.R

class GoogleSignInHelper(private val activity: Activity) {


    private val oneTapClient: SignInClient = Identity.getSignInClient(activity)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var onSuccess: ((FirebaseUser) -> Unit)? = null
    private var onFailure: ((Exception) -> Unit)? = null

    private val TAG = "GoogleSignInHelper"

    /**
     * Initiates Google One Tap sign-in.
     */
    fun signIn(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        this.onSuccess = onSuccess
        this.onFailure = onFailure

        Log.d(TAG, "Starting sign-in process")

        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    Log.d(TAG, "Launching One Tap UI")
                    launcher.launch(intentSenderRequest)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "IntentSender exception: ${e.message}", e)
                    onFailure.invoke(e)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "One Tap sign-in failed: ${e.message}", e)
                onFailure.invoke(e)
            }
    }

    /**
     * Handles the result returned by the One Tap UI.
     */
    fun handleSignInResult(result: ActivityResult) {
        Log.d(TAG, "Handling sign-in result: resultCode=${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    Log.d(TAG, "ID token retrieved, authenticating with Firebase")
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Log.e(TAG, "No ID token found in credential")
                    onFailure?.invoke(Exception("No ID token found"))
                }
            } catch (e: ApiException) {
                Log.e(TAG, "API exception during credential extraction: ${e.message}", e)
                onFailure?.invoke(e)
            }
        } else {
            Log.e(TAG, "Sign-in canceled or failed with result code: ${result.resultCode}")
            onFailure?.invoke(Exception("Sign-in canceled"))
        }
    }

    /**
     * Authenticates with Firebase using the Google ID token.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d(TAG, "Firebase authentication successful: ${user?.email}")
                    user?.let { onSuccess?.invoke(it) }
                } else {
                    Log.e(TAG, "Firebase authentication failed", task.exception)
                    onFailure?.invoke(task.exception ?: Exception("Unknown Firebase error"))
                }
            }
    }

    /**
     * Signs out the user from both One Tap and Firebase.
     */
    fun signOut() {
        Log.d(TAG, "Signing out user")
        oneTapClient.signOut().addOnCompleteListener {
            auth.signOut()
            Log.d(TAG, "Sign-out complete")
        }
    }

    /**
     * Gets the currently signed-in Firebase user.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
}
