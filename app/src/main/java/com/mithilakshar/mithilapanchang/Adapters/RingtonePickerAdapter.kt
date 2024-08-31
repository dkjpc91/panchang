package com.mithilakshar.mithilapanchang.Adapters

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mithilakshar.mithilapanchang.R

class RingtonePickerAdapter(
    context: Context,
    private val ringtoneNames: Array<String>,
    private val ringtones: Array<Int>,
    private val ringtoneSelectedListener: (Int, String, String) -> Unit // Callback for ringtone selection
) : ArrayAdapter<String>(context, 0, ringtoneNames) {

    private var mediaPlayer: MediaPlayer? = null



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_ringtone_picker, parent, false)
            viewHolder = ViewHolder()
            viewHolder.ringtoneNameTextView = view.findViewById(R.id.titleTextView)
            viewHolder.playButton = view.findViewById(R.id.playButton)
            viewHolder.stopButton = view.findViewById(R.id.stopButton)
            viewHolder.saveButton = view.findViewById(R.id.saveButton)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.ringtoneNameTextView.text = ringtoneNames[position]

        viewHolder.playButton.setOnClickListener {
            playRingtone(ringtones[position])
        }

        viewHolder.stopButton.setOnClickListener {
            stopRingtone()
        }

        viewHolder.saveButton.setOnClickListener {
            val title = "" // You can implement title selection if needed
            val messageText = "" // You can implement message text selection if needed
            ringtoneSelectedListener(ringtones[position], title, messageText)
            stopRingtone()


        }

        return view!!
    }

    private fun playRingtone(ringtoneResId: Int) {
        stopRingtone()
        mediaPlayer = MediaPlayer.create(context, ringtoneResId)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            stopRingtone()
        }
    }

    private fun stopRingtone() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // ViewHolder pattern to optimize view lookups
    private class ViewHolder {
        lateinit var ringtoneNameTextView: TextView
        lateinit var playButton: Button
        lateinit var stopButton: Button
        lateinit var saveButton: Button
    }
}
