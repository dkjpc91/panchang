package com.mithilakshar.mithilapanchang.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.databinding.ItemAlarmDataBinding
import com.mithilakshar.mithilapanchang.databinding.ItemCaldataBinding


class CalAdapter(private val context: Context, private var list: List<Map<String, Any?>>) : RecyclerView.Adapter<CalAdapter.CalViewHolder>() {

    class CalViewHolder(val binding: ItemCaldataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Map<String, Any?>) {
            binding.apply {
                val tithiData = model["tithi"]
                val parsedTithi = TranslationUtils.parseTithiInput(tithiData.toString())
                val tithiNames = parsedTithi?.joinToString(", ") ?: "~"


                // Parsing Nakshatra
                val nakshatraData = model["nakshatra"]
                val parsedNakshatra = TranslationUtils.parseNakshatraInput(nakshatraData.toString())
                val nakshatraNames = parsedNakshatra?.joinToString(", ") ?: "~"


                // Month, Date, Day, and Year
                val monthName = model["monthname"]?.toString()
                val translatedMonth =
                    TranslationUtils.translateToHindiDevanagariHinduMonth(monthName ?: "Unknown")


                val day = model["day"]?.toString()
                val date = model["date"]?.toString()
                val year = model["year"]?.toString()


                // Sunrise and Sunset
                val sunriseHour = model["sunrise"]?.toString()?.toIntOrNull() ?: 0
                val sunriseMinute = model["sunrisemin"]?.toString()?.toDoubleOrNull() ?: 0.0
                val formattedSunrise = TranslationUtils.formatTimeD(sunriseHour, sunriseMinute)

                val sunsetHour = model["sunset"]?.toString()?.toIntOrNull() ?: 0
                val sunsetMinute = model["sunsetmin"]?.toString()?.toDoubleOrNull() ?: 0.0
                val formattedSunset = TranslationUtils.formatTimeD(sunsetHour, sunsetMinute)


                // Tithi End Time
                val tithiEndH = model["tithiendh"]?.toString() ?: "Unknown"
                val tithiEndM = model["tithiendm"]?.toString() ?: "Unknown"
                val formattedTithiEnd = TranslationUtils.createTithitimeformat(tithiEndH, tithiEndM)


                // Nakshatra End Time
                val nakshatraEndH = model["nakshatraendh"]?.toString() ?: "Unknown"
                val nakshatraEndM = model["nakshatraendm"]?.toString() ?: "Unknown"
                val formattedNakshatraEnd =
                    TranslationUtils.createTithitimeformat(nakshatraEndH, nakshatraEndM)


                // Rashi and Paksha
                val rashi =
                    TranslationUtils.translateToHindiDevanagariRashi(model["rashi"].toString())


                val paksha = TranslationUtils.translateToPaksha(model["paksha"].toString())


                // Yog
                val yogValue = model["yog"]?.toString()?.toIntOrNull() ?: 0
                val translatedYog = TranslationUtils.translateNumberToYoga(yogValue)

                binding.apply {
                    tithiValue.text = tithiNames
                    nakshatraValue.text = nakshatraNames
                    month.text = translatedMonth
                    taskDate.text = "$day, $date $translatedMonth, $year"
                    sunrise.text = formattedSunrise
                    sunset.text = formattedSunset
                    tithiEndtime.text = formattedTithiEnd
                    nakshatraEndtime.text = formattedNakshatraEnd
                    yog.text = translatedYog


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
