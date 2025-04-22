package com.mithilakshar.mithilapanchang.UI.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseUser
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.GoogleSignInHelper
import com.mithilakshar.mithilapanchang.databinding.ActivityTestBinding
import android.net.Uri
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController

class TestActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    private lateinit var binding: ActivityTestBinding
    private lateinit var googleSignInHelper: GoogleSignInHelper

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        googleSignInHelper.handleSignInResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }

        videoView = binding.backgroundVideo
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.background1}")
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()


        googleSignInHelper = GoogleSignInHelper(this)
        showLoading(true)



        googleSignInHelper.getCurrentUser()?.let {
            updateUI(it)
        } ?: run {
            showLoading(false)
        }

        binding.signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        showLoading(true)
        googleSignInHelper.signIn(
            launcher = signInLauncher,
            onSuccess = { user ->
                showLoading(false)
                updateUI(user)
            },
            onFailure = { exception ->
                showLoading(false)
                Toast.makeText(
                    this,
                    "Authentication failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("uid", it.uid)
                putExtra("displayName", it.displayName)
                putExtra("email", it.email)
                putExtra("photoUrl", it.photoUrl?.toString())
            }
            startActivity(intent)
            finish()

        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
    }
}
