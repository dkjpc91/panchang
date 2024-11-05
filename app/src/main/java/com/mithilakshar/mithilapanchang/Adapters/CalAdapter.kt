package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.databinding.ItemAlarmDataBinding
import com.mithilakshar.mithilapanchang.databinding.ItemCaldataBinding


class CalAdapter(private val context: Context, private var list: List<Map<String, Any?>>) : RecyclerView.Adapter<CalAdapter.CalViewHolder>() {

    class CalViewHolder(val binding: ItemCaldataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todaysdatedetails: Map<String, Any?>) {
            binding.apply {
                val tithiData = todaysdatedetails["tithi"]
                val parsedTithi = TranslationUtils.parseTithiInput(tithiData.toString())
                val tithiNames = parsedTithi?.joinToString("  एवं  ") ?: "~"


                // Parsing Nakshatra
                val nakshatraData = todaysdatedetails["nakshatra"]
                val parsedNakshatra = TranslationUtils.parseNakshatraInput(nakshatraData.toString())
                val nakshatraNames = parsedNakshatra?.joinToString("  एवं  ") ?: "~"


                // Month, Date, Day, and Year
                val monthName = todaysdatedetails["month"]?.toString()
                val monthhindi=TranslationUtils.translateToHindi(monthName.toString())

                val hindimonthName = todaysdatedetails["monthname"]?.toString()

                val translatedMonth = TranslationUtils.translateToHindiDevanagariHinduMonth(hindimonthName ?: "Unknown")
                Log.d("monthh", "$hindimonthName $translatedMonth")

                val day = todaysdatedetails["day"]?.toString()
                val date = todaysdatedetails["date"]?.toString()


                // Sunrise and Sunset
                val sunriseHour = todaysdatedetails["sunrise"]?.toString()?.toIntOrNull() ?: 0
                val sunriseMinute = todaysdatedetails["sunrisemin"]?.toString()?.toDoubleOrNull() ?: 0.0
                val formattedSunrise = TranslationUtils.formatTimeD(sunriseHour, sunriseMinute)

                val sunsetHour = todaysdatedetails["sunset"]?.toString()?.toIntOrNull() ?: 0
                val sunsetMinute = todaysdatedetails["sunsetmin"]?.toString()?.toDoubleOrNull() ?: 0.0
                val formattedSunset = TranslationUtils.formatTimeD(sunsetHour, sunsetMinute)


                // Tithi End Time
                val tithiEndH = todaysdatedetails["tithiendh"]?.toString() ?: "Unknown"
                val tithiEndM = todaysdatedetails["tithiendm"]?.toString() ?: "Unknown"
                var formattedTithiEnd=""
                if (tithiEndH != null && tithiEndM != null) {

                    val formattedOutput = TranslationUtils.createTithitimeformat(tithiEndH.toString(),
                        tithiEndM.toString()
                    )
                    formattedTithiEnd =formattedOutput
                }




                // Nakshatra End Time
                val nakshatraEndH = todaysdatedetails["nakshatraendh"]?.toString() ?: "Unknown"
                val nakshatraEndM = todaysdatedetails["nakshatraendm"]?.toString() ?: "Unknown"

                var formattedNakshatraEnd =" "
                if (nakshatraEndH != null && nakshatraEndM != null) {
                    val formattedOutput = TranslationUtils.createTithitimeformat(nakshatraEndH.toString(),
                        nakshatraEndM.toString()
                    )
                    formattedNakshatraEnd=formattedOutput

                }


                // Rashi and Paksha
                val rashi = TranslationUtils.translateToHindiDevanagariRashi(todaysdatedetails["rashi"].toString())


                val paksha = TranslationUtils.translateToPaksha(todaysdatedetails["paksha"].toString())


                // Yog
                val yogValue = todaysdatedetails["yog"]
                val parsedyog = TranslationUtils.parseYogaInput(yogValue.toString())
                val yogNames = parsedyog?.joinToString(" एवं ") ?: "~"


                binding.apply {
                    taskTitle.text =  "${TranslationUtils.translateToHindidaythree(day.toString())}, $date $monthhindi "
                    tithivalue.text=tithiNames
                    tithiEndtimevalue.text=formattedTithiEnd
                    nakshatravalue.text=nakshatraNames


                }



            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val binding = ItemCaldataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        val currentData = list[position]
        holder.bind(currentData)
    }

    fun updateHolidays(newHolidays: List<Map<String, Any?>>) {
        list = newHolidays
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
