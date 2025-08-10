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
import java.io.File

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

        val downloadDirectory = File(requireContext().getExternalFilesDir(null), "test")

        val itemsCaln = downloadDirectory.listFiles()
            ?.map { it.nameWithoutExtension }
            ?.filter { name ->
                name.startsWith("caln", ignoreCase = true) &&
                        name.drop(4).all(Char::isDigit) // everything after "caln" must be digits
            }
            ?.onEach { calnName ->
                Log.d("MyTagCaln", "Found caln file: $calnName")
            }
            ?.map { name ->
                val year = name.drop(4) // remove "caln"
                CustomSpinnerAdapter.SpinnerItem(R.drawable.calendar, year)
            }
            ?: emptyList()

        Log.d("MyTagCaln", "Spinner Items: $itemsCaln")



        val items = listOf(
            CustomSpinnerAdapter.SpinnerItem(R.drawable.calendar, "2025"),
        )
        val adapter = CustomSpinnerAdapter(requireContext(), R.layout.spinner_item, itemsCaln)
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

                Log.d("rows", "rows$rows")

                val adjustedrows=adjustListForDayfinal(rows)
                Log.d("rows", "adjustedrows $adjustedrows")
                logMergedRows(adjustedrows)

                val calendarAdapter=CalendarAdapter(adjustedrows,requireContext(),year)
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


    fun adjustListForDayfinal(rows: List<Map<String, Any?>>): List<Map<String, Any?>> {
        if (rows.isEmpty()) return rows

        // Get the first row's "day"
        val firstDay = rows[0]["day"]?.toString()?.trim()?.lowercase() ?: return rows

        // Determine how many blanks to add (Mon = 0, Tue = 1, ..., Sun = 6)
        val blanksToAdd = when (firstDay) {
            "mon" -> 0
            "tue" -> 1
            "wed" -> 2
            "thu" -> 3
            "fri" -> 4
            "sat" -> 5
            "sun" -> 6
            else -> 0
        }

        // Define your blank entry map (make sure keys match your original maps)
        val blankEntry = mapOf(
            "sno" to null, "month" to null, "date" to null, "day" to null,
            "monthname" to null, "rashi" to null, "paksha" to null, "helper" to null,
            "hindidate" to null, "holidayname" to null, "monthhindi" to null,
            "holidaydesc" to null,
            "holidayimage" to "https://i.pinimg.com/736x/35/71/00/35710061f8c642aedec21dc90c7f4262.jpg",
            "filter" to null
        )

        // Create the final list with blanks at the beginning
        val blanks = List(blanksToAdd) { blankEntry }
        return blanks + rows
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