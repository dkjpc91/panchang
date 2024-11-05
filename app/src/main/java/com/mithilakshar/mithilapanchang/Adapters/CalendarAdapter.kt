package com.mithilakshar.mithilapanchang.Adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mithilakshar.mithilapanchang.CalendarDialog
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.Utility.TranslationUtils
import com.mithilakshar.mithilapanchang.databinding.CalendardayitemBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class CalendarAdapter(
    private val data: List<Map<String, Any?>>,
    private val context: Context,
    private val year: Int
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(private val binding: CalendardayitemBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(model: Map<String, Any?>, context: Context, year: Int) {
            binding.apply {
                val currentDateStr = getCurrentDateString()
                val currentMonthStr = getCurrentMonthName()
                Log.d("date", "Current Date: $currentDateStr, Current Month: $currentMonthStr, Model Month: ${model["month"]}")


                val tithiData = model["tithi"]
                val parsedTithi = TranslationUtils.parseTithiInput(tithiData.toString())
                val tithiNames = parsedTithi?.joinToString(", ") ?: "~"

                calendardayText.text = tithiNames
                calendardateText.text = model["date"]?.toString() ?: ""
                calendardescText.text = TranslationUtils.translateToPaksha(model["paksha"]?.toString() ?: "")


                val backgroundColor = when {
                    model["holiday"].toString().isNotEmpty() && model["date"].toString() != currentDateStr -> {
                        ContextCompat.getColor(context, R.color.background)
                    }
                    model["holiday"].toString().isNotEmpty() && model["month"].toString() != currentMonthStr -> {
                        ContextCompat.getColor(context, R.color.background)
                    }
                    model["date"].toString() == currentDateStr && model["month"].toString() == currentMonthStr -> {
                        ContextCompat.getColor(context, R.color.green)
                    }
                    else -> Color.WHITE
                }

                setBackgroundColor(binding, backgroundColor)
            }

            binding.root.setOnClickListener {
                showCalendarDialog(context, model, year)
            }
        }

        private fun setBackgroundColor(binding: CalendardayitemBinding, color: Int) {
            binding.apply {
                calendardateText.setBackgroundColor(color)
                calendardayText.setBackgroundColor(color)
                calendardescText.setBackgroundColor(color)
            }
        }

        private fun getCurrentDateString(): String {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            return dateFormat.format(Calendar.getInstance().time)
        }

        private fun getCurrentMonthName(): String {
            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            return dateFormat.format(Calendar.getInstance().time).uppercase(Locale.getDefault())
        }

        private fun showCalendarDialog(context: Context, todaysdatedetails: Map<String, Any?>, year: Int) {
            val calendarDialog = CalendarDialog(context)

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


            Log.d("monthh", "$yogValue $yogNames")

            // Set all values in the dialog
            calendarDialog.setDialogValues(
                date =    "${TranslationUtils.translateToHindidaythree(day.toString())}, $date $monthhindi, $year",

                    tithi = tithiNames,
                tithiEndtime = formattedTithiEnd,
                    nakshatra = nakshatraNames,
                nakshatraEndtime = formattedNakshatraEnd,

                    month = translatedMonth,

                        rashi = rashi,
                     paksha = paksha,
                   sunrise=formattedSunrise,
                    sunset=formattedSunset,



                    yog=yogNames,


                )

            calendarDialog.show()
        }


    }

    private var itemWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = CalendardayitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = binding.root.layoutParams
        layoutParams.width = itemWidth
        binding.root.layoutParams = layoutParams
        return CalendarViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val currentData = data[position]
        holder.bind(currentData, context, year)
    }

    fun setItemWidth(itemWidth: Int) {
        this.itemWidth = itemWidth
        notifyDataSetChanged() // Refresh the adapter to apply the new item width
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun translateToHindi(currentMonth: String): String? {
        val monthTranslation = mapOf(
            "JANUARY" to "जनवरी",
            "FEBRUARY" to "फ़रवरी",
            "MARCH" to "मार्च",
            "APRIL" to "अप्रैल",
            "MAY" to "मई",
            "JUNE" to "जून",
            "JULY" to "जुलाई",
            "AUGUST" to "अगस्त",
            "SEPTEMBER" to "सितंबर",
            "OCTOBER" to "अक्टूबर",
            "NOVEMBER" to "नवंबर",
            "DECEMBER" to "दिसंबर"
        )
        return monthTranslation[currentMonth]
    }

    private fun translateToHindiday(currentDay: String): String? {
        val dayTranslation = mapOf(
            "MONDAY" to "सोमवार",
            "TUESDAY" to "मंगलवार",
            "WEDNESDAY" to "बुधवार",
            "THURSDAY" to "गुरुवार",
            "FRIDAY" to "शुक्रवार",
            "SATURDAY" to "शनिवार",
            "SUNDAY" to "रविवार"
        )
        return dayTranslation[currentDay]
    }

    private fun translateToHindidate(date: String): String? {
        val dateTranslation = mapOf(
            "1" to "१", "2" to "२", "3" to "३", "4" to "४", "5" to "५",
            "6" to "६", "7" to "७", "8" to "८", "9" to "९", "10" to "१०",
            "11" to "११", "12" to "१२", "13" to "१३", "14" to "१४",
            "15" to "१५", "16" to "१६", "17" to "१७", "18" to "१८",
            "19" to "१९", "20" to "२०", "21" to "२१", "22" to "२२",
            "23" to "२३", "24" to "२४", "25" to "२५", "26" to "२६",
            "27" to "२७", "28" to "२८", "29" to "२९", "30" to "३०",
            "31" to "३१", "2024" to "२०२४", "2025" to "२०२५",
            "2026" to "२०२६", "2027" to "२०२७", "2028" to "२०२८",
            "2029" to "२०२९", "2030" to "२०३०"
        )
        return dateTranslation[date]
    }
}
