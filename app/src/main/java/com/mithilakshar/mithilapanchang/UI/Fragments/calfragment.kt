package com.mithilakshar.mithilapanchang.UI.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mithilakshar.mithilapanchang.Adapters.CalendarAdapter
import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.R

import com.mithilakshar.mithilapanchang.Utility.CalendarHelper


import com.mithilakshar.mithilapanchang.databinding.FragmentMayfragmentBinding

import kotlinx.coroutines.launch

import java.time.LocalDate
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [calfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class calfragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var binding: FragmentMayfragmentBinding
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentMayfragmentBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedYear.value = getCurrentYear()

        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.calendar, "2025"),
        )
        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_item, items)
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

        selectedYear.observe(viewLifecycleOwner, Observer {
            binding.monthName.text=translateToHindi(currentDate.month.toString()) +" "+it
            fragmentindexnumber = fragmentindex.indexOf(currentDate.month.toString())



            loadfragmentdata( fragmentindex[fragmentindexnumber],it)
        })






        binding.backmonth.setOnClickListener {
            if (fragmentindexnumber == 0) {
                fragmentindexnumber = fragmentindex.size - 1
            } else {
                fragmentindexnumber--
            }

            selectedYear.observe(viewLifecycleOwner, { year ->
                binding.monthName.text = translateToHindi(fragmentindex[fragmentindexnumber]) + " " + year
                loadfragmentdata(fragmentindex[fragmentindexnumber], year)
            })
        }


        binding.forwardmonth.setOnClickListener {
            if (fragmentindexnumber == fragmentindex.size - 1) {
                fragmentindexnumber = 0
            } else {
                fragmentindexnumber++
            }

            selectedYear.observe(viewLifecycleOwner, { year ->
                binding.monthName.text = translateToHindi(fragmentindex[fragmentindexnumber]) + " " + year
                loadfragmentdata(fragmentindex[fragmentindexnumber], year)
            })
        }




    }



    private fun loadfragmentdata(month:String,year: Int) {

        lifecycleScope.launch {
            var dbname="caln.db"
            var table="caln"
            if (year>2023){
                dbname="caln$year.db"
                table="$table$year"
                calendarHelper = CalendarHelper(requireContext(), dbname)

                val rows = calendarHelper.getAllTableDataForMonth(table,month)

                Log.d("rows", "$rows")
     /*           val mergedRows = mergeRowsByDate(rows)
                val adjustedrows=adjustListForDayfinal(mergedRows)
                logMergedRows(mergedRows)*/

                val calendarAdapter=CalendarAdapter(rows,requireContext(),year)
                val layoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(context, 7, LinearLayoutManager.HORIZONTAL, false)

                var screenWidth = resources.displayMetrics.widthPixels
                screenWidth = screenWidth - 293
                val itemWidth = screenWidth / 5 // Number of columns is 5
                calendarAdapter.setItemWidth(itemWidth)
                binding.calendarRecycler.layoutManager=layoutManager
                binding.calendarRecycler.adapter=calendarAdapter

            }else{

            }











        }



    }

    fun adjustListForDayfinal(data: List<Map<String, Any?>>): List<Map<String, Any?>> {
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

            // Add blank entries followed by the original data
            var result = (blankEntries + data).toMutableList()

            // Check if the resulting list has more than 35 elements
            if (result.size > 35) {
                // Elements beyond the 35th position (starting from index 35)
                val excessElements = result.subList(35, result.size)

                // For each excess element, adjust its "sno" and overwrite the initial elements
                excessElements.forEachIndexed { index, map ->
                    if (index < 35) {
                        // Adjust "sno" of excess element to 36, 37, 38, ...
                        val adjustedMap = map.toMutableMap()
                        adjustedMap["sno"] = 36 + index

                        // Overwrite the element at the corresponding index
                        result[index] = adjustedMap
                    }
                }

                // Trim the list to 35 elements after overwriting
                result = result.take(35).toMutableList()
            }

            // Return the final adjusted list
            return result
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