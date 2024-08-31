package com.mithilakshar.mithilapanchang.UI.View

import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech

import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope

import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel
import com.mithilakshar.mithilapanchang.databinding.ActivityBoardDetailBinding

import kotlinx.coroutines.launch
import java.util.Locale



class BoardDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var binding:ActivityBoardDetailBinding
    private var textToSpeech: TextToSpeech? = null
    var G1: String? = ""
    var G2: String? = ""
    private var isFabClicked = false

    val handler1 = Handler(Looper.getMainLooper())
    val viewModel: BhagwatGitaViewModel by lazy {
        ViewModelProvider(this).get(BhagwatGitaViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBoardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val networkdialog = Networkdialog(this)
        val networkManager= NetworkManager(this)
        networkManager.observe(this, {
            if (!it){
                if (!networkdialog.isShowing){networkdialog.show()}

            }else{
                if (networkdialog.isShowing){networkdialog.dismiss()}

            }
        })


        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)

        textToSpeech = TextToSpeech(this, this)

        lifecycleScope.launch {

            val i = intent

            G1= i.getStringExtra("g1")
            G2= i.getStringExtra("g2")

        binding.sholktext.text=G1
            binding.fab.setOnClickListener {

                isFabClicked = !isFabClicked
                if (isFabClicked) {


                    binding.fab.visibility= View.GONE

                    delayedTask(1000)
                    handler1.postDelayed({ // Your code to be executed after the delay

                        binding.fab.visibility= View.VISIBLE
                        isFabClicked = !isFabClicked

                    }, 2000)


                } else {

                    switchFabColor(binding.fab)

                }


            }

            binding.bhagwatimage.setOnClickListener {

                ViewShareUtil.shareViewAsImageDirectly(binding.root,this@BoardDetailActivity)
            }



        }





    }

    private fun translateToHindidate(date: String): String? {
        // Manually create a mapping for English to Hindi month names
        val nmap: MutableMap<String, String> = HashMap()
        nmap["1"] = "१"
        nmap["2"] = "२"
        nmap["3"] = "३"
        nmap["4"] = "४"
        nmap["5"] = "५"
        nmap["6"] = "६"
        nmap["7"] = "७"
        nmap["8"] = "८"
        nmap["9"] = "९"
        nmap["10"] = "१०"
        nmap["11"] = "११"
        nmap["12"] = "१२"
        nmap["13"] = "१३"
        nmap["14"] = "१४"
        nmap["15"] = "१५"
        nmap["16"] = "१६"
        nmap["17"] = "१७"
        nmap["18"] = "१८"
        nmap["19"] = "१९"
        nmap["20"] = "२०"
        nmap["21"] = "२१"
        nmap["22"] = "२२"
        nmap["23"] = "२३"
        nmap["24"] = "२४"
        nmap["25"] = "२५"
        nmap["26"] = "२६"
        nmap["27"] = "२७"
        nmap["28"] = "२८"
        nmap["29"] = "२९"
        nmap["30"] = "३०"
        nmap["31"] = "३१"
        // Return the translated month name
        return nmap[date]
    }

            override fun onInit(status: Int) {

                if (status == TextToSpeech.SUCCESS) {


                }
            }
    

    private fun onSpeechCompleted() {

       binding. fab.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fabColorSwitched)
    }

    private fun switchFabColor(fab: FloatingActionButton) {
        if (isFabClicked) {
            // Set the original color if it's switched
            fab.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fabColorOriginal)

        } else {
            // Set the switched color
            fab.backgroundTintList = ContextCompat.getColorStateList(this, R.color.fabColorSwitched)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech!!.stop()
        textToSpeech!!.shutdown()
    }
    override fun onPause() {
        super.onPause()
        textToSpeech!!.stop()
        textToSpeech!!.shutdown()
    }

    private fun delayedTask(delayMillis: Int) {



        // Request audio focus (optional)

        handler1.postDelayed({ // Your code to be executed after the delay
            textToSpeech!!.setLanguage(Locale.forLanguageTag("hi"))

            // Speak text
            textToSpeech!!.setPitch(1f)
            textToSpeech!!.setSpeechRate(0.6f)
            textToSpeech!!.speak(G2, TextToSpeech.QUEUE_FLUSH, null, null)
        }, delayMillis.toLong())
    }



}


