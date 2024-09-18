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
import android.content.Context

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
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil

import com.mithilakshar.mithilapanchang.Utility.dbDownloader
import com.mithilakshar.mithilapanchang.Utility.dbDownloadersequence
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
    private lateinit var dbHelperHoliday: dbHelper


    private lateinit var auth: FirebaseAuth

    private  var holidaybannerurl :String =""

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

    private lateinit var fileDownloader: FirebaseFileDownloader

    companion object {
        private const val REQUEST_WRITE_STORAGE = 1
    }





    private lateinit var adView: AdView
    private lateinit var adviewMR: AdView


    private var delayMillis: Long = 4000

    private lateinit var dbDownloadersequence: dbDownloadersequence




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

        val randomValue = (1..100).random()

        if (randomValue <= 70) {
            // Load adviewMR 70% of the time
            adviewMR.loadAd(adRequest)
        } else {
            // Load adView 30% of the time
            adView.loadAd(adRequest)
        }






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
        dbDownloadersequence = dbDownloadersequence(updatesDao, fileDownloader)

        val filesWithIds = listOf(
            Pair("holiday", 2),
            Pair("calander", 3),
            Pair("holiday2025",11),
            Pair("imageauto", 5)
        )

        lifecycleScope.launch {
            val updateChecker = UpdateChecker(updatesDao)
            val isUpdateNeeded = updateChecker.getUpdateStatus()
            if (isUpdateNeeded!="a") {

                Log.d("updatechecker", " :  needed $isUpdateNeeded")

                dbDownloadersequence.observeMultipleFileExistence(
                    filesWithIds,
                    this@HomeActivity,
                    lifecycleScope,
                    homeActivity = this@HomeActivity, // Your activity
                    progressCallback = { progress, filePair  ->



                        Log.d("Progress", "File: $filePair, Progress: $progress%")


                    },{



                        dbHelperHoliday = dbHelper(this@HomeActivity, "holiday.db")


                       /* setupViewPagerAndDatabase(
                            context =this@HomeActivity,
                            currentMonthString = currentMonthString,
                            currentDay = currentDay,
                            delayMillis = delayMillis,
                            dbHelperHoliday
                        )*/



                    }
                )


            } else {

                dbHelperHoliday = dbHelper(this@HomeActivity, "holiday.db")
                dbHelpercalander = dbHelper(this@HomeActivity, "calander.db")
                dbHelperimage = dbHelper(this@HomeActivity, "imageauto.db")
                homeBroadcast = viewModel.gethomeBroadcast()
                Log.d("homeBroadcast", "$homeBroadcast")

                if (homeBroadcast.isNullOrEmpty()) {
                    binding.floatingActionButton.visibility = View.GONE
                } else {
                    binding.floatingActionButton.visibility = View.VISIBLE
                }
                val rowsFormonthdate = getRowByMonthAndDate(dbHelpercalander, currentMonthString, currentDay.toString())
                speak = rowsFormonthdate?.get("speak") ?: ""
                textToSpeech = TextToSpeech(this@HomeActivity, TextToSpeech.OnInitListener { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeech?.language = Locale.forLanguageTag("hi")
                        Log.d("speak", "TTS success")
                        delayedTask(1000,speak.toString())
                    } else {
                        Log.d("speak", "TTS failed")
                    }
                })



                handleHolidayData(
                    dbHelpercalander =dbHelpercalander,
                    dbHelperimage =dbHelperimage,
                    currentMonthString = currentMonthString,
                    currentDay = currentDay,
                    currentDayName = currentDayName
                )

               /* setupViewPagerAndDatabase(
                    context =this@HomeActivity,
                    currentMonthString = currentMonthString,
                    currentDay = currentDay,
                    delayMillis = delayMillis,
                    dbHelperHoliday
                )
*/
                Log.d("updatechecker", " : not needed $isUpdateNeeded")
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

                }
            }



































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

                val shareText = "पंचांग ऐप डाउनलोड करने के लिए नीचे दिए गए लिंक पर क्लिक करें .\n https://play.google.com/store/apps/details?id=com.mithilakshar.mithilapanchang  \n\n\n @mithilakshar13"

                putExtra(Intent.EXTRA_TEXT, shareText)
                // Set the MIME type
                type = "text/plain"
            }
            // Start the activity with the intent
            startActivity(Intent.createChooser(intent, "शेयर: "))
        }



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


        if (::adviewMR.isInitialized) {
            adviewMR.destroy()
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






    fun checkFileExistence(fileName: String): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath =
            this.getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }










    fun setupViewPagerAndDatabase(
        context: Context,
        currentMonthString: String,
        currentDay: Int,
        delayMillis: Long,
        dbHelperHoliday:dbHelper
    ) {


        // Check if column exists
        val doesColumnExist = dbHelperHoliday.doesColumnExist("holiday", "datenumber")
        Log.d("doesColumnExist", "$doesColumnExist")

        // Prepare holidayMonthList
        val holidayMonthList: MutableList<Map<String, String>> = mutableListOf(
            mapOf(
                "month" to "पंचांग",
                "date" to "",
                "name" to "पंचांग",
                "desc" to ""
            )
        )

        // Fetch holidays based on column existence
        if (doesColumnExist) {
            holidayMonthList.addAll(
                dbHelperHoliday.getHolidaysByMonthdate(
                    currentMonthString.lowercase(Locale.getDefault()),
                    currentDay.toString()
                )
            )
            Log.d("doesColumnExist", "dateExists $doesColumnExist")
        } else {
            holidayMonthList.addAll(
                dbHelperHoliday.getHolidaysByMonth(
                    currentMonthString.lowercase(Locale.getDefault())
                )
            )
            Log.d("doesColumnExist", "notExist $doesColumnExist")
        }

        Log.d("holidayMonthList", "$holidayMonthList")

        // Set up ViewPager and adapter
        val handler = Handler(Looper.getMainLooper())
        val sliderAdapter = SliderAdapter(holidayMonthList)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager) // Replace with actual ViewPager2 ID

        viewPager.adapter = sliderAdapter
        binding.frame.visibility=View.VISIBLE

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




    fun handleHolidayData(dbHelpercalander: dbHelper, dbHelperimage: dbHelper, currentMonthString: String, currentDay: Int, currentDayName: String) {
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

    fun getRowByMonthAndDate(dbHelpercalander: dbHelper, currentMonthString: String, currentDay: String): Map<String, String>? {
        return dbHelpercalander.getRowByMonthAndDate(currentMonthString, currentDay)
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





}

