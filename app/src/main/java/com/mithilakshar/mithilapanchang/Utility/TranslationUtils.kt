package com.mithilakshar.mithilapanchang.Utility

import android.util.Log
import java.util.Locale
import kotlin.math.floor

object TranslationUtils {

     fun translateToHindi(currentMonth: String): String? {
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

    fun translateToHindiDevanagariHinduMonth(currentMonth: String): String? {
        // Manually create a mapping for Hindi Hindu month names to Devanagari
        val monthTranslation: MutableMap<String, String> = HashMap()
        monthTranslation["Paush"] = "पौष"         // Paush
        monthTranslation["Magh"] = "माघ"           // Magh
        monthTranslation["Phalgun"] = "फाल्गुन"    // Phalgun
        monthTranslation["Chaitra"] = "चैत"        // Chaitra
        monthTranslation["Vaishakh"] = "वैशाख"     // Vaishakh
        monthTranslation["Jyeshtha"] = "ज्येष्ठ"   // Jyeshtha
        monthTranslation["Ashadha"] = "आषाढ़"     // Ashadha
        monthTranslation["Shravana"] = "श्रावण"     // Shravana
        monthTranslation["Bhadav"] = "भाद्रपद" // Bhadrapada
        monthTranslation["Ashwin"] = "आश्विन"      // Ashwin
        monthTranslation["Kartik"] = "कार्तिक"     // Kartik
        monthTranslation["Aghan"] = "मार्गशीर्ष" // Margashirsha

        // Return the translated month name
        return monthTranslation[currentMonth]
    }

    fun translateToHindiDevanagariRashi(currentRashi: String): String? {
        // Manually create a mapping for Hindu Rashi names to Devanagari
        val rashiTranslation: MutableMap<String, String> = HashMap()
        rashiTranslation["makar"] = "मकर"          // Capricorn
        rashiTranslation["kumbh"] = "कुंभ"         // Aquarius
        rashiTranslation["meen"] = "मीन"           // Pisces
        rashiTranslation["mesh"] = "मेष"          // Aries
        rashiTranslation["vrish"] = "वृष"          // Taurus
        rashiTranslation["mithun"] = "मिथुन"       // Gemini
        rashiTranslation["karkata"] = "कर्क"          // Cancer
        rashiTranslation["singh"] = "सिंह"         // Leo
        rashiTranslation["kanya"] = "कन्या"        // Virgo
        rashiTranslation["tula"] = "तुला"          // Libra
        rashiTranslation["vrischika"] = "वृश्चिक"  // Scorpio
        rashiTranslation["dhanus"] = "धनु"         // Sagittarius

        // Return the translated Rashi name
        return rashiTranslation[currentRashi]
    }


    fun translateTomonthnumber(currentMonth: String): String? {
        // Manually create a mapping for English to Hindi month names
        val monthTranslation: MutableMap<String, String> = HashMap()
        monthTranslation["1"] = "JANUARY"
        monthTranslation["2"] = "FEBRUARY"
        monthTranslation["3"] = "MARCH"
        monthTranslation["4"] = "APRIL"
        monthTranslation["5"] = "MAY"
        monthTranslation["6"] = "JUNE"
        monthTranslation["7"] = "JULY"
        monthTranslation["8"] = "AUGUST"
        monthTranslation["9"] = "SEPTEMBER"
        monthTranslation["10"] = "OCTOBER"
        monthTranslation["11"] = "NOVEMBER"
        monthTranslation["12"] = "DECEMBER"
        // Return the translated month name
        return monthTranslation[currentMonth]
    }


     fun translateToHindiday(currentDay: String): String? {
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

    fun translateToHindidaythree(currentDay: String): String? {
        // Manually create a mapping for English to Hindi month names
        val monthTranslation: MutableMap<String, String> = HashMap()
        monthTranslation["Mon"] = "सोमवार"
        monthTranslation["Tue"] = "मंगलवार"
        monthTranslation["Wed"] = "बुधवार"
        monthTranslation["Thu"] = "गुरुवार"
        monthTranslation["Fri"] = "शुक्रवार"
        monthTranslation["Sat"] = "शनिवार"
        monthTranslation["Sun"] = "रविवार"
        // Return the translated month name
        return monthTranslation[currentDay]
    }

     fun translateToHindidate(date: String): String? {
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
        // Return the translated month name
        return nmap[date]
    }


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

    fun translateToPaksha(input: String): String? {
        val pakshaTranslation: MutableMap<String, String> = HashMap()
        pakshaTranslation["K"] = "कृष्ण पक्ष"  // K translates to Krishna Paksha
        pakshaTranslation["S"] = "शुक्ल पक्ष"   // S translates to Shukla Paksha
        pakshaTranslation["k"] = "कृष्ण पक्ष"  // K translates to Krishna Paksha
        pakshaTranslation["s"] = "शुक्ल पक्ष"   // S translates to Shukla Paksha
        // Return the translated Paksha name or null if not found
        return pakshaTranslation[input.uppercase(Locale.getDefault())]
    }



    fun translateAbbreviatedDayToHindi(dayAbbreviation: String): String? {
        val dayTranslation: MutableMap<String, String> = HashMap()
        dayTranslation["MON"] = "सोमवार"     // Mon -> Somvar
        dayTranslation["TUE"] = "मंगलवार"    // Tue -> Mangalvar
        dayTranslation["WED"] = "बुधवार"      // Wed -> Budhvar
        dayTranslation["THU"] = "गुरुवार"     // Thu -> Guruvaar
        dayTranslation["FRI"] = "शुक्रवार"    // Fri -> Shukravar
        dayTranslation["SAT"] = "शनिवार"      // Sat -> Shanivar
        dayTranslation["SUN"] = "रविवार"      // Sun -> Ravivaar

        // Return the translated day name or null if not found
        return dayTranslation[dayAbbreviation.uppercase(Locale.getDefault())]
    }






    fun speakFunction(
        formattedTextt: String,
        month: String,
        date: String,
        day: String,
        monthName: String,
        rashi: String,
        paksha: String,
        hindiDate: String,
        holidayName: String,
        holidayDesc: String,
    ): String {

        Log.d("SpeakFunction", "formattedTextt: $formattedTextt")
        Log.d("SpeakFunction", "month: $month")
        Log.d("SpeakFunction", "date: $date")
        Log.d("SpeakFunction", "day: $day")
        Log.d("SpeakFunction", "monthName: $monthName")
        Log.d("SpeakFunction", "rashi: $rashi")
        Log.d("SpeakFunction", "paksha: $paksha")
        Log.d("SpeakFunction", "hindiDate: $hindiDate")
        Log.d("SpeakFunction", "holidayName: $holidayName")
        Log.d("SpeakFunction", "holidayDesc: $holidayDesc")

        val hindimonth=translateToHindiDevanagariHinduMonth (monthName)
        val pakshahindi= translateToPaksha (paksha)
        val hindiday=translateAbbreviatedDayToHindi (day.toUpperCase())
        val hindirashi= translateToHindiDevanagariRashi (rashi)
        val tithispeak=convertPanchangTextToMaithili(formattedTextt)

        Log.d("SpeakFunction", "tithispeak: $tithispeak")

        return "आई ${hindiDate}, दिन ${hindiday}, तिथि  ${tithispeak} ,  ${hindimonth} महीना, ${pakshahindi}  , ${hindirashi}  राशि अईछ।  मिथिला पंचांग प्रयोग के लेल धन्यवाद"
    }





    fun formatTimeD(hours: Int, minutes: Double): String {
        // Wrap hours around to 48-hour format
        val validHours = hours % 12

        // Calculate the actual minutes and fractional seconds
        val minutePart = floor(minutes).toInt()

        // Adjust period based on the new rules
        val period = when {
            validHours < 12 -> "पूर्वाहन" // Purvahan for 0-12
            validHours < 24 -> "अपराहन" // Aprahan for 12-24
            validHours < 36 -> "पूर्वाहन" // Purvahan for 24-36
            validHours < 48 -> "अपराहन" // Aprahan for 36-48
            else -> "पूर्वाहन" // Purvahan for 48+
        }

        // Adjust hours for AM/PM format
        val adjustedHours = when {
            validHours == 0 -> 12
            validHours > 12 -> validHours - 12
            else -> validHours
        }

        // Format the time as "<period>, <hours> baje ke <minutes> minute"
        return String.format("%s, %2d:%02d", period, adjustedHours, minutePart)
    }




    // Helper function to handle `tithi` as either single or multiple values
    fun parseTithiInput(tithi: String): List<String>? {
        val tithiTranslation = mapOf(
            1 to "प्रतिपदा", 2 to "द्वितीया", 3 to "तृतीया", 4 to "चतुर्थी",
            5 to "पञ्चमी", 6 to "षष्ठी", 7 to "सप्तमी", 8 to "अष्टमी",
            9 to "नवमी", 10 to "दशमी", 11 to "एकादशी", 12 to "द्वादशी",
            13 to "त्रयोदशी", 14 to "चतुर्दशी", 15 to "पूर्णिमा", 30 to "अमावस्या"
        )

        return try {
            if (tithi.startsWith("[") && tithi.endsWith("]")) {
                tithi.removeSurrounding("[", "]")
                    .split(", ")
                    .mapNotNull { it.toIntOrNull()?.let(tithiTranslation::get) }
            } else {
                listOfNotNull(tithi.toIntOrNull()?.let(tithiTranslation::get))
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Helper function to handle `nakshatra` as either single or multiple values
    fun parseNakshatraInput(nakshatra: String): List<String>? {
        val nakshatraTranslation = mapOf(
            1 to "अश्विनी", 2 to "भरणी", 3 to "कृत्तिका", 4 to "रोहिणी",
            5 to "मृगशीर्ष", 6 to "आर्द्रा", 7 to "पुनर्वसु", 8 to "पुष्य",
            9 to "अश्लेषा", 10 to "मघा", 11 to "पूर्वा फाल्गुनी", 12 to "उत्तर फाल्गुनी",
            13 to "हस्त", 14 to "चित्रा", 15 to "स्वाति", 16 to "विशाखा",
            17 to "अनुराधा", 18 to "ज्येष्ठा", 19 to "मूल", 20 to "पूर्वाषाढ़ा",
            21 to "उत्तराषाढ़ा", 22 to "श्रवण", 23 to "धनिष्ठा", 24 to "शतभिषा",
            25 to "पूर्वा भाद्रपद", 26 to "उत्तर भाद्रपद", 27 to "रेवती"
        )

        return try {
            if (nakshatra.startsWith("[") && nakshatra.endsWith("]")) {
                nakshatra.removeSurrounding("[", "]")
                    .split(", ")
                    .mapNotNull { it.toIntOrNull()?.let(nakshatraTranslation::get) }
            } else {
                listOfNotNull(nakshatra.toIntOrNull()?.let(nakshatraTranslation::get))
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Helper function to parse hours as integers
    fun parseIntListInput(input: String): List<Int>? {
        return try {
            if (input.startsWith("[") && input.endsWith("]")) {
                input.removeSurrounding("[", "]")
                    .split(", ")
                    .mapNotNull { it.toIntOrNull() }
            } else {
                listOfNotNull(input.toIntOrNull())
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Helper function to parse minutes as doubles
    fun parseDoubleListInput(input: String): List<Double>? {
        return try {
            if (input.startsWith("[") && input.endsWith("]")) {
                input.removeSurrounding("[", "]")
                    .split(", ")
                    .mapNotNull { it.toDoubleOrNull() }
            } else {
                listOfNotNull(input.toDoubleOrNull())
            }
        } catch (e: NumberFormatException) {
            null
        }
    }



    fun createTithitimeformat(tithiEndH: String, tithiEndM: String): String {
        val tithiEndHList = parseIntListInput(tithiEndH) ?: return "Invalid end hours input"
        val tithiEndMList = parseDoubleListInput(tithiEndM) ?: return "Invalid end minutes input"

        // Generate a message using the parsed hours and minutes
        return tithiEndHList.mapIndexed { index, endHour ->
            val endMinute = if (index < tithiEndMList.size) tithiEndMList[index] else 0.0
            val formattedTime = formatTimeD(endHour, endMinute)
            "$formattedTime"
        }.joinToString(" एवं  ")
    }

    fun parseYogaInput(yoga: String): List<String>? {
        val yogaTranslation = mapOf(
            1 to "विष्कुम्भ", 2 to "प्रिति", 3 to "आयुष्मान", 4 to "सौभाग्य",
            5 to "शोभना", 6 to "अतिगंड", 7 to "सुखर्मा", 8 to "धृति",
            9 to "सूला", 10 to "गंड", 11 to "वृद्धि", 12 to "ध्रुवा",
            13 to "व्याघात", 14 to "हर्षण", 15 to "वज्र", 16 to "सिद्धि (अस्रिक)",
            17 to "व्यतिपात", 18 to "वरियान", 19 to "परिघ", 20 to "शिव",
            21 to "सिद्ध", 22 to "साध्य", 23 to "शुभ", 24 to "सुख्ल (सुक्रा)",
            25 to "ब्रह्म", 26 to "इन्द्र", 27 to "विधृति"
        )

        return try {
            if (yoga.startsWith("[") && yoga.endsWith("]")) {
                yoga.removeSurrounding("[", "]")
                    .split(", ")
                    .mapNotNull { it.toIntOrNull()?.let(yogaTranslation::get) }
            } else {
                listOfNotNull(yoga.toIntOrNull()?.let(yogaTranslation::get))
            }
        } catch (e: NumberFormatException) {
            null
        }
    }


    fun convertPanchangTextToMaithili(text: String): String {
        val dayMap = mapOf(
            "सोमवार" to "सोमदिन",
            "मंगलवार" to "मंगलदिन",
            "बुधवार" to "बुधदिन",
            "गुरुवार" to "बृहस्पतिदिन",
            "शुक्रवार" to "शुक्रदिन",
            "शनिवार" to "शनिदिन",
            "रविवार" to "रविदिन"
        )

        val resultBuilder = StringBuilder()
        val lines = text.split("\n")

        for (line in lines) {
            val parts = line.split(" - ")
            if (parts.size == 2) {
                val tithi = parts[0].trim()
                val timeParts = parts[1].split(" से ")
                if (timeParts.size == 2) {
                    val start = timeParts[0].trim()
                    val end = timeParts[1].trim()

                    // Separate the day from the datetime using first comma
                    val startCommaIndex = start.indexOf(",")
                    val endCommaIndex = end.indexOf(",")

                    if (startCommaIndex != -1 && endCommaIndex != -1) {
                        val startDay = start.substring(0, startCommaIndex).trim()
                        val startDateTime = start.substring(startCommaIndex + 1).trim()

                        val endDay = end.substring(0, endCommaIndex).trim()
                        val endDateTime = end.substring(endCommaIndex + 1).trim()

                        val startFormatted = formatDateTime(startDateTime)
                        val endFormatted = formatDateTime(endDateTime)

                        val startDayMaithili = dayMap[startDay] ?: startDay
                        val endDayMaithili = dayMap[endDay] ?: endDay

                        resultBuilder.append(" $tithi, तिथि समय ,$startFormatted स, $startDayMaithili, $endFormatted तक.\n")
                    }
                }
            }
        }

        return resultBuilder.toString().trim()
    }

    fun formatDateTime(dateTimeStr: String): String {
        // Example: "11 अप्रैल 2025, 01:01 पूर्वाह्न"
        val parts = dateTimeStr.split(" ")
        if (parts.size >= 5) {
            val day = parts[0]
            val month = parts[1]
            val year = parts[2].replace(",", "")
            val time = parts[3]
            val meridiem = parts[4]

            val (hour, minute) = time.split(":")
            val meridiemMaithili = if (meridiem.contains("पूर्वाह्न")) "पूर्वाह्न" else "अपराह्न"

            return "$day ,$month, $year, ${hour.trim()} : ${minute.trim()} $meridiemMaithili"
        }
        return dateTimeStr
    }



}
