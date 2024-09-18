package com.mithilakshar.mithilapanchang.UI.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mithilakshar.mithilapanchang.Adapters.CalendarAdapter
import com.mithilakshar.mithilapanchang.Utility.dbHelper

import com.mithilakshar.mithilapanchang.ViewModel.HomeViewModel

import com.mithilakshar.mithilapanchang.databinding.FragmentMayfragmentBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [mayfragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class mayfragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var binding: FragmentMayfragmentBinding

    private lateinit var dbHelper: dbHelper

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


        val currentDate = LocalDate.now()
        binding.monthName.text=translateToHindi(currentDate.month.toString())


        fragmentindexnumber = fragmentindex.indexOf(currentDate.month.toString())



        loadfragmentdata( fragmentindex[fragmentindexnumber])


        binding.backmonth.setOnClickListener {
            if(fragmentindexnumber==0){

                fragmentindexnumber=(fragmentindex.size-1)
                binding.monthName.text=translateToHindi(fragmentindex[fragmentindexnumber])
                loadfragmentdata(fragmentindex[fragmentindexnumber])

            }
            else {
                binding.monthName.text=translateToHindi(fragmentindex[--fragmentindexnumber])
                loadfragmentdata(fragmentindex[fragmentindexnumber])
            }

        }

        binding.forwardmonth.setOnClickListener {

            if(fragmentindexnumber==(fragmentindex.size-1)){

                fragmentindexnumber=0
                binding.monthName.text=translateToHindi(fragmentindex[fragmentindexnumber])
                loadfragmentdata(fragmentindex[fragmentindexnumber])

            }
            else{

                binding.monthName.text=translateToHindi(fragmentindex[++fragmentindexnumber])
                loadfragmentdata(fragmentindex[fragmentindexnumber])
            }

        }



    }

    private fun loadfragmentdata(path:String) {

        lifecycleScope.launch {

            dbHelper = dbHelper(requireContext(), "calander.db")
            val rowsForAugust = dbHelper.getRowsByMonth(path)

            val rowsForkaybwordAugust2 = dbHelper.getRowsByColumnKeywordIfExists("calander","keyword","ekadashi")
            Log.d("rowsForAugust", "calander data is $rowsForkaybwordAugust2")



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