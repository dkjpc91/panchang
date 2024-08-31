package com.mithilakshar.mithilapanchang.UI.View

import android.media.AudioAttributes

import android.media.MediaPlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.databinding.ActivityKathaDescriptionBinding
import com.squareup.picasso.Picasso


class KathaDescriptionActivity : AppCompatActivity() {

    lateinit var binding:ActivityKathaDescriptionBinding



    var kathaD: String? = null
    private var isFabClicked = false
    val mediaPlayer = MediaPlayer()
    var currentPlaybackPosition: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityKathaDescriptionBinding.inflate(layoutInflater)
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

        val intent = intent
        val kathaT = intent.getStringExtra("kathaName")
        kathaD = intent.getStringExtra("kathaStory")
        val kathaI = intent.getStringExtra("kathaUrl")
        val audioURL = intent.getStringExtra("audioURL")

        if (audioURL != null) {
            binding.fab.visibility=View.GONE
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA) // Set usage type (e.g., music, alarm)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // Set content type
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes)


        Picasso.get().load(kathaI).into(binding.kathaImg)
        switchFabColor(binding.fab)
        binding.kathatitle.text=kathaT
        binding.kathaDesc.text=kathaD


        binding.fab.setOnClickListener {
            isFabClicked = !isFabClicked
            if (isFabClicked) {
                binding.fab.setImageResource(R.drawable.speaker)
                switchFabColor(binding.fab)

                stopAudio()
                playAudio(audioURL!!)

            } else {
                binding.fab.setImageResource(R.drawable.mutespeaker)
                switchFabColor(binding.fab)
                stopAudio()

                binding.fab.visibility=View.INVISIBLE

            }
        }
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
        // Shutdown TextToSpeech engine
        stopAudio()

    }

    override fun onPause() {
        super.onPause()
        pauseAudio()
    }

    override fun onResume() {
        super.onResume()
        if (currentPlaybackPosition > 0) {
            mediaPlayer.seekTo(currentPlaybackPosition)
            mediaPlayer.start()
        }

    }

    fun pauseAudio() {
        if (mediaPlayer.isPlaying) {
            currentPlaybackPosition = mediaPlayer.currentPosition
            mediaPlayer.pause()
        }
    }

    fun stopAudio() {

        mediaPlayer.stop()

        mediaPlayer.reset()
        // Reset the media player before preparing a new audio source // Release resources after stopping playback
        binding.fab.post {
            binding.fab.visibility = View.VISIBLE
        }


    }

    fun playAudio(audioURL:String){



            try {
                // Set the data source for the MediaPlayer
                mediaPlayer.setDataSource(audioURL)

                // Prepare the MediaPlayer asynchronously
                mediaPlayer.prepareAsync()

                // Set a listener to handle when the MediaPlayer is prepared
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                }

            } catch (e: Exception) {
                // Handle error (e.g., show a Toast message)
                e.printStackTrace()
            }


        }


    }


