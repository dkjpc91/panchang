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
    private var calendartext: TextView? = null
    private var calendartext1: TextView? = null
    private var calendartext2: TextView? = null
    private var calendartext3: TextView? = null
    private var calendartext4: TextView? = null
    private var calendartext5: TextView? = null
    private var calendardialogtext5l: LinearLayout? = null
    private var fab: FloatingActionButton? = null
    private var tts: TextToSpeech? = null
    private var speakText: String? = "hello"

    constructor(context: Context) : super(context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.calendardialog)
        calendartext = findViewById(R.id.calendardialogtext)
        calendartext1 = findViewById(R.id.calendardialogtext1)
        calendartext3 = findViewById(R.id.calendardialogtext3)
        calendartext4 = findViewById(R.id.calendardialogtext4)
        calendartext5 = findViewById(R.id.calendardialogtext5)
        fab = findViewById(R.id.fab)
        calendardialogtext5l = findViewById(R.id.calendardialogtext5l)

        tts = TextToSpeech(context, this)
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    fun setcalendardialogtext(text: String?) {
        calendartext?.text = text
    }

    fun setcalendardialogtext1(text: String?) {
        calendartext1?.text = text
        Log.d("speak", "$text")
    }
    fun setcalendardialogtext2(speaktext: String?) {
        speakText= speaktext

        Log.d("speak", "$speaktext")
        Log.d("speak", "$speakText")
    }
    fun setcalendardialogtext3(text: String?) {
        calendartext3?.text = text
    }

    fun setcalendardialogtext4(text: String?) {
        calendartext4?.text = text
    }

    fun setcalendardialogtext5(text: String?) {

        if (text.isNullOrBlank()) {
            calendartext5?.visibility = View.GONE // or View.GONE
            calendardialogtext5l?.visibility = View.GONE // or View.GONE
        } else {
            calendartext5?.visibility = View.VISIBLE
            calendartext5?.text = text
        }
        fab?.setOnClickListener {

            speakText?.let { it1 -> speak(it1) }
            Log.d("speak", "$speakText")
        }

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
