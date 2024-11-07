package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import android.view.View
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.Utility.ViewShareUtil
import com.mithilakshar.mithilapanchang.databinding.ItemAlarmDataBinding
import com.mithilakshar.mithilapanchang.databinding.ItemCaldataBinding


class CalAdapter(private val context: Context, private var list: List<Map<String, Any?>>) : RecyclerView.Adapter<CalAdapter.CalViewHolder>() {
    private var expandedPosition = 0

    class CalViewHolder(val binding: ItemCaldataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(todaysdatedetails: Map<String, Any?>, isExpanded: Boolean,context: Context) {
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
                    nakshatraEndtimevalue.text=formattedNakshatraEnd
                    monthvalue.text=translatedMonth
                    rashivalue.text=rashi
                    pakshavalue.text=paksha
                    sunrisevalue.text=formattedSunrise
                    sunsetvalue.text=formattedSunset
                    yogvalue.text=yogNames
                    expandableLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

                }

            binding.share.setOnClickListener {

                ViewShareUtil.shareViewAsImageDirectly(binding.root, context )
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
        val isExpanded = position == expandedPosition
        val currentData = list[position]
        holder.bind(currentData,isExpanded,context)
        holder.binding.root.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position // Toggle expanded position
            notifyDataSetChanged() // Refresh the adapter to update the view
        }
    }

    fun updateHolidays(newHolidays: List<Map<String, Any?>>) {
        list = newHolidays
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
