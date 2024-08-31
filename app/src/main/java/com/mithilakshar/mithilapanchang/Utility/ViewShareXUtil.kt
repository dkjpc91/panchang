package com.mithilakshar.mithilapanchang.Utility
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
class ViewShareXUtil {

    companion object {
        private const val TWITTER_IMAGE_WIDTH = 1080
        private const val TWITTER_IMAGE_HEIGHT = 1350

        fun viewToBitmap(view: View): Bitmap {
            val originalBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(originalBitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            view.draw(canvas)



            // Create scaled bitmap to Twitter's recommended dimensions
            return Bitmap.createScaledBitmap(originalBitmap, TWITTER_IMAGE_WIDTH, TWITTER_IMAGE_HEIGHT, true)
        }

        fun shareViewAsImageDirectly(view: View, context: Context) {
            val bitmap = viewToBitmap(view)
            val contentUri = Uri.parse("content://${context.packageName}.bitmapprovider")
            BitmapContentProvider.setBitmap(contentUri, bitmap)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                val shareText = "मिथिला पंचांग ऐप: \n\n@mithilakshar13"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "मिथिला पंचांग ऐप"))
        }
    }
}