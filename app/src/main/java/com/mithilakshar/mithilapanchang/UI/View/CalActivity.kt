package com.mithilakshar.mithilapanchang.UI.View

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.UI.Fragments.calfragment
import com.mithilakshar.mithilapanchang.Utility.GoogleSignInHelper
import com.mithilakshar.mithilapanchang.databinding.ActivityCalBinding

class CalActivity : AppCompatActivity() {

    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var binding: ActivityCalBinding

    val handler = Handler(Looper.getMainLooper())
    val totalDuration = 1200L
    val interval = 500L
    private lateinit var adView3: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lottieView: LottieAnimationView =  binding.lottieAnimationView
        val animationList = listOf(
            R.raw.a1 ,
            R.raw.a2  ,
            R.raw.k3  ,

    R.raw.a3  ,
         R.raw.a4  ,
          R.raw.om  ,
      R.raw.solar  ,
        )
        val randomAnimation = animationList.random()
        lottieView.setAnimation(randomAnimation) // Reference to new animation in raw folder
        lottieView.playAnimation()


        val startTime = System.currentTimeMillis()
        val runnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                if (elapsedTime < totalDuration) {
                    // Your task (e.g., update UI, show a Toast, or log something)


                    // Re-post the Runnable to run again after the interval
                    handler.postDelayed(this, interval)
                } else {
                    // Task completed after 10 seconds
                    binding.lottieAnimationView.visibility= View.GONE
                    binding.loadingstatus.visibility= View.GONE
                    binding.adView3.visibility= View.VISIBLE
                }
            }
        }

        adView3 = findViewById(R.id.adView3)
        val adRequest = AdRequest.Builder().build()
        // Set an AdListener to make the AdView visible when the ad is loaded
        adView3.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView3.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView3.loadAd(adRequest)


        handler.post(runnable)

        replaceFragment(calfragment())

        googleSignInHelper = GoogleSignInHelper(this)

        binding.logout.setOnClickListener {
            googleSignInHelper.signOut()

        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager // Get FragmentManager from Activity
        val transaction = fragmentManager.beginTransaction() // Begin transaction
        transaction.replace(R.id.fragmentContainer, fragment) // Replace container with fragment
        transaction.commit() // Commit transaction
    }

}