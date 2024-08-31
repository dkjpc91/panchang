package com.mithilakshar.mithilapanchang.Utility

import java.util.Locale

object TranslationUtils {

    fun translateToHindimonth(month: String): String {
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
            "DECEMBER" to "दिसंबर",

        )
        return monthTranslation[month.uppercase(Locale.getDefault())] ?: month
    }

    fun translateToHindiday(day: String): String {
        val dayTranslation = mapOf(
            "MONDAY" to "सोमवार",
            "TUESDAY" to "मंगलवार",
            "WEDNESDAY" to "बुधवार",
            "THURSDAY" to "गुरुवार",
            "FRIDAY" to "शुक्रवार",
            "SATURDAY" to "शनिवार",
            "SUNDAY" to "रविवार",
            "AM" to "पूर्वाह्न",
            "PM" to "अपराह्न"

        )
        return dayTranslation[day.uppercase(Locale.getDefault())] ?: day
    }

    fun translateToHindidate(date: String): String {
        val dateTranslation = mapOf(
            "1" to "१", "2" to "२", "3" to "३", "4" to "४", "5" to "५", "6" to "६", "7" to "७",
            "8" to "८", "9" to "९", "10" to "१०", "11" to "११", "12" to "१२", "13" to "१३",
            "14" to "१४", "15" to "१५", "16" to "१६", "17" to "१७", "18" to "१८", "19" to "१९",
            "20" to "२०", "21" to "२१", "22" to "२२", "23" to "२३", "24" to "२४", "25" to "२५",
            "26" to "२६", "27" to "२७", "28" to "२८", "29" to "२९", "30" to "३०", "31" to "३१","2024" to "२०२४",
            "2025" to "२०२५",
            "2026" to "२०२६",
            "2027" to "२०२७",
            "2028" to "२०२८",
            "2029" to "२०२९",
            "2030" to "२०३०","01" to "०१",
            "02" to "०२",
            "03" to "०३",
            "04" to "०४",
            "05" to "०५",
            "06" to "०६",
            "07" to "०७",
            "08" to "०८",
            "09" to "०९",
            "32" to "३२",
            "33" to "३३",
            "34" to "३४",
            "35" to "३५",
            "36" to "३६",
            "37" to "३७",
            "38" to "३८",
            "39" to "३९",
            "40" to "४०",
            "41" to "४१",
            "42" to "४२",
            "43" to "४३",
            "44" to "४४",
            "45" to "४५",
            "46" to "४६",
            "47" to "४७",
            "48" to "४८",
            "49" to "४९",
            "50" to "५०",
            "51" to "५१",
            "52" to "५२",
            "53" to "५३",
            "54" to "५४",
            "55" to "५५",
            "56" to "५६",
            "57" to "५७",
            "58" to "५८",
            "59" to "५९",
            "60" to "६०"
        )
        return dateTranslation[date] ?: date
    }

    fun producttranslate(day: String): String {
        val dayTranslation = mapOf(
            "mithila_panchang_50 (Mithila Panchang)" to "मिथिला पंचांग",
            "mithila_panchang_silver (Mithila Panchang)" to "मिथिला पंचांग",
            "mithila_panchang_100 (Mithila Panchang)" to "मिथिला पंचांग"

        )
        return dayTranslation[day] ?: day
    }
}
