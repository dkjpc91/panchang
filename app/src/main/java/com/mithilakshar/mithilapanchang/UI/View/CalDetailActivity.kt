package com.mithilakshar.mithilapanchang.UI.View

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.mithilakshar.mithilapanchang.Adapters.CalAdapter
import com.mithilakshar.mithilapanchang.Adapters.CalendarAdapter
import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager

import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.CalendarHelper
import com.mithilakshar.mithilapanchang.Utility.InterstitialAdManager
import com.mithilakshar.mithilapanchang.databinding.ActivityCalDetailBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class CalDetailActivity : AppCompatActivity() {

    private lateinit var adView5: AdView


    private lateinit var binding: ActivityCalDetailBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalAdapter
    private lateinit var spinner: Spinner
    private lateinit var calendarHelper: CalendarHelper
    private val selectedYear = MutableLiveData<Int>()

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }
    var fragmentindexnumber=0
    val fragmentindex = arrayOf(
        "JANUARY",
        "FEBRUARY",
        "MARCH",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUGUST",
        "SEPTEMBER",
        "OCTOBER",
        "NOVEMBER",
        "DECEMBER"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityCalDetailBinding.inflate(layoutInflater)




        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val networkdialog = Networkdialog(this)
        val networkManager= NetworkManager(this)
        networkManager.observe(this, {
            if (!it){
                if (!networkdialog.isShowing){networkdialog.show()}

            }else{
                if (networkdialog.isShowing){networkdialog.dismiss()}

            }
        })

        MobileAds.initialize(this)
        adView5 = findViewById(R.id.adView5)
        val adRequest = AdRequest.Builder().build()
        // Set an AdListener to make the AdView visible when the ad is loaded
        adView5.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Make the AdView visible when the ad is loaded
                adView5.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Optionally, you can log or handle the error here
            }
        }
        adView5.loadAd(adRequest)


        selectedYear.value = getCurrentYear()

        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.calendar, "2025"),
        )
        val adapter = CustomSpinnerAdapter(this@CalDetailActivity, R.layout.spinner_item, items)
        spinner=binding.spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? CustomSpinnerAdapter.SpinnerItem
                selectedItem?.let {
                    // Access the properties of SpinnerItem
                    val iconResId = it.iconResId
                    val text = it.text
                    // Do something with the selected item

                    if (text == "2024") {
                        // Perform action for Item 1
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    } else if (text == "2025") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2026") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2027") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2028") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2029") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }else if (text == "2030") {
                        // Perform action for Item 2
                        selectedYear.value=text.toInt()
                        // Update other UI elements or perform other actions
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when no item is selected (optional)
            }
        }
        val currentDate = LocalDate.now()

        selectedYear.observe(this, Observer {
            binding.monthName.text=translateToHindi(currentDate.month.toString()) +" "+it
            fragmentindexnumber = fragmentindex.indexOf(currentDate.month.toString())



            loadfragmentdata( fragmentindex[fragmentindexnumber],it)
        })

        binding.backmonth.setOnClickListener {
            // Move to the previous month
            if (fragmentindexnumber == 0) {
                fragmentindexnumber = fragmentindex.size - 1 // Wrap to last month
            } else {
                fragmentindexnumber-- // Decrement to previous month
            }

            selectedYear.observe(this, { year ->
                binding.monthName.text = translateToHindi(fragmentindex[fragmentindexnumber]) + " " + year
                loadfragmentdata(fragmentindex[fragmentindexnumber], year)
            })
        }

        binding.forwardmonth.setOnClickListener {
            // Move to the next month
            if (fragmentindexnumber == fragmentindex.size - 1) {
                fragmentindexnumber = 0 // Wrap to first month
            } else {
                fragmentindexnumber++ // Increment to next month
            }

            selectedYear.observe(this, { year ->
                binding.monthName.text = translateToHindi(fragmentindex[fragmentindexnumber]) + " " + year
                loadfragmentdata(fragmentindex[fragmentindexnumber], year)
            })
        }


    }

    private fun loadfragmentdata(month:String,year: Int) {

        lifecycleScope.launch {

            var dbname="cal$year.db"
            var table="cal"
            if (year>2023){
                dbname="cal$year.db"
                table="$table$year"
                calendarHelper = CalendarHelper(this@CalDetailActivity, dbname)

                val rows = calendarHelper.getAllTableDataForMonth(table,month)
                val mergedRows = mergeRowsByDate(rows)

                logMergedRows(mergedRows)

                recyclerView = binding.calrecycler
                recyclerView.layoutManager = LinearLayoutManager(this@CalDetailActivity)
                adapter = CalAdapter(this@CalDetailActivity,mergedRows)
                recyclerView.adapter = adapter

            }else{

            }











        }



    }
    fun adjustListForDay(data: List<Map<String, Any?>>): List<Map<String, Any?>> {
        // Check if the list is not empty
        if (data.isNotEmpty()) {
            // Get the first element's "day"
            val firstDay = data[0]["day"] as? String

            // Determine how many blank elements to add
            val blanksToAdd = when (firstDay?.lowercase()) {
                "mon" -> 0
                "tue" -> 1
                "wed" -> 2
                "thu" -> 3
                "fri" -> 4
                "sat" -> 5
                "sun" -> 6
                else -> 0 // If the day is unknown or not found, add no blanks
            }

            // Create the blank maps to insert (all fields are null)
            val blankEntry = mapOf<String, Any?>(
                "sno" to null, "month" to null, "date" to null, "day" to null,
                "sunrise" to null, "sunrisemin" to null, "noon" to null,
                "noon min" to null, "sunset" to null, "sunsetmin" to null,
                "tithi" to null, "tithiendh" to null, "tithiendm" to null,
                "nakshatra" to null, "nakshatraendh" to null, "nakshatraendm" to null,
                "yog" to null, "yogendh" to null, "yogendm" to null,
                "monthname" to null, "rashi" to null, "paksha" to null
            )

            // Create a list with the blank entries
            val blankEntries = List(blanksToAdd) { blankEntry }

            // Return a new list with blank entries followed by the original data
            return blankEntries + data
        }
        // If the list is empty, just return the original list
        return data
    }
    private fun translateToHindi(currentMonth: String?): String? {
        // Manually create a mapping for English to Hindi month names
        val monthTranslation: MutableMap<String?, String> = HashMap()
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
}