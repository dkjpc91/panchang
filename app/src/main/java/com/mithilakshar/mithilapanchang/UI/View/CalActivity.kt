package com.mithilakshar.mithilapanchang.UI.View

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.UI.Fragments.calfragment
import com.mithilakshar.mithilapanchang.databinding.ActivityCalBinding

class CalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalBinding


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

        replaceFragment(calfragment())


    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager // Get FragmentManager from Activity
        val transaction = fragmentManager.beginTransaction() // Begin transaction
        transaction.replace(R.id.fragmentContainer, fragment) // Replace container with fragment
        transaction.commit() // Commit transaction
    }

}