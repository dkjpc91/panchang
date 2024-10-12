package com.mithilakshar.mithilapanchang.UI.View



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.appcompat.widget.SearchView

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds



import com.mithilakshar.mithilapanchang.Adapters.holidayadapter
import com.mithilakshar.mithilapanchang.Dialog.Networkdialog
import com.mithilakshar.mithilapanchang.Notification.NetworkManager
import com.mithilakshar.mithilapanchang.R

import com.mithilakshar.mithilapanchang.Utility.dbHelper


import com.mithilakshar.mithilapanchang.databinding.ActivityHolidaylistBinding

import java.util.Calendar


class HolidayListActivity : AppCompatActivity() {


    private lateinit var adView5: AdView
    lateinit var binding:ActivityHolidaylistBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: holidayadapter
    private var holidays: MutableList<Map<String, String>> = mutableListOf()
    private lateinit var dbHelperHolidaylistfilter: dbHelper

    private lateinit var searchView: SearchView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityHolidaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

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




        recyclerView = binding.holidayrecycler
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = holidayadapter(this, holidays)
        recyclerView.adapter = adapter


        // Notify adapter that data set has changed
        adapter.notifyDataSetChanged()

        val intent = intent
        val month = intent.getStringExtra("month").toString()
        val monthEng = intent.getStringExtra("monthEng").toString()
        val intValue = intent.getIntExtra("intValue", 1)
        val selectedyear = intent.getIntExtra("year", getCurrentYear())




        readFileContent(monthEng,selectedyear)



        searchView=binding.searchview



        if (selectedyear>2023){
            val a="holi$selectedyear.db"
            val b="holi$selectedyear"
            dbHelperHolidaylistfilter=dbHelper(this@HolidayListActivity,a)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val filteredList =dbHelperHolidaylistfilter.searchFilter(newText ?: "", b)
                    Log.d("filteredList", "filteredList   $b  : $filteredList")
                    binding.title.text="हिन्दू त्योहार - "+"$selectedyear"
                    adapter.updateHolidays(filteredList)
                    return true
                }
            })


        }








        binding.title.text="हिन्दू त्योहार - "+"$month"+"$selectedyear"





    }




    private fun readFileContent(month:String,year:Int) {


            val a="holi$year.db"

            val dbHelper = dbHelper(applicationContext, a)
           val av = dbHelper.getHolidaysByMonthanddb(month,"holi$year")
            val filterexist=dbHelper.doesColumnExist("holi$year","filter")
            if (filterexist){
                binding.searchview.visibility=View.VISIBLE

            }
            Log.d("filterexist", "year: $av")
            Log.d("filterexist", "year: $filterexist")
            holidays.addAll(av)
            adapter.notifyDataSetChanged()





    }









    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }


    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }





}