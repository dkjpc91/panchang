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
import com.mithilakshar.mithilapanchang.Utility.dbHelper
import com.mithilakshar.mithilapanchang.databinding.CalendardayitemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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


                val tithiData = model["monthname"]

                calendardayText.text = TranslationUtils.translateToHindiDevanagariHinduMonth(
                    tithiData.toString())
                calendardateText.text = model["date"]?.toString() ?: ""
                calendardescText.text = TranslationUtils.translateToPaksha(model["paksha"]?.toString() ?: "")


                val backgroundColor = when {
                    // Case 1: If "holidayname" is not empty and the date is not today's date
                    model["holidayname"]?.toString()?.isNotEmpty() == true &&
                            model["date"]?.toString() != currentDateStr -> {
                        ContextCompat.getColor(context, R.color.background)
                    }

                    // Case 2: If "holidayname" is not null/blank, not "null", and month is not current month
                    !model["holidayname"]?.toString()?.trim().isNullOrBlank() &&
                            !model["holidayname"]?.toString()?.trim().equals("null", ignoreCase = true) &&
                            model["month"]?.toString()?.trim() != currentMonthStr -> {
                        ContextCompat.getColor(context, R.color.background)
                    }

                    // Case 3: If today’s date and month match
                    model["date"]?.toString() == currentDateStr &&
                            model["month"]?.toString() == currentMonthStr -> {
                        ContextCompat.getColor(context, R.color.green)
                    }

                    // Default case if none of the above match
                    else -> Color.WHITE
                }

                setBackgroundColor(binding, backgroundColor)

            }

            binding.root.setOnClickListener {
                if(model["date"]?.toString()?.isNotEmpty() == true)
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


            val tithidbname="t$year.db"
            val ttable="t$year"
            val nakshatradbname="n$year.db"
            val ntable="n$year"

            val dbHelpertithi=dbHelper(context,tithidbname)
            val dbHelpernakshatra=dbHelper(context,nakshatradbname)
            val (todayDate, todayMonth) = getTodayDateAndMonth()
            val todaystithi=dbHelpertithi.getTithiRowsContainingDate(todayDate,todayMonth,tithidbname,ttable)
            val todaysnakshatra=dbHelpernakshatra.getTithiRowsContainingDate(todayDate,todayMonth,nakshatradbname,ntable)
            Log.d("todaystithi", "Received todaystithi: ${todaystithi}")
            val formattedTextt = todaystithi.joinToString(separator = "\n") { row ->
                val hindiTithi = row["Hindi Tithi"] ?: "N/A"
                val hindiTiming = row["Hindi Timinig"] ?: ""

                // Try to match either प्रारंभ or आरंभ for start time, and always match समाप्ति समय for end time
                val startRegex = Regex("(?:प्रारंभ समय|आरंभ समय):\\s*([^स]+)").find(hindiTiming)
                val endRegex = Regex("समाप्ति समय:\\s*(.+)").find(hindiTiming)

                val startTime = startRegex?.groupValues?.get(1)?.trim() ?: "N/A"
                val endTime = endRegex?.groupValues?.get(1)?.trim() ?: "N/A"

                "$hindiTithi - $startTime से $endTime"
            }
            val formattedTextn = todaysnakshatra.joinToString(separator = "\n") { row ->
                val hindiTithi = row["Hindi Tithi"] ?: "N/A"
                val hindiTiming = row["Hindi Timinig"] ?: ""

                // Try to match either प्रारंभ or आरंभ for start time, and always match समाप्ति समय for end time
                val startRegex = Regex("(?:प्रारंभ समय|आरंभ समय):\\s*([^स]+)").find(hindiTiming)
                val endRegex = Regex("समाप्ति समय:\\s*(.+)").find(hindiTiming)

                val startTime = startRegex?.groupValues?.get(1)?.trim() ?: "N/A"
                val endTime = endRegex?.groupValues?.get(1)?.trim() ?: "N/A"

                "$hindiTithi - $startTime से $endTime"
            }



            val tithiNames = formattedTextt
            val nakshatraNames = formattedTextn


            // Month, Date, Day, and Year
            val monthName = todaysdatedetails["month"]?.toString()
            val monthhindi=TranslationUtils.translateToHindi(monthName.toString())

            val hindimonthName = todaysdatedetails["monthname"]?.toString()

            val translatedMonth = TranslationUtils.translateToHindiDevanagariHinduMonth(hindimonthName ?: "Unknown")
            Log.d("monthh", "$hindimonthName $translatedMonth")

            val day = todaysdatedetails["day"]?.toString()
            val date = todaysdatedetails["date"]?.toString()

            // Rashi and Paksha
            val rashi = TranslationUtils.translateToHindiDevanagariRashi(todaysdatedetails["rashi"].toString())

            val paksha = TranslationUtils.translateToPaksha(todaysdatedetails["paksha"].toString())
            val holidayname = todaysdatedetails["holidayname"].toString()
            val holidayname1 = todaysdatedetails["monthhindi"].toString()

            // Set all values in the dialog
            calendarDialog.setDialogValues(
                date =    "${TranslationUtils.translateToHindidaythree(day.toString())}, $date $monthhindi, $year",

                    tithi = tithiNames,
                    nakshatra = nakshatraNames,
                    month = translatedMonth,
                        rashi = rashi,
                     paksha = paksha,
                holiday= holidayname+"\n"+holidayname1
                )

            calendarDialog.show()
        }

        fun getTodayDateAndMonth(): Pair<String, String> {
            val date = Date()
            val dayFormat = SimpleDateFormat("dd", Locale.ENGLISH) // returns "01", "02", etc.
            val monthFormat = SimpleDateFormat("MMM", Locale.ENGLISH) // returns "Jan", "Feb", etc.

            val day = dayFormat.format(date)
            val month = monthFormat.format(date)

            return Pair(day, month)
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
