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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.OvershootInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mithilakshar.mithilapanchang.Adapters.SliderAdapter
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.CalendarHelper
import com.mithilakshar.mithilapanchang.Utility.DocList

import com.mithilakshar.mithilapanchang.Utility.InterstitialAdManager
import com.mithilakshar.mithilapanchang.Utility.SupabaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils.speakFunction
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.Utility.dbSupabaseDownloadeSequence
import com.mithilakshar.mithilapanchang.databinding.ActivityHomeBinding
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executors


class HomeActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    lateinit var binding: ActivityHomeBinding
    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.IMMEDIATE
    private lateinit var updatesDao: UpdatesDao

    private lateinit var dbHelpercalander: CalendarHelper
    private lateinit var dbHelpercalander1: dbHelper
    private lateinit var dbHelperimage: dbHelper
    private lateinit var dbHelperHoliday: dbHelper
    val docList = DocList()
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
    private lateinit var interstitialAdManager: InterstitialAdManager
    private lateinit var interstitialAdManagercal: InterstitialAdManager

    private var delayMillis: Long = 4000

    private lateinit var dbSupabaseDownloadeSequence: dbSupabaseDownloadeSequence
    private lateinit var supabasedownloader: SupabaseFileDownloader

    private var firebasedoclist: List<Map<String, Any>> = emptyList()

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
            R.raw.a1 ,
            R.raw.a2  ,
            R.raw.k3  ,
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

        interstitialAdManager = InterstitialAdManager(this, R.string.interstitialholiday.toString())
        interstitialAdManagercal = InterstitialAdManager(this,this.getString(R.string.interstitialcal))
        interstitialAdManager.loadAd {  }
        interstitialAdManagercal.loadAd {  }


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




            val cDate: LocalDate = LocalDate.now()
            val currentMonthString: String = cDate.month.name // Gets the current month in uppercase (e.g., "JANUARY")
            val currentDay: Int = cDate.dayOfMonth
            val currentDayName: String = cDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()


            supabasedownloader = SupabaseFileDownloader(this)
            updatesDao = UpdatesDatabase.getDatabase(applicationContext).UpdatesDao()
            dbSupabaseDownloadeSequence = dbSupabaseDownloadeSequence(updatesDao, supabasedownloader)


            docList.getHomeDocuments(
                callback = { docs ->
                    firebasedoclist = docs // ✅ Store in global variable
                        Log.d("Firestore", "Fetched documents: $firebasedoclist")

                    // Optional: log each document individually
                    firebasedoclist.forEach {
                        Log.d("Firestore", it.toString())
                    }

                    val filesWithIds = listOf(
                        "iauto" to 85,
                        "holi2025" to 80,
                        "t2025" to 65,
                        "t2026" to 66,
                        "n2025" to 67,
                        "k2025" to 68,
                        "y2025" to 69,
                        "n2026" to 70,
                        "k2026" to 71,

                        "y2026" to 72,
                        "caln2025" to 34
                    )

                    val fileIdMap = filesWithIds.toMap()



                    // Holds full info
                    data class FileInfo(val name: String, val id: Int, val action: String?)

// Start with known files
                    val predefinedFileInfoList = filesWithIds.map { (name, id) ->
                        val action = firebasedoclist.find {
                            val fname = it["test"] as? String
                            fname?.removeSuffix(".db") == name
                        }?.get("action") as? String
                        FileInfo(name, id, action)
                    }

// Now add any *new* files from Firestore
                    val extraFileInfoList = firebasedoclist.mapNotNull { doc ->
                        val fileName = doc["test"] as? String ?: return@mapNotNull null
                        val name = fileName.removeSuffix(".db")
                        if (fileIdMap.containsKey(name)) return@mapNotNull null // already included

                        val action = doc["action"] as? String

                        // Extract digits from filename, fallback to 0
                        val idFromName = Regex("\\d+").find(name)?.value?.toIntOrNull() ?: 0

                        FileInfo(name, idFromName, action)
                    }

// Combine all
                        val fullList = predefinedFileInfoList + extraFileInfoList

// ✅ Log the result
                    fullList.forEach {
                        Log.d("newlist", "Name: ${it.name}, ID: ${it.id}, Action: ${it.action}")
                    }

                    val backToPairs: List<Pair<String, Int>> = fullList.map { it.name to it.id }
                    backToPairs.forEach { (name, id) ->
                        Log.d("MyTag", "Name: $name, ID: $id")
                    }

                    lifecycleScope.launch {
                        val updateChecker = UpdateChecker(updatesDao)
                        val isUpdateNeeded = updateChecker.getUpdateStatus()

                        Log.d("supabase", "isUpdateNeeded: $isUpdateNeeded")
                        withContext(Dispatchers.Main) {
                            if (isUpdateNeeded != "a") {

                                Log.d("updatechecker", " :  needed $isUpdateNeeded")

                                dbSupabaseDownloadeSequence.observeMultipleFileExistence(
                                    backToPairs,
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

                                val nonExistentFiles= checkFilesExistence(backToPairs)
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

                                        CoroutineScope(Dispatchers.IO).launch {
                                            OneSignal.Notifications.requestPermission(true)
                                        }


                                        dbHelperHoliday = dbHelper(this@HomeActivity, "holi$year.db")

                                        dbHelperimage = dbHelper(this@HomeActivity, "iauto.db")






                                        val randomHoliday1 =  dbHelperHoliday .getRandomHoliday( currentMonthString.lowercase(Locale.getDefault()),
                                            currentDay.toString(), "holi$year")


                                        binding.titleTextView.text=randomHoliday1?.get("name")
                                        binding.subtitleTextView.text="${randomHoliday1?.get("date")}  ${randomHoliday1?.get("desc")}  "

                                        Log.d("randomHoliday", "randomHoliday1: $randomHoliday1 ")





                                        val dbname1="caln$year.db"
                                        var table1="caln"
                                        table1="$table1$year"
                                        val month=getCurrentMonth()
                                        val dATE=getCurrentDay()




                                        dbHelpercalander1  = dbHelper(this@HomeActivity, dbname1)
                                        dbHelpercalander = CalendarHelper(this@HomeActivity, dbname1)

                                        val todayrow=dbHelpercalander1.getRowByMonthAndDate("${TranslationUtils.translateTomonthnumber(
                                            month.toString())}",
                                            dATE.toString(),
                                            table1)

                                        Log.d("todayrow", "table: $table1")
                                        Log.d("todayrow", "month: ${TranslationUtils.translateTomonthnumber(
                                            month.toString())}")
                                        Log.d("todayrow", "date: $dATE")
                                        Log.d("todayrow", "dbname1: $dbname1")
                                        Log.d("todayrow", "todayrow: $todayrow")



                                        val selectedyear=getCurrentYear()


                                        val tithidbname="t$selectedyear.db"
                                        val ttable="t$selectedyear"

                                        val dbHelpertithi=dbHelper(this@HomeActivity,tithidbname)


                                        val (todayDate, todayMonth) = getTodayDateAndMonth()
                                        val todaystithi=dbHelpertithi.getTithiRowsContainingDate(todayDate,todayMonth,tithidbname,ttable)



                                        Log.d("todaystithi", " todaystithi: ${todaystithi}")
                                        val formattedTextt = todaystithi.joinToString(separator = " । ") { row ->
                                            val hindiTithi = row["Hindi Tithi"] ?: "N/A"
                                            var hindiTiming = row["Hindi Timinig"] ?: ""

                                            // Fix: Move पूर्वाह्न/अपराह्न before the time
                                            hindiTiming = hindiTiming.replace(Regex("(\\d{2}:\\d{2})\\s*(पूर्वाह्न|अपराह्न)")) {
                                                "${it.groupValues[2]} ${it.groupValues[1]}"
                                            }

                                            "$hindiTithi - $hindiTiming"
                                        }


                                        Log.d("todaystithi", "Received todaystithi: ${formattedTextt}")



                                        if (todayrow?.isNotEmpty() == true && todayrow != null) {
                                            val toaydata = todayrow

                                            val sentence = speakFunction(
                                                formattedTextt,
                                                month = (toaydata?.get("month")?: ""),
                                                date = toaydata?.get("date")?.toString() ?: "",
                                                day = toaydata?.get("day")?.toString() ?: "",
                                                monthName = toaydata?.get("monthname")?.toString() ?: "",
                                                rashi = toaydata?.get("rashi")?.toString() ?: " ",
                                                paksha = toaydata?.get("paksha")?.toString() ?: " ",
                                                hindiDate = toaydata?.get("hindidate")?.toString() ?: "  ",
                                                holidayName = toaydata?.get("holiday name")?.toString() ?: " ",
                                                holidayDesc = toaydata?.get("holidaydesc")?.toString() ?: "  ",

                                                )

                                            loadTodaysDetails( toaydata)

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



                                        binding.shareicon.visibility=View.VISIBLE
                                        binding.homeBanner.visibility=View.VISIBLE

                                        handleHolidayData(
                                            dbHelpercalander = dbHelpercalander,
                                            dbHelperimage = dbHelperimage,
                                            currentMonthString = currentMonthString,
                                            currentDay = currentDay,
                                            currentDayName = currentDayName
                                        )
                                        binding.holidaycounter.visibility=View.VISIBLE
                                        randomHoliday1?.get("date")?.let { startCountdown(it) }



                                        binding.bannercard.animate().translationZ(16f).setDuration(200).start()

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



                },
                onError = { e ->
                    Log.e("Firestore", "Error: ${e.message}")
                }
            )









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
              /*  interstitialAdManager.showAd(this){
                    startActivity(i)
                }*/
                stopAudio()
            }

            binding.caldetail.setOnClickListener {
                val i = Intent(this, CalDetailActivity::class.java)
                startActivity(i)
                interstitialAdManagercal.showAd(this){
                    startActivity(i)
                }

                stopAudio()
            }


            binding.holiday.setOnClickListener {
                val i = Intent(this, HolidayActivity::class.java)

                startActivity(i)
                stopAudio()
            }


            binding.tithi.setOnClickListener {
                val i = Intent(this, TithiActivity::class.java)

                startActivity(i)
                stopAudio()
            }
            binding.nakshatra.setOnClickListener {
                val i = Intent(this, NakshatraActivity::class.java)

                startActivity(i)
                stopAudio()
            }
            binding.yog.setOnClickListener {
                val i = Intent(this, YogActivity::class.java)

                startActivity(i)
                stopAudio()
            }
            binding.karna.setOnClickListener {
                val i = Intent(this, KarnaActivity::class.java)

                startActivity(i)
                stopAudio()
            }

            binding.shareicon.setOnClickListener {
                ViewShareUtil.shareViewAsImageDirectly(binding.homeBanner,this)

            }

            binding.shareapp.setOnClickListener {
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
            Log.d("HolidayLog", "Fetching holidays for date - Month: ${currentMonthString.lowercase(Locale.getDefault())}, Day: ${currentDay-1}, Year: $year")

             holidayMonthList= dbHelperHoliday.getHolidaysByMonthdate(
                    currentMonthString.lowercase(Locale.getDefault()),
                 (currentDay-1).toString(), "holi$year"
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
        Log.d("todayimage", "speak $rowsFormonthdate")

        val holidayurl = rowsFormonthdate?.get("holidayimage")
        val holidaytoday = rowsFormonthdate?.get("holidayname")
        Log.d("todayimage", "imge $holidayurl")
        Log.d("todayimage", "desc$holidaytoday")

        if (holidayurl.isNullOrBlank() || holidayurl.equals("null", ignoreCase = true)) {
            handleHolidayWithoutImage(dbHelperimage, currentDayName)
            Log.d("holidayurl", "today is effectively empty $holidayurl")
        } else {
            if (!holidayurl.isNullOrBlank()) {
                handleHolidayWithImage(holidayurl, holidaytoday)
            }
            Log.d("holidayurl", "today is not empty $holidayurl")
        }



    }

    fun getRowByMonthAndDate(dbHelpercalander: CalendarHelper, currentMonthString: String, currentDay: String): Map<String, String>? {
        return dbHelpercalander.getRowByMonthAndDate(currentMonthString, currentDay, "caln${getCurrentYear()}")
    }

    fun handleHolidayWithImage(holidayurl:String, holidaytoday: String?) {
        Log.d("holidayurl", "holidayurl.isNotEmpty()")
        val holidayurl = holidayurl

        if (holidayurl != null) {
            val holidaybannerurl = holidayurl
            val holidayNameData = holidaytoday.toString()

            val imageView = binding.homeBanner

            val fixedHeightDp = 530
            val fixedHeightPx = (fixedHeightDp * binding.root.context.resources.displayMetrics.density).toInt()

            imageView.layoutParams = binding.homeBanner.layoutParams.apply {
                height = fixedHeightPx
            }



            Glide.with(binding.root.context)

                .load(holidaybannerurl)
                .transition(DrawableTransitionOptions.withCrossFade(600))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)

            val todayimage = holidayurl
            handler.postDelayed({
                setupAppBarBanner(todayimage)
            }, 10000)
        }
    }

    fun handleHolidayWithoutImage(dbHelperimage: dbHelper, currentDayName: String) {
        Log.d("handleHolidayWithoutImage", "holidayurl.empty()")
        val todayimage = dbHelperimage.getimageByDayName(currentDayName)
        Log.d("handleHolidayWithoutImage", "$currentDayName")
        Log.d("handleHolidayWithoutImage", "$todayimage")

        val randomImageUrl = todayimage.random()["imageurl"]
        Log.d("randomImage", "Picked: $randomImageUrl")

        if (randomImageUrl != null) {
            setupAppBarBanner(randomImageUrl)
        }
    }



    fun setupAppBarBanner(todayimage: String) {
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
                    val randomImage = todayimage
                    Log.d("appbar", "rN$randomImage")

                    ///




                                                val imageView = binding.homeBanner

                                                  val fixedHeightDp = 530
                                                val fixedHeightPx = (fixedHeightDp * binding.root.context.resources.displayMetrics.density).toInt()

                                                imageView.layoutParams = binding.homeBanner.layoutParams.apply {
                                                    height = fixedHeightPx
                                                }


                                                Glide.with(binding.root.context).load(randomImage)
                                                    .transition(DrawableTransitionOptions.withCrossFade(800))
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
                        val imageView = binding.homeBanner

                    val fixedHeightDp = 520
                        val fixedHeightPx = (fixedHeightDp * binding.root.context.resources.displayMetrics.density).toInt()

                        imageView.layoutParams = binding.homeBanner.layoutParams.apply {
                            height = fixedHeightPx
                        }




                                                   Glide.with(binding.root.context)
                                                       .load(            appbarbannerurls[random])
                                                       .transition(DrawableTransitionOptions.withCrossFade(800))
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

    private fun loadTodaysDetails(todaydata: Map<String, Any?>) {
        lifecycleScope.launch {
            try {
                // Get references to all TextViews
                val todaytithiview: TextView = binding.todaytithi
                val tithiendtime: TextView = binding.tithiendtime

                val todaynakshatra: TextView = binding.todaynakshatra
                val nakshatraendtime: TextView = binding.nakshatraendtime

                val hindimonth: TextView = binding.hindimonth

                val todaypaksha: TextView = binding.paksha

                val todayrashi: TextView = binding.todayrashi

                val tyohaarname: TextView = binding.tyohaarname


                val selectedyear=getCurrentYear()

                val tithidbname="t$selectedyear.db"
                val ttable="t$selectedyear"
                val nakshatradbname="n$selectedyear.db"
                val ntable="n$selectedyear"


                val dbHelpertithi=dbHelper(this@HomeActivity,tithidbname)
                val dbHelpernakshatra=dbHelper(this@HomeActivity,nakshatradbname)

                val (todayDate, todayMonth) = getTodayDateAndMonth()
                val todaystithi=dbHelpertithi.getTithiRowsContainingDate(todayDate,todayMonth,tithidbname,ttable)
                val todaysnakshatra=dbHelpernakshatra.getTithiRowsContainingDate(todayDate,todayMonth,nakshatradbname,ntable)


                Log.d("todaystithi", "Received todaystithi: ${todaystithi}")

                val formattedTextt = todaystithi.joinToString(separator = " । ") { row ->
                    val hindiTithi = row["Hindi Tithi"] ?: "N/A"
                    var hindiTiming = row["Hindi Timinig"] ?: ""

                    // Fix: Move पूर्वाह्न/अपराह्न before the time
                    hindiTiming = hindiTiming.replace(Regex("(\\d{2}:\\d{2})\\s*(पूर्वाह्न|अपराह्न)")) {
                        "${it.groupValues[2]} ${it.groupValues[1]}"
                    }

                    "$hindiTithi - $hindiTiming"
                }




                val formattedTextn = todaysnakshatra.joinToString(separator = " । ") { row ->
                    val hindiTithi = row["Hindi Tithi"] ?: "N/A"
                    var hindiTiming = row["Hindi Timinig"] ?: ""

                    // Fix: Move पूर्वाह्न/अपराह्न before the time
                    hindiTiming = hindiTiming.replace(Regex("(\\d{2}:\\d{2})\\s*(पूर्वाह्न|अपराह्न)")) {
                        "${it.groupValues[2]} ${it.groupValues[1]}"
                    }

                    "$hindiTithi - $hindiTiming"
                }



                Log.d("todaysnakshatra", "Received data: ${todaysnakshatra}")

                runOnUiThread {



                    todaytithiview.text=formattedTextt
                    todaynakshatra.text=formattedTextn

                    todaydata["monthname"]?.let {
                        hindimonth.text = TranslationUtils.translateToHindiDevanagariHinduMonth(  it.toString())
                    } ?: run {
                        hindimonth.text = "-"
                    }


                    // Set paksha (directly available)
                    todaydata["paksha"]?.let {
                        todaypaksha.text = TranslationUtils.translateToPaksha(  it.toString())
                    } ?: run {
                        todaypaksha.text = "-"
                    }

                    // Set rashi (directly available)
                    todaydata["rashi"]?.let {
                        todayrashi.text = TranslationUtils.translateToHindiDevanagariRashi(  it.toString())
                    } ?: run {
                        todayrashi.text = "-"
                    }

                    val holidayName = todaydata["holidayname"]?.toString()

                  if (!holidayName.isNullOrBlank())
                    {    tyohaarname.text = holidayName}
                    else {
                        binding.tyohaarname.visibility=View.GONE
                        binding.holidaytitle.visibility=View.GONE
                    }




                    // For time fields not in your data
                    tithiendtime.text = "-"
                    nakshatraendtime.text = "-"




                    // If you have holiday information
                    todaydata["holiday name"]?.toString()?.let { holiday ->
                        // You might want to display this somewhere
                        Toast.makeText(this@HomeActivity, "Today is $holiday", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("TodaysDetails", "Error loading today's details", e)
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Error loading details", Toast.LENGTH_SHORT).show()
                }
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


    private fun startCountdown(hindiDate: String) {
        // Mapping of Hindi months to English month names
        val hindiMonths = mapOf(
            "जनवरी" to "January", "फरवरी" to "February", "मार्च" to "March", "अप्रैल" to "April",
            "मई" to "May", "जून" to "June", "जुलाई" to "July", "अगस्त" to "August",
            "सितंबर" to "September", "अक्तूबर" to "October", "नवंबर" to "November", "दिसंबर" to "December"
        )

        // Parse the Hindi date string
        val dateParts = hindiDate.split(" ")
        if (dateParts.size != 3) {
            Log.e("Countdown", "Invalid date format: $hindiDate")
            return
        }

        val day = dateParts[0].toIntOrNull()
        val monthHindi = dateParts[1]
        val year = dateParts[2].toIntOrNull()

        if (day == null || year == null) {
            Log.e("Countdown", "Invalid day or year in date: $hindiDate")
            return
        }

        val monthEnglish = hindiMonths[monthHindi] ?: run {
            Log.e("Countdown", "Invalid month name: $monthHindi")
            return
        }

        val month = SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthEnglish)?.month ?: run {
            Log.e("Countdown", "Failed to parse month: $monthEnglish")
            return
        }

        // Set target countdown date
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val targetDate = calendar.time
        val currentDate = Calendar.getInstance().time
        val diffInMillis = targetDate.time - currentDate.time

        if (diffInMillis > 0) {
            // Calculate the remaining days
            val remainingDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
            val remainingHours = ((diffInMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toInt()
            val remainingMinutes = ((diffInMillis % (1000 * 60 * 60)) / (1000 * 60)).toInt()
            val remainingSeconds = ((diffInMillis % (1000 * 60)) / 1000).toInt()

            // Update the UI with the remaining time
            binding.daysTextView.text = remainingDays.toString()
            binding.hoursTextView.text = remainingHours.toString()
            binding.minutesTextView.text = remainingMinutes.toString()
            binding.secondsTextView.text = remainingSeconds.toString()
        } else {
            // If the target date has passed, show 0 for all
            binding.daysTextView.visibility = View.GONE
            binding.hoursTextView.visibility =  View.GONE
            binding.minutesTextView.visibility =  View.GONE
            binding.secondsTextView.visibility =  View.GONE
        }




    }

    fun getTodayDateAndMonth(): Pair<String, String> {
        val date = Date()
        val dayFormat = SimpleDateFormat("dd", Locale.ENGLISH) // returns "01", "02", etc.
        val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH) // returns "Jan", "Feb", etc.

        val day = dayFormat.format(date)
        val month = monthFormat.format(date)

        return Pair(day, month)
    }



}



