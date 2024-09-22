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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mithilakshar.mithilapanchang.Adapters.CalendarAdapter
import com.mithilakshar.mithilapanchang.Adapters.CustomSpinnerAdapter
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Room.UpdatesDao
import com.mithilakshar.mithilapanchang.Room.UpdatesDatabase
import com.mithilakshar.mithilapanchang.Utility.FirebaseFileDownloader
import com.mithilakshar.mithilapanchang.Utility.UpdateChecker
import com.mithilakshar.mithilapanchang.Utility.dbDownloadersequence
import com.mithilakshar.mithilapanchang.Utility.dbHelper

import com.mithilakshar.mithilapanchang.ViewModel.HomeViewModel

import com.mithilakshar.mithilapanchang.databinding.FragmentMayfragmentBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [mayfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class mayfragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var binding: FragmentMayfragmentBinding
    private lateinit var spinner: Spinner
    private lateinit var dbHelper: dbHelper
    private val selectedYear = MutableLiveData<Int>()

    private lateinit var updatesDao: UpdatesDao
    private lateinit var fileDownloader: FirebaseFileDownloader
    private lateinit var dbDownloadersequence: dbDownloadersequence

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }

    val viewmodelhome : HomeViewModel by viewModels()

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
            CustomSpinnerAdapter.SpinnerItem(R.drawable.calendar, "2024"),
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


        fileDownloader = FirebaseFileDownloader(requireContext())
        updatesDao = UpdatesDatabase.getDatabase(requireContext()).UpdatesDao()
        dbDownloadersequence = dbDownloadersequence(updatesDao, fileDownloader)
        val filesWithIds = listOf(
            Pair("calander2025",21),

            )

        lifecycleScope.launch {
            val updateChecker = UpdateChecker(updatesDao)
            val isUpdateNeeded = updateChecker.getUpdateStatus()
            val nonExistentFiles = mutableListOf<Pair<String, Int>>()
            val jobs = mutableListOf<Job>()
            for (filePair in filesWithIds) {
                val job = launch {
                    // Observe the LiveData returned by checkFileExistence
                    checkFileExistence("${filePair.first}.db").observeForever { exists ->
                        if (exists != null && !exists) {
                            nonExistentFiles.add(filePair)
                        }
                    }
                }
                jobs.add(job)
            }
            jobs.joinAll()


            Log.d("nonExistentFiles", " :  nonExistentFiles  $nonExistentFiles")
            if (isUpdateNeeded!="a" ) {

                Log.d("updatechecker", " :  needed $isUpdateNeeded")

                dbDownloadersequence.observeMultipleFileExistence(
                    filesWithIds,
                    requireActivity(),
                    lifecycleScope,
                    homeActivity = requireActivity(), // Your activity
                    progressCallback = { progress, filePair  ->



                        Log.d("update", "File: $filePair, Progress: $progress%")


                    },{

                        // observeFileExistence("holiday",2)
                        binding.spinner.visibility=View.VISIBLE

                        Log.d("update", " total update completed")


                    }
                )


            } else {

                dbDownloadersequence.observeMultipleFileExistence(
                    nonExistentFiles,
                    requireActivity(),
                    lifecycleScope,
                    requireActivity(), // Your activity
                    progressCallback = { progress, filePair  ->



                        Log.d("update", "File: $filePair, Progress: $progress%")


                    },{

                        // observeFileExistence("holiday",2)
                        binding.spinner.visibility=View.VISIBLE
                        Log.d("update", " total non update  completed")


                    }
                )


            }
        }


        val currentDate = LocalDate.now()

        selectedYear.observe(viewLifecycleOwner, Observer {
            binding.monthName.text=translateToHindi(currentDate.month.toString()) +" "+it
            fragmentindexnumber = fragmentindex.indexOf(currentDate.month.toString())



            loadfragmentdata( fragmentindex[fragmentindexnumber],it)
        })






        binding.backmonth.setOnClickListener {
            if(fragmentindexnumber==0){

                fragmentindexnumber=(fragmentindex.size-1)
                selectedYear.observe(viewLifecycleOwner, {
                    binding.monthName.text=translateToHindi(fragmentindex[fragmentindexnumber]) +" "+it
                    loadfragmentdata(fragmentindex[fragmentindexnumber],it)
                })





            }
            else {
                selectedYear.observe(viewLifecycleOwner, {
                    binding.monthName.text=translateToHindi(fragmentindex[--fragmentindexnumber])+" "+it
                    loadfragmentdata(fragmentindex[fragmentindexnumber],it)
                })



            }

        }

        binding.forwardmonth.setOnClickListener {

            if(fragmentindexnumber==(fragmentindex.size-1)){

                fragmentindexnumber=0

                selectedYear.observe(viewLifecycleOwner, {
                    binding.monthName.text=translateToHindi(fragmentindex[fragmentindexnumber])+" "+it
                    loadfragmentdata(fragmentindex[fragmentindexnumber],it)
                })



            }
            else{
                selectedYear.observe(viewLifecycleOwner, {
                    binding.monthName.text=translateToHindi(fragmentindex[++fragmentindexnumber])+" "+it
                    loadfragmentdata(fragmentindex[fragmentindexnumber],it)
                })


            }

        }



    }

    fun checkFileExistence(fileName: String): LiveData<Boolean> {
        val fileExistsLiveData = MutableLiveData<Boolean>()
        val dbFolderPath = requireContext().getExternalFilesDir(null)?.absolutePath + File.separator + "test"
        val dbFile = File(dbFolderPath, fileName)
        fileExistsLiveData.value = dbFile.exists()
        return fileExistsLiveData
    }

    private fun loadfragmentdata(month:String,year: Int) {

        lifecycleScope.launch {
            var dbname="calander.db"
            var table="calander"
            if (year>2024){
                dbname="calander$year.db"
                table="$table$year"
                dbHelper = dbHelper(requireContext(), dbname)
                val rowsForAugust = dbHelper.getRowsByMonth(month, table)
                val calendarAdapter=CalendarAdapter(rowsForAugust,requireContext())
                val layoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(context, 7, LinearLayoutManager.HORIZONTAL, false)

                var screenWidth = resources.displayMetrics.widthPixels
                screenWidth = screenWidth - 293
                val itemWidth = screenWidth / 5 // Number of columns is 5
                calendarAdapter.setItemWidth(itemWidth)
                binding.calendarRecycler.layoutManager=layoutManager
                binding.calendarRecycler.adapter=calendarAdapter

            }else{
                dbname="calander.db"
                table="calander"
                dbHelper = dbHelper(requireContext(), dbname)
                val rowsForAugust = dbHelper.getRowsByMonth(month, table)
                val calendarAdapter=CalendarAdapter(rowsForAugust,requireContext())
                val layoutManager: RecyclerView.LayoutManager =
                    GridLayoutManager(context, 7, LinearLayoutManager.HORIZONTAL, false)

                var screenWidth = resources.displayMetrics.widthPixels
                screenWidth = screenWidth - 293
                val itemWidth = screenWidth / 5 // Number of columns is 5
                calendarAdapter.setItemWidth(itemWidth)
                binding.calendarRecycler.layoutManager=layoutManager
                binding.calendarRecycler.adapter=calendarAdapter

            }











        }



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


}