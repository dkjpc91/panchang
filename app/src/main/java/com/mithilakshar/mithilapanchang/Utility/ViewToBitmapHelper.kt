import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.databinding.HolidaybanneritemBinding

class ViewToBitmapHelper(private val activity: AppCompatActivity) {

    private val dpToPx: (Int) -> Int = { dp ->
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        (dp * metrics.density + 0.5f).toInt()
    }

    fun generateBitmapsFromList(list: List<Map<String, String>>?): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()

        // Check if the list is null
        if (list == null) {
            return bitmaps
        }

        val inflater = LayoutInflater.from(activity)
        val widthInPixels = dpToPx(400) // Convert 100dp to pixels

        // Inflate a dummy container to measure and layout the views
        val containerView = inflater.inflate(R.layout.holidaybanneritem, null) as ViewGroup
        val container = containerView.findViewById<ViewGroup>(R.id.holidaysliderview)

        if (container == null) {
            throw IllegalStateException("Container view is null")
        }

        for (item in list) {
            val binding = HolidaybanneritemBinding.inflate(inflater, container, false)

            // Populate the view with details from the list
            binding.holidayname.text = item["name"] ?: ""
            binding.holidaygreeting.text = (item["date"] ?: "") +"\n\n"+ (item["desc"] ?: "")

            // Set the width to 100dp and measure the height accordingly
            val layoutParams = ViewGroup.LayoutParams(widthInPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
            binding.root.layoutParams = layoutParams

            binding.root.measure(
                View.MeasureSpec.makeMeasureSpec(widthInPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED)
            )
            binding.root.layout(0, 0, binding.root.measuredWidth, binding.root.measuredHeight)

            val width = binding.root.measuredWidth
            val height = binding.root.measuredHeight

            if (width <= 0 || height <= 0) {
                Log.e("ViewToBitmapHelper", "Invalid bitmap dimensions: width = $width, height = $height")
                continue
            }

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            binding.root.draw(canvas)

            bitmaps.add(bitmap)
        }

        return bitmaps
    }
}
