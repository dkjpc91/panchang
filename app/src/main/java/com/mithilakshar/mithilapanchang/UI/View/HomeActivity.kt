package com.mithilakshar.mithilapanchang.UI.View



import android.content.Intent
import java.time.format.TextStyle
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.firebase.firestore.FirebaseFirestore
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog

import com.mithilakshar.mithilapanchang.Notification.NetworkManager


import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.ViewModel.BhagwatGitaViewModel
import com.mithilakshar.mithilapanchang.ViewModel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

import android.content.ContentValues.TAG

import android.net.Uri


import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest


import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


import com.google.firebase.auth.FirebaseAuth


import com.mithilakshar.mithilapanchang.Adapters.SliderAdapter
import com.mithilakshar.mithilapanchang.R


import com.mithilakshar.mithilapanchang.Room.Updates
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase

import com.mithilakshar.mithilapanchang.Utility.LayoutBitmapGenerator
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil

import com.mithilakshar.mithilapanchang.Utility.dbDownloader
import com.mithilakshar.mithilapanchang.databinding.ActivityHomeBinding
import java.io.File


@RequiresApi(Build.VERSION_CODES.DONUT)
class HomeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {




    lateinit var binding: ActivityHomeBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    private lateinit var updatesDao: UpdatesDao

    private lateinit var dbHelpercalander: dbHelper
    private lateinit var dbHelperimage: dbHelper
    private lateinit var dbHelperholiday: dbHelper

    private lateinit var auth: FirebaseAuth

    private  var holidaybannerurl :String =""

    val mediaPlayer = MediaPlayer()
    var currentPlaybackPosition: Int = 0

    val handler = Handler(Looper.getMainLooper())

    private var isFabClicked = false

    private var textToSpeech: TextToSpeech? = null
    var speak: String? = "मिथिला पंचांग में आहाँ के स्वागत अई"
    var homeBroadcast: String = ""


    val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private lateinit var fileDownloader: FirebaseFileDownloader

    companion object {
        private const val REQUEST_WRITE_STORAGE = 1
    }

    var G1 = ""
    var G2 = ""

    private lateinit var fileExistenceLiveData: LiveData<Boolean>

    private lateinit var adView1: AdView
    private lateinit var adView: AdView
    private lateinit var adviewMR: AdView


    //slider
    private lateinit var handler1: Handler
    private lateinit var runnable: Runnable
    private var delayMillis: Long = 4000
    private lateinit var viewPager: ViewPager2
    private lateinit var sliderAdapter: SliderAdapter

    var holidayMonthList: MutableList<Map<String, String>> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }




        checkForAppUpdate()

        val networkdialog = Networkdialog(this)
        val networkManager = NetworkManager(this)
        networkManager.observe(this, {
            if (!it) {
                if (!networkdialog.isShowing) {
                    networkdialog.show()
                }

            } else {
                if (networkdialog.isShowing) {
                    networkdialog.dismiss()
                }

            }
        })

        adView1 = findViewById(R.id.adView1)

        adView = binding.adView
        adviewMR = binding.adviewMR


        val adRequest = AdRequest.Builder().build()


        // Set an AdListener to make the AdView visible when the ad is loaded
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView1.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView1.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adviewMR.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adviewMR.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }

        val randomValue = (1..100).random()

        if (randomValue <= 70) {
            // Load adviewMR 70% of the time
            adviewMR.loadAd(adRequest)
        } else {
            // Load adView 30% of the time
            adView.loadAd(adRequest)
        }


       adView1.loadAd(adRequest)




        val maxHeightInDp = 700
        val maxHeightInPx = (maxHeightInDp * resources.displayMetrics.density).toInt()

        val imageView = binding.homeBanner

        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val contentHeight = imageView.height
                val params = imageView.layoutParams as ViewGroup.LayoutParams

                // If the content height is greater than maxHeight, use maxHeight
                params.height = if (contentHeight > maxHeightInPx) maxHeightInPx else ViewGroup.LayoutParams.WRAP_CONTENT
                imageView.layoutParams = params

                // Remove the listener to prevent it from being called again
                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }

// Add the global layout listener
        imageView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)




        val cDate: LocalDate = LocalDate.now()
        val currentMonthString: String = cDate.month.name // Gets the current month in uppercase (e.g., "JANUARY")
        val currentDay: Int = cDate.dayOfMonth

        val currentDayName: String = cDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()

        fileDownloader = FirebaseFileDownloader(this)

        updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()

        val dbDownloader= dbDownloader(updatesDao,fileDownloader)

        dbDownloader.observeFileExistence("imageauto",this,lifecycleScope,5,this,progressCallback = { progress ->
            runOnUiThread {
                // Update UI with the progress
                Log.d("HomeActivity", "Download progress: $progress%")
                // You can display progress using a ProgressBar or any other UI component
            }
        })
        dbDownloader.observeFileExistence("calander",this,lifecycleScope,3,this,progressCallback = { progress ->
            runOnUiThread {
                // Update UI with the progress
                Log.d("HomeActivity", "Download progress: $progress%")
                // You can display progress using a ProgressBar or any other UI component
            }
        })
        dbDownloader.observeFileExistence("holiday",this,lifecycleScope,2,this,progressCallback = { progress ->
            runOnUiThread {
                // Update UI with the progress
                Log.d("HomeActivity", "Download progress: $progress%")
                // You can display progress using a ProgressBar or any other UI component
            }
        })


        dbHelpercalander = dbHelper(this, "calander.db")

        dbHelperimage = dbHelper(this, "imageauto.db")
        dbHelperholiday = dbHelper(this, "holiday.db")

        val doescolumnexist=dbHelperholiday.doesColumnExist("holiday","datenumber")
        Log.d("doescolumnexist", "$doescolumnexist")
        holidayMonthList.add(mapOf(
            "month" to "",
            "date" to "",
            "name" to "मिथिला पंचांग",
            "desc" to ""
            ))

        if (doescolumnexist){

            holidayMonthList= dbHelperholiday.getHolidaysByMonthdate(currentMonthString.lowercase(Locale.getDefault()),
                currentDay.toString()).toMutableList()
            Log.d("doescolumnexist", "dateexist $doescolumnexist")

        }else{

            holidayMonthList= dbHelperholiday.getHolidaysByMonth(currentMonthString.lowercase(Locale.getDefault()))
                .toMutableList()
            Log.d("doescolumnexist", "notexist $doescolumnexist")
        }



        Log.d("holidaymonthlist", "$holidayMonthList")
        handler1 = Handler(Looper.getMainLooper())
        sliderAdapter = SliderAdapter(holidayMonthList)
        viewPager=binding.viewPager
        viewPager.adapter=sliderAdapter

        runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % sliderAdapter.itemCount
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delayMillis)
            }
        }


        auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "",
                        Toast.LENGTH_SHORT,
                    ).show()
                   // updateUI(null)
                }
            }






         startAutoScroll()






        val rowsFormonthdate = dbHelpercalander.getRowByMonthAndDate(currentMonthString,currentDay.toString())

         speak = rowsFormonthdate?.get(key = "speak") ?:"मिथिला पंचांग में आहाँ के स्वागत अई"
        Log.d("speak", "$speak")



        val holidaytoday= rowsFormonthdate?.get(key = "holiday")
        val holidaydesc= rowsFormonthdate?.get(key = "holidaydesc")
        Log.d("todayimage", "$holidaytoday")
        val holidayurl= dbHelperimage.getimageByholidayname(rowsFormonthdate?.get("holiday").toString())
        Log.d("holidayurl", "$holidayurl")

        if (holidayurl.isNotEmpty()) {
            Log.d("holidayurl", "holidayurl.isNotEmpty()")
            // Select a random entry from the list
            val randomEntry = holidayurl[Random.nextInt(holidayurl.size)]
            val randomImageUrl = randomEntry["imageurl"] // Replace "url" with the actual key that holds the image URL
            Log.d("holidayurl", "$randomImageUrl")
            // Now you can use randomImageUrl in your generateBitmap function

            if (randomImageUrl != null) {
                holidaybannerurl=randomImageUrl
            }

            val holidayNameData = holidaytoday.toString()
            val holidayGreetingData = holidaydesc.toString()
            val layoutBitmapGenerator = LayoutBitmapGenerator(this)

            // Generate bitmap from the layout
            layoutBitmapGenerator.generateBitmap(holidayNameData, holidayGreetingData, holidaybannerurl) { generatedBitmap ->
                if (generatedBitmap != null) {
                    // Use the generated bitmap, for example, display it in an ImageView

                    Glide.with(this)
                        .load(generatedBitmap)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Avoid caching issues
                        .skipMemoryCache(true) // Skip memory cache if needed
                        .into(binding.homeBanner)
                } else {
                    // Handle the error
                    Log.e("MyActivity", "Failed to generate bitmap from layout")
                }
            }

            val todayimage=dbHelperimage.getimageByholidayname(holidaytoday.toString())



            handler.postDelayed({

                setupAppBarBanner(todayimage,viewModel)
            }, 20000)






        }else{
            Log.d("holidayurl", "holidayurl.empty()")
                val todayimage=dbHelperimage.getimageByDayName(currentDayName)
                Log.d("holidayurl", "${currentDayName}")
                Log.d("holidayurl", "$todayimage")

                setupAppBarBanner(todayimage,viewModel)

        }


        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.forLanguageTag("hi")
                Log.d("speak", "TTS success")
                delayedTask(1000,speak.toString())
            } else {
                Log.d("speak", "TTS failed")
            }
        })













        //observeFileExistence("Gita")













        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA) // Set usage type (e.g., music, alarm)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // Set content type
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes)







        //date

        val currentDate = LocalDate.now()

        val hindiMonth = translateToHindi(currentDate.month.toString())
        val hindiDay = translateToHindiday(currentDate.dayOfWeek.toString())
        val hindidate = translateToHindidate(currentDate.dayOfMonth.toString())




        //text speak auto data.

        /*     firestoreRepo.getspeaktext(currentDate.dayOfMonth.toString().padStart(2, '0'),
                 currentDate.month.toString().lowercase(Locale.getDefault())
                     .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) {

                 if (it != null) {
                     //speak = it

                 }

             }*/




        //text speak broadcast
        binding.floatingActionButton.setOnClickListener {
            isFabClicked = !isFabClicked
            if (isFabClicked) {


                switchFabColor(binding.floatingActionButton)

                delayedBroadcast(500)


            } else {

                stopAudio()
                switchFabColor(binding.floatingActionButton)

            }

        }






        binding.apply {

            textViewDate.text = hindidate
            textViewDay.text = hindiDay
            textViewMonth.text = hindiMonth

        }














        binding.alarm.setOnClickListener {
            val i = Intent(this, AlarmActivity::class.java)

            startActivity(i)
            stopAudio()
        }

        binding.calendar.setOnClickListener {
            val i = Intent(this, CalendarActivity::class.java)

            startActivity(i)
            stopAudio()
        }

        binding.holiday.setOnClickListener {
            val i = Intent(this, HolidayActivity::class.java)

            startActivity(i)
            stopAudio()
        }



        binding.katha.setOnClickListener {
            val i = Intent(this, KathaActivity::class.java)

            startActivity(i)
            stopAudio()
        }



        binding.purchase.setOnClickListener {
            val i = Intent(this, BillingActivity::class.java)

            startActivity(i)
            stopAudio()
        }


        binding.shareicon.setOnClickListener {
            ViewShareUtil.shareViewAsImageDirectly(binding.homeBanner,this)

        }



        binding.share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                // Put the text to share in the intent

                val shareText = "नीचा देल गेल लिंक पर क्लिक क मिथिला पंचांग ऐप्प डाउनलोड करू .\n https://play.google.com/store/apps/details?id=com.mithilakshar.mithilapanchang  \n\n\n @mithilakshar13"

                putExtra(Intent.EXTRA_TEXT, shareText)
                // Set the MIME type
                type = "text/plain"
            }
            // Start the activity with the intent
            startActivity(Intent.createChooser(intent, "साझा करू : "))
        }

        binding.otherapps.setOnClickListener {

            val developerPageUrl = "https://play.google.com/store/apps/dev?id=8281901881443422197&hl=en-IN"

            // Create an Intent to open the URL in a browser
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(developerPageUrl)
            }

            // Start the activity to open the URL
            startActivity(intent)

        }


    }



    private fun startAutoScroll() {
        handler1.postDelayed(runnable, delayMillis)
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


    private val installStateUpdatedListener = InstallStateUpdatedListener {
        if (it.installStatus() == InstallStatus.DOWNLOADED) {

            Toast.makeText(this, "Download Completed", Toast.LENGTH_LONG).show()
            lifecycleScope.launch {
                delay(5.seconds)
                appUpdateManager.completeUpdate()
            }
        }
    }


    private fun checkForAppUpdate() {

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            val isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.IMMEDIATE -> appUpdateInfo.isImmediateUpdateAllowed
                AppUpdateType.FLEXIBLE -> appUpdateInfo.isFlexibleUpdateAllowed
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                performPreUpdateTasks {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, updateType, this, 113
                    )
                }
            }
        }
    }

    private fun performPreUpdateTasks(onComplete: () -> Unit) {
        // Directory to delete
        val downloadDirectory = File(this.getExternalFilesDir(null), "test")

        // Function to delete the directory and its contents
        fun deleteDirectory(directory: File) {
            if (directory.isDirectory) {
                val files = directory.listFiles()
                files?.forEach {
                    if (it.isDirectory) {
                        deleteDirectory(it)
                    } else {
                        it.delete()
                    }
                }
            }
            directory.delete()
        }

        // Perform directory deletion
        deleteDirectory(downloadDirectory)

        // Proceed with the update
        onComplete()
    }


    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) {
            adView1.resume()
        }
        if (::adView1.isInitialized) {
            adView1.resume()
        }



        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener {
                if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        it, updateType, this, 113
                    )

                }
            }
        }




        if (currentPlaybackPosition > 0) {
            mediaPlayer.seekTo(currentPlaybackPosition)
            mediaPlayer.start()
        }


    }


    override fun onDestroy() {
        handler1.removeCallbacks(runnable)

        if (::adView1.isInitialized) {
            adView1.destroy()
        }
        if (::adView.isInitialized) {
            adView.destroy()
        }

        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }

        textToSpeech!!.stop()
        textToSpeech!!.shutdown()
        stopAudio()




    }

    override fun onPause() {
        handler1.removeCallbacks(runnable)
        if (::adView.isInitialized) {
            adView.pause()
        }
        if (::adView1.isInitialized) {
            adView1.pause()
        }
        super.onPause()

        textToSpeech!!.stop()
        textToSpeech!!.shutdown()

        pauseAudio()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 113) {
            if (resultCode != RESULT_OK) {
                Log.d("speak", "reached here not ok")
            }
        }
    }


    private fun translateToHindi(currentMonth: String): String? {
        // Manually create a mapping for English to Hindi month names
        val monthTranslation: MutableMap<String, String> = HashMap()
        monthTranslation["JANUARY"] = "जनवरी"
        monthTranslation["FEBRUARY"] = "फ़रवरी"
        monthTranslation["MARCH"] = "मार्च"
        monthTranslation["APRIL"] = "अप्रैल"
        monthTranslation["MAY"] = "मई"
        monthTranslation["JUNE"] = "जून"
        monthTranslation["JULY"] = "जुलाई"
        monthTranslation["AUGUST"] = "अगस्त"
        monthTranslation["SEPTEMBER"] = "सितंबर"
        monthTranslation["OCTOBER"] = "अक्टूबर"
        monthTranslation["NOVEMBER"] = "नवंबर"
        monthTranslation["DECEMBER"] = "दिसंबर"
        // Return the translated month name
        return monthTranslation[currentMonth]
    }


    private fun translateToHindiday(currentDay: String): String? {
        // Manually create a mapping for English to Hindi month names
        val monthTranslation: MutableMap<String, String> = HashMap()
        monthTranslation["MONDAY"] = "सोमवार"
        monthTranslation["TUESDAY"] = "मंगलवार"
        monthTranslation["WEDNESDAY"] = "बुधवार"
        monthTranslation["THURSDAY"] = "गुरुवार"
        monthTranslation["FRIDAY"] = "शुक्रवार"
        monthTranslation["SATURDAY"] = "शनिवार"
        monthTranslation["SUNDAY"] = "रविवार"
        // Return the translated month name
        return monthTranslation[currentDay]
    }

    private fun translateToHindidate(date: String): String? {
        // Manually create a mapping for English to Hindi month names
        val nmap: MutableMap<String, String> = HashMap()
        nmap["1"] = "१"
        nmap["2"] = "२"
        nmap["3"] = "३"
        nmap["4"] = "४"
        nmap["5"] = "५"
        nmap["6"] = "६"
        nmap["7"] = "७"
        nmap["8"] = "८"
        nmap["9"] = "९"
        nmap["10"] = "१०"
        nmap["11"] = "११"
        nmap["12"] = "१२"
        nmap["13"] = "१३"
        nmap["14"] = "१४"
        nmap["15"] = "१५"
        nmap["16"] = "१६"
        nmap["17"] = "१७"
        nmap["18"] = "१८"
        nmap["19"] = "१९"
        nmap["20"] = "२०"
        nmap["21"] = "२१"
        nmap["22"] = "२२"
        nmap["23"] = "२३"
        nmap["24"] = "२४"
        nmap["25"] = "२५"
        nmap["26"] = "२६"
        nmap["27"] = "२७"
        nmap["28"] = "२८"
        nmap["29"] = "२९"
        nmap["30"] = "३०"
        nmap["31"] = "३१"
        // Return the translated month name
        return nmap[date]
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {

            // Set language

        } else {
            // Handle error
        }
    }


    private fun delayedTask(delayMillis: Int, speak: String) {
        handler.postDelayed({
            if (textToSpeech != null) {
                textToSpeech!!.setLanguage(Locale.forLanguageTag("hi"))
                Log.d("speak", "reached here tts $speak")
                textToSpeech!!.setPitch(1f)
                textToSpeech!!.setSpeechRate(0.6f)
                textToSpeech!!.speak(speak, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Log.e("TTS", "TextToSpeech not initialized")
            }
        }, delayMillis.toLong())
    }



    private fun delayedBroadcast(delayMillis: Int) {
        handler.postDelayed({ // Your code to be executed after the delay

            stopAudio()
            playAudio(homeBroadcast)

        }, delayMillis.toLong())
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


    }

    fun playAudio(audioURL: String) {


        try {
            // Set the data source for the MediaPlayer
            mediaPlayer.setDataSource(audioURL)

            // Prepare the MediaPlayer asynchronously
            mediaPlayer.prepareAsync()

            // Set a listener to handle when the MediaPlayer is prepared
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }

            mediaPlayer.setOnCompletionListener {
                Toast.makeText(this, "audio completed", Toast.LENGTH_SHORT).show()
                isFabClicked = !isFabClicked
            }

        } catch (e: Exception) {
            // Handle error (e.g., show a Toast message)
            e.printStackTrace()
        }


    }


    private fun downloadFile(storagePath: String, action: String, localFileName: String,progressCallback: (Int) -> Unit) {
        if (::fileDownloader.isInitialized) {
            fileDownloader.retrieveURL(storagePath, action, localFileName, { downloadedFile ->
                if (downloadedFile != null) {
                    // File downloaded successfully, do something with the file if needed
                    Log.d(TAG, "File downloaded successfully: $downloadedFile")

                    // Notify UI or perform tasks with downloaded file
                    handleDownloadedFile(downloadedFile)
                } else {
                    // Handle the case where download failed
                    Log.d(TAG, "Download failed for file: $localFileName")
                }
            }, progressCallback)
        } else {
            Log.e(TAG, "fileDownloader is not initialized.")
        }
    }

    private fun handleDownloadedFile(downloadedFile: File) {

        readFileContent()

    }


    private fun readFileContent() {
       // binding.HomeBoard.visibility=View.VISIBLE

        val dbHelper = dbHelper(applicationContext, "Gita.db")
        val av = dbHelper.getRowCount("Gita")
        if (av > 0) {
            val avt = dbHelper.getRowValues("Gita", Random.nextInt(av))
            if (avt != null) {

                G1 = avt[1].toString()
                G2 = avt[2].toString()
                Log.d("gita", "$G2")
                Log.d("gita", "$G2")

                if (G1 != null){
                    val formattedText  = G1
                    runOnUiThread {
                        Log.d("gita", "$G1")

                    }

                }else{

                    binding.homeBanner.visibility=View.GONE
                }

            }
        } else {
            // Handle case when file exists but is empty (if applicable)
            binding.homeBanner.visibility=View.GONE

        }
    }


    private fun recreateActivity() {
        val intent = intent // Get the current intent to pass extras if needed

        finish() // Finish the current activity
        startActivity(intent) // Start the activity again
    }


    private fun observeFileExistence(month:String) {
        fileExistenceLiveData = checkFileExistence("Gita.db")
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("SQLdb")
        val documentRef = collectionRef.document("Gita")
        fileExistenceLiveData.observe(this) { fileExists ->
            if (fileExists) {


                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "Gita.db"
                        lifecycleScope.launch {
                            val updates = updatesDao.getfileupdate(fileName)
                            if (updates.get(0).uniqueString == actions) {
                                readFileContent()
                               // binding.Gita.visibility=View.VISIBLE
                                Log.d("DownloadProgress", "reached here is $100% done")

                            } else {

                                val gitaUpdate = updatesDao.findById(4)
                                gitaUpdate.let {
                                    it.uniqueString = actions
                                    updatesDao.update(it)
                                }


                                val storagePath = "SQLdb/Gita"
                                downloadFile(
                                    storagePath,
                                    "delete",
                                    "Gita.db",
                                    progressCallback = { progress ->
                                        if(progress==100){
                                            //binding.Gita.visibility=View.VISIBLE

                                        }
                                        // Update your progress UI, e.g., a ProgressBar or TextView
                                        Log.d("DownloadProgress", "Gita  is $progress% done")

                                    })
                            }
                        }


                        // File exists, proceed with reading its content


                    } else {


                    }


                }

                // File does not exist, handle accordingly
            } else {

                val storagePath = "SQLdb/Gita"
                downloadFile(storagePath, "delete", "Gita.db", progressCallback = { progress ->
                    // Update your progress UI, e.g., a ProgressBar or TextView
                    if(progress==100){
                       // binding.Gita.visibility=View.VISIBLE

                    }
                    Log.d("DownloadProgress", " Gita Download is $progress% done")
                })
                documentRef.get().addOnSuccessListener {
                    if (it != null) {
                        val fileUrl = it.getString("test") ?: ""
                        val actions = it.getString("action") ?: "delete"
                        val fileName = "Gita.db"

                        lifecycleScope.launch {

                            val gita = Updates(id = 4 , fileName = "Gita.db", uniqueString = "Gita")
                            updatesDao.insert(gita)

                            val gitaUpdate = updatesDao.findById(4)
                            gitaUpdate.let {
                                it.uniqueString = actions
                                updatesDao.update(it)
                            }

                        }

                    }


                }

            }
        }

    }

    fun checkFileExistence(fileName: String): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath =
            this.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }







    fun setupAppBarBanner(todayimage: List<Map<String, String>>, viewModel: HomeViewModel
    ) {
        Log.d("holidayurl", "reached here ")
        // Set up the app bar banner
        lifecycleScope.launch {
            val appbarbannerurls = viewModel.getappbarImagelist("appbar")
            Log.d("holidayurl", "$appbarbannerurls")

            val randomIndex = if (appbarbannerurls.isNotEmpty()) {
                Random.nextInt(appbarbannerurls.size)
            } else {
                0
            }

            val appbarbannerurl = if (appbarbannerurls.isNotEmpty()) {
                appbarbannerurls[randomIndex]
            } else {
                "empty"
            }

            Log.d("appbar", "$appbarbannerurl")


                if (appbarbannerurl == "empty") {
                    Log.d("appbar", "empty")
                    if (todayimage.isNotEmpty()) {
                        val random = Random.nextInt(todayimage.size)
                        val randomImage = todayimage[random]
                        Log.d("appbar", "$randomImage")

// Adjust ImageView height
                        val maxHeightInDp = 700
                        val maxHeightInPx = (maxHeightInDp * binding.root.context.resources.displayMetrics.density).toInt()

                        val imageView = binding.homeBanner

                        val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val contentHeight = imageView.height
                                val params = imageView.layoutParams as ViewGroup.LayoutParams

                                // If the content height is greater than maxHeight, use maxHeight
                                params.height = if (contentHeight > maxHeightInPx) maxHeightInPx else ViewGroup.LayoutParams.WRAP_CONTENT
                                imageView.layoutParams = params

                                // Remove the listener to prevent it from being called again
                                imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }

                        // Add the global layout listener
                        imageView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)

                        Glide.with(binding.root.context).load(randomImage["imageurl"])
                            .into(imageView)
                        imageView.invalidate()

                    } else {
                        Log.d("appbar", "inelse")
                    }
                }


            else {
                Log.d("appbar", "notempty")
                if (appbarbannerurls.isNotEmpty()) {
                    val random = Random.nextInt(appbarbannerurls.size)


                    ///
                    val maxHeightInDp = 700
                    val maxHeightInPx = (maxHeightInDp * binding.root.context.resources.displayMetrics.density).toInt()

                    val imageView = binding.homeBanner

                    val layoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val contentHeight = imageView.height
                            val params = imageView.layoutParams as ViewGroup.LayoutParams

                            // If the content height is greater than maxHeight, use maxHeight
                            params.height = if (contentHeight > maxHeightInPx) maxHeightInPx else ViewGroup.LayoutParams.WRAP_CONTENT
                            imageView.layoutParams = params

                            // Remove the listener to prevent it from being called again
                            imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }

                    // Add the global layout listener
                    imageView.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
                    Log.d("appbar", "$appbarbannerurls")

                    Glide.with(binding.root.context).load(appbarbannerurls[random])
                        .into(imageView)
                    imageView.invalidate()

                }
            }

            val homeBroadcast = viewModel.gethomeBroadcast()

            if (homeBroadcast.isNullOrEmpty()) {
                binding.floatingActionButton.visibility = View.GONE
            } else {
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        }
    }





}

