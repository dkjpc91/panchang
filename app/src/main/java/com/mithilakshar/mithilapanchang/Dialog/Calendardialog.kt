package com.mithilakshar.mithilapanchang.Dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.transition.Visibility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mithilakshar.mithilapanchang.R
import java.util.Locale

class calendardialog : Dialog, TextToSpeech.OnInitListener {
    // TextView references
    private var calendarText: TextView? = null
    private var todayTithi: TextView? = null
    private var tithiEndTime: TextView? = null
    private var todayNakshatra: TextView? = null
    private var nakshatraEndTime: TextView? = null
    private var todayMonth: TextView? = null
    private var todayRashi: TextView? = null
    private var todayPaksha: TextView? = null
    private var todaySunrise: TextView? = null
    private var todaySunset: TextView? = null
    private var todayYog: TextView? = null
    private var fab: FloatingActionButton? = null
    private var tts: TextToSpeech? = null
    private var speakText: String? = "hello"

    constructor(context: Context) : super(context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.calendardialog)
        // Initialize UI components based on the provided XML layout
        calendarText = findViewById(R.id.calendardialogtext)
        todayTithi = findViewById(R.id.todaytithi)
        tithiEndTime = findViewById(R.id.tithiendtime)
        todayNakshatra = findViewById(R.id.todaynakshatra)
        nakshatraEndTime = findViewById(R.id.nakshatraendtime)
        todayMonth = findViewById(R.id.todaymonth)
        todayRashi = findViewById(R.id.todayrashi)
        todayPaksha = findViewById(R.id.todaypaksha)
        todaySunrise = findViewById(R.id.todaysunrise)
        todaySunset = findViewById(R.id.todaysunset)
        todayYog = findViewById(R.id.todayyog)
        fab = findViewById(R.id.fab)

        tts = TextToSpeech(context, this)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)


    fun setCalendarDialogText(text: String?) {
        calendarText?.text = text
    }
    fun setTodayTithi(text: String?) {
        todayTithi?.text = text
        Log.d("speak", "$text")
    }
    fun setTithiEndTime(text: String?) {
        tithiEndTime?.text = text
    }  fun setTodayNakshatra(text: String?) {
        todayNakshatra?.text = text
    }
    fun setNakshatraEndTime(text: String?) {
        nakshatraEndTime?.text = text
    }

    fun setTodayMonth(text: String?) {
        todayMonth?.text = text
    }

    fun setTodayRashi(text: String?) {
        todayRashi?.text = text
    }

    fun setTodayPaksha(text: String?) {
        todayPaksha?.text = text
    }
    fun setTodaySunrise(text: String?) {
        todaySunrise?.text = text
    }

    fun setTodaySunset(text: String?) {
        todaySunset?.text = text
    }

    fun setTodayYog(text: String?) {
        todayYog?.text = text
    }

    // Set the text to speak
    fun setSpeakText(speakText: String?) {
        this.speakText = speakText
        Log.d("speak", "$speakText")
    }





    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {

        } else {
            // Initialization failed
        }
    }

    private fun speak(text: String) {
        if (tts == null) {
            Log.e("speak", "TextToSpeech not initialized")
            return
        }

        // Set language only if TTS is initialized
        val result = tts!!.setLanguage(Locale.forLanguageTag("hi"))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("speak", "Language not supported")
            return
        }

        Log.d("speak", "Speaking: $text")
        tts!!.setPitch(1f)
        tts!!.setSpeechRate(0.6f)
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }


    override fun dismiss() {
        Log.d("speak", "end Speaking: ")
        tts?.stop()
        tts?.shutdown()
        super.dismiss()
    }

}
