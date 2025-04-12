package com.mithilakshar.mithilapanchang.UI.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseUser
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.GoogleSignInHelper
import com.mithilakshar.mithilapanchang.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {

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
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
