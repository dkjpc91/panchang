package com.mithilakshar.mithilapanchang.Utility

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mithilakshar.mithilapanchang.R

class LayoutBitmapGenerator(private val context: Context) {

    fun generateBitmap(
        holidayNameData: String,
        holidayGreetingData: String,
        imageUrl: String,
        callback: (Bitmap?) -> Unit
    ) {
        Log.d("BitmapGenerator", "Starting bitmap generation")

        // Fixed width for the view
        val viewWidth = 1080

        // Fetch the bitmap from URL
        Bitmapgreeringprovider.fetchBitmapFromUrl(imageUrl) { bitmap ->
            if (bitmap != null) {
                // Set the bitmap in Bitmapgreeringprovider
                val bitmapUri = Uri.parse("content://com.mithilakshar.mithilapanchang.Bitmapgreeringprovider/bitmap")
                Bitmapgreeringprovider.setBitmap(bitmapUri, bitmap)

                // Ensure UI operations are on the main thread
                Handler(Looper.getMainLooper()).post {
                    // Inflate the layout
                    val inflater = LayoutInflater.from(context)
                    val view: View = inflater.inflate(R.layout.greetingbannerlayout, null)
                    Log.d("BitmapGenerator", "Layout inflated successfully")

                    // Find the views
                    val holidayImage: ImageView = view.findViewById(R.id.holidayimage)
                    val holidayName: TextView = view.findViewById(R.id.holidayname)
                    val holidayGreeting: TextView = view.findViewById(R.id.holidaygreeting)

                    // Set the text
                    holidayName.text = holidayNameData
                    holidayGreeting.text = insertNewlinesEveryNChars(holidayGreetingData, 38)
                    Log.d("BitmapGenerator", "Holiday name set to: $holidayNameData")
                    Log.d("BitmapGenerator", "Holiday greeting set to: $holidayGreetingData")

                    // Set the bitmap to the ImageView
                    holidayImage.setImageBitmap(bitmap)
                    Log.d("BitmapGenerator", "Bitmap set to ImageView")

                    // Set fixed width and let height be WRAP_CONTENT
                    val layoutParams = ViewGroup.LayoutParams(viewWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                    view.layoutParams = layoutParams

                    // Measure and layout the view
                    view.measure(View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                    view.layout(0, 0, view.measuredWidth, view.measuredHeight)

                    // Check if dimensions are valid
                    if (view.measuredWidth > 0 && view.measuredHeight > 0) {
                        // Generate bitmap of the entire view
                        val viewBitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(viewBitmap)
                        view.draw(canvas)
                        Log.d("BitmapGenerator", "Bitmap created from view")

                        callback(viewBitmap)
                    } else {
                        Log.e("BitmapGenerator", "Invalid view dimensions")
                        callback(null)
                    }
                }
            } else {
                Log.e("BitmapGenerator", "Failed to fetch bitmap from URL")
                callback(null)
            }
        }
    }

    fun insertNewlinesEveryNChars(input: String, n: Int): String {
        val result = StringBuilder()
        var index = 0
        while (index < input.length) {
            val end = (index + n).coerceAtMost(input.length)
            result.append(input.substring(index, end))
            if (end < input.length) {
                result.append("\n")
            }
            index += n
        }
        return result.toString()
    }
}
