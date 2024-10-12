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
import com.mithilakshar.mithilapanchang.Dialog.calendardialog
import com.mithilakshar.mithilapanchang.R
import com.mithilakshar.mithilapanchang.databinding.CalendardayitemBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


class CalendarAdapter(
    private val data: List<Map<String, Any?>>,
    private val context: Context,
    private val year: Int
) : RecyclerView.Adapter<CalendarAdapter.calendarviewholder>() {

    class calendarviewholder(val binding: CalendardayitemBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(model: Map<String, Any?>, context: Context, year: Int) {
            binding.apply {

                val currentDateStr = getCurrentDateString()
                val currentDateStrmonth = getCurrentMonthName()
                Log.d("date", "$currentDateStr")
                Log.d("date", " date sustem $currentDateStrmonth")
                Log.d("date", "date mdoel ${model.get("month").toString()}")


                calendardateText.text = translateToHindidate(model.get("date").toString())
                calendardayText.text = model.get("tithi").toString()
                calendardescText.text = model.get("monthhindi").toString()

                when {
                    // Check if holiday is not blank
                    model.get("holiday").toString().isNotEmpty() && (model.get("date").toString() != currentDateStr)-> {
                        // Set the background color to white
                        val redcolor = ContextCompat.getColor(context, R.color.background)
                        setbackgroundcolor(binding, redcolor)
                    }

                    model.get("holiday").toString().isNotEmpty() && (model.get("month").toString() != currentDateStrmonth) -> {
                        // Set the background color to white
                        val redcolor = ContextCompat.getColor(context, R.color.background)
                        setbackgroundcolor(binding, redcolor)
                    }
                    // Check if the date and month match
                    (model.get("date").toString() == currentDateStr) && (model.get("month").toString() == currentDateStrmonth) -> {
                        // Set the background color to green
                        val greenColor = ContextCompat.getColor(context, R.color.green)
                        setbackgroundcolor(binding, greenColor)
                    }
                    else -> {
                        // No match for date, month, or holiday - set the default background color
                        binding.root.setBackgroundColor(Color.WHITE)
                    }
                }


            }

            binding.root.setOnClickListener {

                val calendarDialog = calendardialog(context)
                val currentDate = LocalDate.now()

                val hindiMonth = translateToHindi(model.get("month").toString())

                val hindidate = translateToHindidate(model.get("date").toString())

                val hindiyear = translateToHindidate(year.toString())

                calendarDialog.setcalendardialogtext(
                    "\nपंचांग विवरण :-\n \n"
                            + hindidate + " " + hindiMonth + " " + hindiyear
                )
                calendarDialog.setcalendardialogtext1(model.get("tithi").toString())
                calendarDialog.setcalendardialogtext2( "तारीख "+ hindidate + " " + hindiMonth + " " + hindiyear+ ","+
                        "तिथि "+ model.get("tithi")+ ","+model.get("nakshatra")+ "नक्षत्र  "+","+model.get("monthhindi")+ "मास  "+","+ model.get("paksha")+ " ")
                calendarDialog.setcalendardialogtext3(model.get("nakshatra").toString())


                calendarDialog.show()
            }

        }

        private fun setbackgroundcolor(binding: CalendardayitemBinding, color: Int) {
            binding.calendardateText.setBackgroundColor(color)
            binding.calendardayText.setBackgroundColor(color)
            binding.calendardescText.setBackgroundColor(color)
        }


        private fun getCurrentDateString(): String {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            return dateFormat.format(Calendar.getInstance().time)
        }


        private fun getCurrentMonthName(): String {
            val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            return dateFormat.format(Calendar.getInstance().time).uppercase(Locale.getDefault())
        }


    }



        private var itemWidth = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): calendarviewholder {

            val binding=CalendardayitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

            val layoutParams = binding.root.layoutParams
            layoutParams.width = itemWidth
            binding.root.layoutParams = layoutParams

            return calendarviewholder(binding)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: calendarviewholder, position: Int) {
            val currentdata=data.get(position)

            if (currentdata != null) {
                holder.bind(currentdata,context,year)
            }

        }

        fun setItemWidth(itemWidth: Int) {
            this.itemWidth = itemWidth
            notifyDataSetChanged() // Refresh the adapter to apply the new item width
        }

        override fun getItemCount(): Int {
            return data.size
        }


    }

private fun translateToHindi(currentMonth: String): String? {
    // Manually create a mapping for English to Hindi month names
    val monthTranslation: MutableMap<String, String> = HashMap()
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


private fun translateToHindiday(currentDay: String): String? {
    // Manually create a mapping for English to Hindi month names
    val monthTranslation: MutableMap<String, String> = HashMap()
    monthTranslation["MONDAY"] = "सोमवार"
    monthTranslation["TUESDAY"] = "मंगलवार"
    monthTranslation["WEDNESDAY"] = "बुधवार"
    monthTranslation["THURSDAY"] = "गुरुवार"
    monthTranslation["FRIDAY"] = "शुक्रवार"
    monthTranslation["SATURDAY"] = "शनिवार"
    monthTranslation["SUNDAY"] = "रविवार"
    // Return the translated month name
    return monthTranslation[currentDay]
}

private fun translateToHindidate(date: String): String? {
    // Manually create a mapping for English to Hindi month names
    val nmap: MutableMap<String, String> = HashMap()
    nmap["1"] = "१"
    nmap["2"] = "२"
    nmap["3"] = "३"
    nmap["4"] = "४"
    nmap["5"] = "५"
    nmap["6"] = "६"
    nmap["7"] = "७"
    nmap["8"] = "८"
    nmap["9"] = "९"
    nmap["10"] = "१०"
    nmap["11"] = "११"
    nmap["12"] = "१२"
    nmap["13"] = "१३"
    nmap["14"] = "१४"
    nmap["15"] = "१५"
    nmap["16"] = "१६"
    nmap["17"] = "१७"
    nmap["18"] = "१८"
    nmap["19"] = "१९"
    nmap["20"] = "२०"
    nmap["21"] = "२१"
    nmap["22"] = "२२"
    nmap["23"] = "२३"
    nmap["24"] = "२४"
    nmap["25"] = "२५"
    nmap["26"] = "२६"
    nmap["27"] = "२७"
    nmap["28"] = "२८"
    nmap["29"] = "२९"
    nmap["30"] = "३०"
    nmap["31"] = "३१"
    nmap["2024"] = "२०२४"
    nmap["2025"] = "२०२५"
    nmap["2026"] = "२०२६"
    nmap["2027"] = "२०२७"
    nmap["2028"] = "२०२८"
    nmap["2029"] = "२०२९"
    nmap["2030"] = "२०३०"
    // Return the translated month name
    return nmap[date]




}





