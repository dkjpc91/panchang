package com.mithilakshar.mithilapanchang.UI.View


import android.animation.ArgbEvaluator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import android.animation.ObjectAnimator

import com.mithilakshar.mithilapanchang.R

import com.mithilakshar.mithilapanchang.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val animation = AnimationUtils.loadAnimation(this, R.anim.fade)
        animation.duration = 2500
        binding.root.animation=animation

        val layout = binding.splash // Replace with your layout's ID

        val startColor = Color.argb(255, 255, 0, 0) // Red (full opacity)
        val endColor = Color.TRANSPARENT // Transparent

        val colorFade = ObjectAnimator.ofObject(layout, "backgroundColor", ArgbEvaluator(), startColor, endColor)
        colorFade.setDuration(1000) // Adjust du ration in milliseconds (1 second here)
        colorFade.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val i = Intent(this, HomeActivity::class.java)
            startActivity(i)
            finish()
        }, 2500)
    }


}