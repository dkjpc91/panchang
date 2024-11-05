package com.mithilakshar.mithilapanchang

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class CalendarDialog : Dialog, TextToSpeech.OnInitListener {

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
        initDialog(context)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        initDialog(context)
    }

    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
        initDialog(context)
    }

    private fun initDialog(context: Context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.calendardialog)
        initializeViews()
        tts = TextToSpeech(context, this)
    }

    private fun initializeViews() {
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
        fab = findViewById(R.id.fab) // Assuming fab is initialized elsewhere in your layout
    }

    // New method to set all the dialog values
    fun setDialogValues(
        tithi: String?,
        nakshatra: String?,
        month: String?,
        date: String?,
        sunrise: String?,
        sunset: String?,
        tithiEndtime: String?,
        nakshatraEndtime: String?,
        yog: String?,
        rashi: String?,
        paksha: String?
    ) {
        todayTithi?.text = tithi ?: "Unknown Tithi"
        todayNakshatra?.text = nakshatra ?: "Unknown Nakshatra"
        todayMonth?.text = month ?: "Unknown Month"
        calendarText?.text = date ?: "Unknown Date"
        todaySunrise?.text = sunrise ?: "Unknown Sunrise"
        todaySunset?.text = sunset ?: "Unknown Sunset"
        tithiEndTime?.text = tithiEndtime ?: "Unknown Tithi End Time"
        nakshatraEndTime?.text = nakshatraEndtime ?: "Unknown Nakshatra End Time"
        todayYog?.text = yog ?: "Unknown Yog"
        todayRashi?.text = rashi ?: "Unknown Rashi"
        todayPaksha?.text = paksha ?: "Unknown Paksha"
    }

    // Existing methods
    fun setSpeakText(speakText: String?) {
        this.speakText = speakText
        Log.d("CalendarDialog", "Speak Text Set: $speakText")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("hi"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("CalendarDialog", "Hindi language not supported for TTS")
            }
        } else {
            Log.e("CalendarDialog", "TTS initialization failed")
        }
    }

    private fun speak(text: String) {
        if (tts == null) {
            Log.e("CalendarDialog", "TextToSpeech not initialized")
            return
        }
        tts?.apply {
            setPitch(1f)
            setSpeechRate(0.6f)
            speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            Log.d("CalendarDialog", "Speaking: $text")
        }
    }

    override fun dismiss() {
        tts?.stop()
        tts?.shutdown()
        Log.d("CalendarDialog", "TTS stopped and shut down")
        super.dismiss()
    }
}
