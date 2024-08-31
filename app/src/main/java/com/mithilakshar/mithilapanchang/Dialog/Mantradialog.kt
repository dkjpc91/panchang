package com.mithilakshar.mithilapanchang.Dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.core.widget.NestedScrollView
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.Utility.ViewShareXUtil
import com.squareup.picasso.Picasso

class Mantradialog : Dialog {
    private var mantratext: TextView? = null
    private var mantradesc: TextView? = null
    private var shareImage: ImageView? = null
    private var shareXImage: ImageView? = null
    private var banner_image: ImageView? = null





    constructor(context: Context) : super(context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.mantradialog)
        mantratext = findViewById(R.id.sanskrit_quote)
        mantradesc = findViewById(R.id.hindi_translation)
        shareImage = findViewById(R.id.shareicon)
        shareXImage = findViewById(R.id.iconxshare)
        banner_image = findViewById(R.id.banner_image)

        val shareImageroot: NestedScrollView?  = findViewById(R.id.shareview)

        shareImage?.setOnClickListener {
            if (shareImageroot != null) {
                sharemantraimage(shareImageroot, context)
            }
        }

        shareXImage?.setOnClickListener {
            if (shareImageroot != null) {
                sharexmantraimage(shareImageroot, context)
            }
        }

    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    protected constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    fun setmantradialogtext(text: String?) {
        mantratext?.text = text
    }
    fun setmantradialogtext1(text: String?) {
        mantradesc?.text = text
    }

    fun sharemantraimage(view: NestedScrollView,context: Context) {


        ViewShareUtil.shareViewAsImageDirectly(view, context)
    }

    fun sharexmantraimage(view: NestedScrollView,context: Context) {

        ViewShareXUtil.shareViewAsImageDirectly(view,context)

    }

    fun setmantraimage(text: String?) {
        Picasso .get()
            .load(text)  // Replace with your image URL
            .into(banner_image)
    }




}
