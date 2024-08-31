package com.mithilakshar.mithilapanchang.Utility

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.SeekBar

class AudioPlayer(private val context: Context) {


    var mediaPlayer: MediaPlayer? = null
    private var isPrepared: Boolean = false
    private var progressListener: ((Int) -> Unit)? = null


    // Function to prepare and optionally start the media player with a URL
    fun prepareAndPlayMedia(url: String, startImmediately: Boolean = true, onPrepared: (() -> Unit)? = null,onCompletion: (() -> Unit)? = null
    ) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener {
                    isPrepared = true
                    onPrepared?.invoke()
                    if (startImmediately) {
                        start()
                    }
                }
                setOnCompletionListener {
                    isPrepared = false
                    onCompletion?.invoke()


                }
                prepareAsync()
            }
        } else {
            if (isPrepared) {
                if (startImmediately) {
                    mediaPlayer?.start()
                }
            } else {

            }
        }
    }



    var position = 0




    fun AudioPlayerResume(){
        if(position>0){
            mediaPlayer?.seekTo(position)
            mediaPlayer?.start()
        }

    }

    fun AudioPlayerSeekto(p: Int){
        if(p>0){
            mediaPlayer?.seekTo(p)
            mediaPlayer?.start()
        }

    }

    fun AudioPlayerPause() {


        if(mediaPlayer!!.isPlaying){
            position= mediaPlayer!!.currentPosition
            mediaPlayer!!.pause()
        }

    }

    fun AudioPlayerStop() {
        mediaPlayer?.apply {
            stop()
            reset()
        }
        mediaPlayer = null
        isPrepared = false
    }

    fun AudioPlayerRelease() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPrepared = false
    }




    // Function to set the progress listener
    // Function to set the progress listener
    fun setProgressListener(listener: (Int) -> Unit) {
        this.progressListener = listener
        /*mediaPlayer?.setOnSeekCompleteListener {
            listener.invoke(currentPosition)
        }*/
    }

    // Getter for the current position
    val currentPosition: Int
        get() = mediaPlayer?.currentPosition ?: 0

    // Getter for the duration
    val duration: Int
        get() = mediaPlayer?.duration ?: 0

    fun setCompletionListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            listener.invoke()
        }
    }




}