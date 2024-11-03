package com.mithilakshar.mithilapanchang.UI.View


import android.content.Intent
import java.time.format.TextStyle
import android.media.AudioAttributes
import android.media.MediaPlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.view.View

import android.widget.Toast
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
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.ViewModel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mithilakshar.mithilapanchang.Adapters.SliderAdapter
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.CalendarHelper
import com.mithilakshar.mithilapanchang.Utility.LayoutBitmapGenerator
import com.mithilakshar.mithilapanchang.Utility.SupabaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils.speakFunction
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.Utility.dbSupabaseDownloadeSequence
import com.mithilakshar.mithilapanchang.databinding.ActivityHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors


class HomeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var binding: ActivityHomeBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    private lateinit var updatesDao: UpdatesDao

    private lateinit var dbHelpercalander: CalendarHelper
    private lateinit var dbHelperimage: dbHelper
    private lateinit var dbHelperHoliday: dbHelper

    val mediaPlayer = MediaPlayer()
    var currentPlaybackPosition: Int = 0
    val handler = Handler(Looper.getMainLooper())
    private var isFabClicked = false
    private var textToSpeech: TextToSpeech? = null
    var speak: String? = ""
    var homeBroadcast: String = ""

    val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }


    private lateinit var adView: AdView
    private lateinit var adviewMR: AdView
    private var delayMillis: Long = 4000

    private lateinit var dbSupabaseDownloadeSequence: dbSupabaseDownloadeSequence
    private lateinit var supabasedownloader: SupabaseFileDownloader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }


        checkForAppUpdate()

        val lottieView: LottieAnimationView =  binding.homeviewloading
        val animationList = listOf(
            R.raw.a1,
            R.raw.a2,
            R.raw.a3  ,
            R.raw.a4  ,
            R.raw.om  ,
            R.raw.solar  ,
        )
        val randomAnimation = animationList.random()
        lottieView.setAnimation(randomAnimation) // Reference to new animation in raw folder
        lottieView.playAnimation()

        val networkdialog = Networkdialog(this)
        val networkManager = NetworkManager(this)

        fun mergeRowsByDate(rows: List<Map<String, Any?>>): List<Map<String, Any?>> {
            val mergedMap = mutableMapOf<Any?, MutableMap<String, Any?>>()

            for (row in rows) {
                val dateKey = row["date"] // Use "date" as the unique key for merging
                if (!mergedMap.containsKey(dateKey)) {
                    mergedMap[dateKey] = mutableMapOf()
                }
                val existingRow = mergedMap[dateKey]!!

                for ((k, v) in row) {
                    if (existingRow.containsKey(k)) {
                        // If the value is different, merge them into a list
                        if (existingRow[k] != v) {
                            existingRow[k] = if (existingRow[k] is List<*>) {
                                (existingRow[k] as List<Any?>) + v
                            } else {
                                listOf(existingRow[k], v)
                            }
                        }
                    } else {
                        existingRow[k] = v
                    }
                }
            }

            return mergedMap.values.map { it.toMap() }
        }


        fun logMergedRows(mergedRows: List<Map<String, Any?>>) {
            mergedRows.forEachIndexed { index, row ->
                val logMessage = StringBuilder("Row ${index + 1}: ")
                row.entries.forEach { entry ->
                    logMessage.append("${entry.key}=${entry.value}, ")
                }
                // Remove the last comma and space
                if (logMessage.isNotEmpty()) {
                    logMessage.setLength(logMessage.length - 2)
                }
                // Use Log.d to log the message
                Log.d("MergedRow", logMessage.toString())
            }
        }

        fun performNetworkTasks() {

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

            adviewMR.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Make the AdView visible when the ad is loaded
                    adviewMR.visibility = View.VISIBLE
                }
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    // Optionally, you can log or handle the error here
                }
            }
            adviewMR.loadAd(adRequest)
            adView.loadAd(adRequest)

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


            supabasedownloader = SupabaseFileDownloader(this)
            updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()
            dbSupabaseDownloadeSequence = dbSupabaseDownloadeSequence(updatesDao, supabasedownloader)

            val filesWithIds = listOf(
                Pair("iauto",85),
                Pair("cal2024", 78),
                Pair("holi2024", 79),
                Pair("holi2025", 80),
                Pair("cal2025", 89),

                )

            lifecycleScope.launch {
                val updateChecker = UpdateChecker(updatesDao)
                val isUpdateNeeded = updateChecker.getUpdateStatus()

                Log.d("supabase", "isUpdateNeeded: $isUpdateNeeded")
                withContext(Dispatchers.Main) {
                    if (isUpdateNeeded != "a") {

                        Log.d("updatechecker", " :  needed $isUpdateNeeded")

                        dbSupabaseDownloadeSequence.observeMultipleFileExistence(
                            filesWithIds,
                            this@HomeActivity,
                            lifecycleScope,
                            homeActivity = this@HomeActivity, // Your activity
                            progressCallback = { progress, filePair ->


                                Log.d("Progress", "File: $filePair, Progress: $progress%")


                            }, {


                                recreateWithDelay(2000)



                            }
                        )


                    } else {

                        val nonExistentFiles= checkFilesExistence(filesWithIds)
                        dbSupabaseDownloadeSequence.observeMultipleFileExistence(
                            nonExistentFiles,
                            this@HomeActivity,
                            lifecycleScope,
                            homeActivity = this@HomeActivity,
                            progressCallback = { progress, filePair ->
                                Log.d(
                                    "FileCheck",
                                    "File: ${filePair.first()}.db, Progress: $progress%"
                                )
                            },
                            {

                                binding.homeviewloading.visibility = View.GONE
                                binding.homeviewloading1.visibility = View.GONE
                                binding.homeview.visibility = View.VISIBLE
                                binding.homeBanner.visibility = View.VISIBLE
                                val year=getCurrentYear()



                                dbHelperHoliday = dbHelper(this@HomeActivity, "holi$year.db")

                                dbHelperimage = dbHelper(this@HomeActivity, "iauto.db")

                                val dbname="cal$year.db"
                                var table="cal"
                                table="$table$year"
                                val month=getCurrentMonth()
                                val dATE=getCurrentDay()

                                dbHelpercalander  = CalendarHelper(this@HomeActivity, dbname)
                                val rows = dbHelpercalander.getAllTableDataForMonth(table,TranslationUtils.translateTomonthnumber(month.toString()).toString())
                                val mergedRows = mergeRowsByDate(rows)


                               Log.d("month", "month: ${TranslationUtils.translateTomonthnumber(
                                    month.toString())}")
                                Log.d("month", "dATE: $dATE")
                                val filtereddata = mergedRows.filter { it["date"] == dATE.toString() } //today item
                                logMergedRows(filtereddata)

                                if (filtereddata.isNotEmpty() && filtereddata[0] != null) {
                                    val toaydata = filtereddata[0]

                                    val sentence = speakFunction(
                                        month = toaydata?.get("month")?.toString() ?: "Unknown month",
                                        date = toaydata?.get("date")?.toString() ?: "Unknown date",
                                        day = toaydata?.get("day")?.toString() ?: "Unknown day",
                                        year = year.toString(),  // Assuming this is dynamically provided as a String
                                        tithi = toaydata?.get("tithi")?.toString() ?: "Unknown tithi",
                                        tithiEndH = toaydata?.get("tithiendh")?.toString() ?: "Unknown",
                                        tithiEndM = toaydata?.get("tithiendm")?.toString() ?: "Unknown",
                                        nakshatra = toaydata?.get("nakshatra")?.toString() ?: "Unknown nakshatra",
                                        nakshatraEndH = toaydata?.get("nakshatraendh")?.toString() ?: "Unknown",
                                        nakshatraEndM = toaydata?.get("nakshatraendm")?.toString() ?: "Unknown",
                                        monthName = toaydata?.get("monthname")?.toString() ?: "Unknown month name",
                                        rashi = toaydata?.get("rashi")?.toString() ?: "Unknown rashi",
                                        paksha = toaydata?.get("paksha")?.toString() ?: "Unknown paksha"
                                    )

                                    loadTodaysDetails(this@HomeActivity, toaydata)

                                    Log.d("toaydata", "$toaydata")
                                    Log.d("sentence", "$sentence")

                                    speak = sentence
                                    textToSpeech = TextToSpeech(this@HomeActivity) { status ->
                                        if (status == TextToSpeech.SUCCESS) {
                                            textToSpeech?.language = Locale.forLanguageTag("hi")
                                            Log.d("speak", "TTS initialized successfully")
                                            delayedTask(1000, speak.toString())
                                        } else {
                                            Log.d("speak", "TTS initialization failed")
                                        }
                                    }
                                }


                              handleHolidayData(
                                    dbHelpercalander = dbHelpercalander,
                                    dbHelperimage = dbHelperimage,
                                    currentMonthString = currentMonthString,
                                    currentDay = currentDay,
                                    currentDayName = currentDayName
                                )

                                setupViewPagerAndDatabase(
                                    context = this@HomeActivity,
                                    currentMonthString = currentMonthString,
                                    currentDay = currentDay,
                                    delayMillis = delayMillis,
                                    dbHelperHoliday
                                )

                            }
                        )









                   /*     homeBroadcast = viewModel.gethomeBroadcast()
                        Log.d("homeBroadcast", "$homeBroadcast")

                        if (homeBroadcast.isNullOrEmpty()) {
                            binding.floatingActionButton.visibility = View.GONE
                        } else {
                            binding.floatingActionButton.visibility = View.VISIBLE
                        }*/


                    }
                }
            }


            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA) // Set usage type (e.g., music, alarm)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // Set content type
                .build()

            mediaPlayer.setAudioAttributes(audioAttributes)

            //date

            val currentDate = LocalDate.now()

            val hindiMonth = TranslationUtils.translateToHindi(currentDate.month.toString())
            val hindiDay =TranslationUtils. translateToHindiday(currentDate.dayOfWeek.toString())
            val hindidate =TranslationUtils. translateToHindidate(currentDate.dayOfMonth.toString())


            //text speak broadcast
            binding.floatingActionButton.setOnClickListener {
                isFabClicked = !isFabClicked
                if (isFabClicked) {


                    switchFabColor(binding.floatingActionButton)

                    delayedBroadcast(1000,binding.floatingActionButton)


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

            binding.test.setOnClickListener {
                val i = Intent(this, TestActivity::class.java)

                startActivity(i)
                stopAudio()
            }

            binding.cal.setOnClickListener {
                val i = Intent(this, CalActivity::class.java)

                startActivity(i)
                stopAudio()
            }

            binding.caldetail.setOnClickListener {
                val i = Intent(this, CalDetailActivity::class.java)

                startActivity(i)
                stopAudio()
            }


            binding.holiday.setOnClickListener {
                val i = Intent(this, HolidayActivity::class.java)

                startActivity(i)
                stopAudio()
            }

            binding.shareicon.setOnClickListener {
                ViewShareUtil.shareViewAsImageDirectly(binding.homeBanner,this)

            }

            binding.share.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    // Put the text to share in the intent

                    val shareText = "पंचांग ऐप डाउनलोड करने के लिए नीचे दिए गए लिंक पर क्लिक करें .\n https://play.google.com/store/apps/details?id=com.mithilakshar.mithilapanchang  \n\n\n @mithilakshar13"

                    putExtra(Intent.EXTRA_TEXT, shareText)
                    // Set the MIME type
                    type = "text/plain"
                }
                // Start the activity with the intent
                startActivity(Intent.createChooser(intent, "शेयर: "))
            }

        }

        fun onNetworkAvailable() {
            // Perform your tasks when network is available
            // Example: Fetch data from the API or sync with the server
            performNetworkTasks()
        }



        networkManager.observe(this, {
            if (!it) {
                if (!networkdialog.isShowing) {
                    networkdialog.show()
                }


            } else {
                if (networkdialog.isShowing) {
                    networkdialog.dismiss()
                }
                onNetworkAvailable()
            }
        })



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
            if (directory.exists()) {
                directory.listFiles()?.forEach {
                    if (it.isDirectory) {
                        deleteDirectory(it)
                    } else {
                        if (!it.delete()) {
                            Log.e("DeleteFile", "Failed to delete file: ${it.absolutePath}")
                        }
                    }
                }
                if (!directory.delete()) {
                    Log.e("DeleteDir", "Failed to delete directory: ${directory.absolutePath}")
                }
            }
        }

        // Perform directory deletion asynchronously
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                deleteDirectory(downloadDirectory)
            } catch (e: Exception) {
                Log.e("DeleteDir", "Error during directory deletion", e)
            } finally {
                // Proceed with the update
                onComplete()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) {
            adviewMR.resume()
        }
        if (::adviewMR.isInitialized) {
            adviewMR.resume()
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
        // Check if adviewMR is initialized
        if (::adviewMR.isInitialized) {
            adviewMR.destroy()
        }

        // Check if adView is initialized
        if (::adView.isInitialized) {
            adView.destroy()
        }

        // Call super first
        super.onDestroy()

        // Unregister the update listener if needed
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }

        // Safely handle textToSpeech
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }

        // Call stopAudio safely
        stopAudio()
    }


    override fun onPause() {
        if (::adView.isInitialized) {
            adView.pause()
        }
        if (::adviewMR.isInitialized) {
            adviewMR.pause()
        }
        super.onPause()

        // Null check for textToSpeech
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }

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



    private fun delayedBroadcast(delayMillis: Int,fab: FloatingActionButton) {
        handler.postDelayed({ // Your code to be executed after the delay

            stopAudio()
            playAudio(homeBroadcast,fab)
            Log.d("delayedBroadcast", "reached here delayedBroadcast")
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

    fun playAudio(audioURL: String,fab:FloatingActionButton) {

        Log.d("delayedBroadcast", "reached here playAudio $audioURL")
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
                switchFabColor(fab)
            }

        } catch (e: Exception) {
            // Handle error (e.g., show a Toast message)
            e.printStackTrace()
        }


    }

















    fun setupViewPagerAndDatabase(
        context: Context,
        currentMonthString: String,
        currentDay: Int,
        delayMillis: Long,
        dbHelperHoliday:dbHelper
    ) {

        val year=getCurrentYear()
        // Check if column exists
        val doesColumnExist = dbHelperHoliday.doesColumnExist("holi$year", "datenumber")
        Log.d("doesColumnExist", "$doesColumnExist")

        // Prepare holidayMonthList
        val holidayMonthList: List<Map<String, String>>
        // Fetch holidays based on column existence
        if (doesColumnExist) {
            Log.d("HolidayLog", "Checking if the column exists: $doesColumnExist")

            // Fetching holidays by month and date
            Log.d("HolidayLog", "Fetching holidays for date - Month: ${currentMonthString.lowercase(Locale.getDefault())}, Day: $currentDay, Year: $year")

             holidayMonthList= dbHelperHoliday.getHolidaysByMonthdate(
                    currentMonthString.lowercase(Locale.getDefault()),
                    currentDay.toString(), "holi$year"
                )


            Log.d("HolidayLog", "Holidays fetched and added to holidayMonthList.")
            Log.d("HolidayLog", "dateExists: $doesColumnExist")
            Log.d("HolidayLog", "Current holidayMonthList: $holidayMonthList")
        } else {
            Log.d("HolidayLog", "Column does not exist, proceeding to fetch holidays by month.")

            // Fetching holidays by month only
            Log.d("HolidayLog", "Fetching holidays for month: ${currentMonthString.lowercase(Locale.getDefault())}, Year: $year")

            holidayMonthList =
                dbHelperHoliday.getHolidaysByMonth(
                    currentMonthString.lowercase(Locale.getDefault()), "holi$year"
                )


            Log.d("HolidayLog", "Holidays fetched and added to holidayMonthList.")
            Log.d("HolidayLog", "notExist: $doesColumnExist")
        }

// Log the final holiday list after processing
        Log.d("HolidayLog", "Final holidayMonthList after processing: $holidayMonthList")



        // Set up ViewPager and adapter
        val handler = Handler(Looper.getMainLooper())
        val sliderAdapter = SliderAdapter(holidayMonthList)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager) // Replace with actual ViewPager2 ID
        binding.viewPager.visibility=View.VISIBLE
        viewPager.adapter = sliderAdapter


        // Runnable for ViewPager auto-scrolling
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % sliderAdapter.itemCount
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delayMillis)
            }
        }

        // Start auto-scrolling
        handler.postDelayed(runnable, delayMillis)
    }




    fun handleHolidayData(dbHelpercalander: CalendarHelper, dbHelperimage: dbHelper, currentMonthString: String, currentDay: Int, currentDayName: String) {
        val rowsFormonthdate = getRowByMonthAndDate(dbHelpercalander, currentMonthString, currentDay.toString())
        Log.d("todayimage", "speak $speak")

        val holidaytoday = rowsFormonthdate?.get("holiday")
        val holidaydesc = rowsFormonthdate?.get("holidaydesc")
        Log.d("todayimage", "imge $holidaytoday")
        Log.d("todayimage", "desc$holidaydesc")

        val holidayurl = dbHelperimage.getimageByholidayname(holidaytoday.toString())
        Log.d("holidayurl", "HH $holidayurl")

        if (holidaytoday.isNullOrEmpty()) {

            handleHolidayWithoutImage(dbHelperimage, currentDayName)
            Log.d("holidayurl", "today empty")

        } else {

            handleHolidayWithImage(holidayurl, holidaytoday, holidaydesc)
            Log.d("holidayurl", "today notempty")
        }
    }

    fun getRowByMonthAndDate(dbHelpercalander: CalendarHelper, currentMonthString: String, currentDay: String): Map<String, String>? {
        return dbHelpercalander.getRowByMonthAndDate(currentMonthString, currentDay, "cal${getCurrentYear()}")
    }

    fun handleHolidayWithImage(holidayurl: List<Map<String, String>>, holidaytoday: String?, holidaydesc: String?) {
        Log.d("holidayurl", "holidayurl.isNotEmpty()")
        val randomEntry = holidayurl[Random.nextInt(holidayurl.size)]
        val randomImageUrl = randomEntry["imageurl"]
        Log.d("holidayurl", "$randomImageUrl")

        if (randomImageUrl != null) {
            val holidaybannerurl = randomImageUrl
            val holidayNameData = holidaytoday.toString()
            val holidayGreetingData = holidaydesc.toString()

            val layoutBitmapGenerator = LayoutBitmapGenerator(this)
            layoutBitmapGenerator.generateBitmap(holidayNameData, holidayGreetingData, holidaybannerurl) { generatedBitmap ->
                if (generatedBitmap != null) {
                    Glide.with(this)
                        .load(generatedBitmap)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.homeBanner)
                } else {
                    Log.e("MyActivity", "Failed to generate bitmap from layout")
                }
            }

            val todayimage = dbHelperimage.getimageByholidayname(holidaytoday.toString())
            handler.postDelayed({
                setupAppBarBanner(todayimage)
            }, 20000)
        }
    }

    fun handleHolidayWithoutImage(dbHelperimage: dbHelper, currentDayName: String) {
        Log.d("holidayurl", "holidayurl.empty()")
        val todayimage = dbHelperimage.getimageByDayName(currentDayName)
        Log.d("holidayurl", "$currentDayName")
        Log.d("holidayurl", "$todayimage")

        setupAppBarBanner(todayimage)
    }



    fun setupAppBarBanner(todayimage: List<Map<String, String>>) {
        Log.d("holidayurl", "reached here ")
        // Set up the app bar banner
        lifecycleScope.launch {
            val appbarbannerurls = viewModel.getappbarImagelist("appbar")
            Log.d("holidayurl", " app bar url $appbarbannerurls")

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
                    Log.d("appbar", "rN$randomImage")

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


        }
    }

    private fun recreateWithDelay(delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = intent // Get the current activity's intent
            finish() // Optional: finish the current activity
            startActivity(intent) // Restart the same activity
        }, delayMillis)
    }

    private fun loadTodaysDetails(context: Context, toaydata: Map<String, Any?>) {
        lifecycleScope.launch {

            // Determine the database name and table name based on the year

            val todaytithi: TextView = binding.todaytithi
            val todaynakshatra: TextView = binding.todaynakshatra
            val todaymonth: TextView = binding.todaymonth
            val todaysunrise: TextView = binding.todaysunrise
            val todaysunset: TextView = binding.todaysunset
            val tithiendtime: TextView = binding.tithiendtime
            val nakshatraendtime: TextView = binding.nakshatraendtime
            val todayrashi: TextView = binding.todayrashi
            val todaypaksha: TextView = binding.todaypaksha
            val todayyog: TextView = binding.todayyog



            val todaysdatedetails = toaydata
            Log.d("todaysdatedetails", " :todays data $todaysdatedetails")

            runOnUiThread {
                val tithiData = todaysdatedetails["tithi"]
                val parsedTithi = TranslationUtils.parseTithiInput(tithiData.toString())
                val TithiNames = parsedTithi?.joinToString(", ") ?: "~"

                val nakshatraData = todaysdatedetails["nakshatra"]
                val nakshatraTithi = TranslationUtils.parseNakshatraInput(nakshatraData.toString())
                val nakshatraNames = nakshatraTithi?.joinToString(", ") ?: "~"



                todaytithi.text = TithiNames
                todaynakshatra.text = nakshatraNames
                todaymonth.text = TranslationUtils. translateToHindiDevanagariHinduMonth (todaysdatedetails?.get("monthname").toString())

                val formattedSunrise = "${TranslationUtils.formatTimeD(todaysdatedetails?.get("sunrise").toString().toInt() , todaysdatedetails?.get("sunrisemin").toString().toDouble())}"
                val formattedSunset = "${TranslationUtils.formatTimeD(todaysdatedetails?.get("sunset").toString().toInt() , todaysdatedetails?.get("sunsetmin").toString().toDouble())}"
                // Setting sunrise and sunset times
                todaysunrise.text =     formattedSunrise
                todaysunset.text = formattedSunset


                // Setting Tithi and Nakshatra end times
                val tithiendh= todaysdatedetails?.get("tithiendh")
                val tithiendm= todaysdatedetails?.get("tithiendm")



                Log.d("tithiendh", " :tithiendh  $tithiendh")
                Log.d("tithiendh", " :tithiendh  $tithiendm")
                if (tithiendh != null && tithiendm != null) {

                    val formattedOutput = TranslationUtils.createTithitimeformat(tithiendh.toString(),
                        tithiendm.toString()
                    )
                    tithiendtime .text =formattedOutput
                }

                val nakshatraendh= todaysdatedetails?.get("nakshatraendh")
                val nakshatraendm= todaysdatedetails?.get("nakshatraendm")

                if (nakshatraendh != null && nakshatraendm != null) {
                    val formattedOutput = TranslationUtils.createTithitimeformat(nakshatraendh.toString(),
                        nakshatraendm.toString()
                    )
                    nakshatraendtime .text =formattedOutput

                }






                // Setting additional details
                todayrashi.text = TranslationUtils.translateToHindiDevanagariRashi(todaysdatedetails?.get("rashi").toString())
                todaypaksha.text = TranslationUtils.translateToPaksha(  todaysdatedetails?.get("paksha").toString()    )
                todayyog.text =  TranslationUtils.translateNumberToYoga(todaysdatedetails?.get("yog").toString().toInt())


            }


        }
    }


    fun getCurrentYear(): Int {
        val calendar = java.util.Calendar.getInstance()
        return calendar.get(java.util.Calendar.YEAR)
    }
    fun getCurrentMonth(): Int {
        val calendar = java.util.Calendar.getInstance()
        // Calendar.MONTH is zero-based, so we add 1
        return calendar.get(java.util.Calendar.MONTH) + 1
    }

    fun getCurrentDay(): Int {
        val calendar = java.util.Calendar.getInstance()
        return calendar.get(java.util.Calendar.DAY_OF_MONTH)
    }

    suspend fun checkFilesExistence(filesWithIds: List<Pair<String, Int>>): List<Pair<String, Int>> {
        val nonExistentFiles = mutableListOf<Pair<String, Int>>() // List to hold non-existent files
        val dbFolderPath = this.getExternalFilesDir(null)?.absolutePath + File.separator + "test" // Directory path
        val dbFolder = File(dbFolderPath)

        if (!dbFolder.exists()) {
            dbFolder.mkdirs() // Create the directory if it doesn't exist
        }

        // Create a coroutine scope for concurrent checks
        coroutineScope {
            val jobs = filesWithIds.map { filePair ->
                launch(Dispatchers.IO) {
                    try {
                        val dbFile = File(dbFolder, "${filePair.first}.db") // Construct the file path
                        // Check if the file exists
                        if (!dbFile.exists()) {
                            synchronized(nonExistentFiles) { // Ensure thread safety while modifying the list
                                nonExistentFiles.add(filePair) // Add to the list of non-existent files
                            }
                            Log.d("supabase", "File does not exist: ${filePair.first}.db, ID: ${filePair.second}")
                        } else {
                            Log.d("supabase", "File exists: ${filePair.first}.db, ID: ${filePair.second}")
                        }
                    } catch (e: Exception) {
                        Log.e("supabase", "Error checking file existence: ${filePair.first}.db, ID: ${filePair.second}", e)
                    }
                }
            }
            // Await all jobs to finish
            jobs.joinAll()
        }

        Log.d("supabase", "Non-existent files: $nonExistentFiles")
        return nonExistentFiles // Return the list of non-existent files
    }

}
