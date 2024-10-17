package com.mithilakshar.mithilapanchang.Utility

import java.util.Locale
import kotlin.math.floor
import kotlin.random.Random

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

    fun translateNumberToNakshatra(number: String): String? {
        val   number=number.toInt()
        val nakshatraTranslation: MutableMap<Int, String> = HashMap()
        nakshatraTranslation[1] = "अश्विनी"        // 1 -> Asvini
        nakshatraTranslation[2] = "भरणी"          // 2 -> Bharani
        nakshatraTranslation[3] = "कृत्तिका"        // 3 -> Krittika
        nakshatraTranslation[4] = "रोहिणी"         // 4 -> Rohini
        nakshatraTranslation[5] = "मृगशिरा"       // 5 -> Mrigasiras
        nakshatraTranslation[6] = "आर्द्रा"        // 6 -> Ardra
        nakshatraTranslation[7] = "पुनर्वसु"       // 7 -> Punarvasu
        nakshatraTranslation[8] = "पुष्य"          // 8 -> Pushya
        nakshatraTranslation[9] = "आश्रेषा"        // 9 -> Aslesha
        nakshatraTranslation[10] = "मघा"          // 10 -> Magha
        nakshatraTranslation[11] = "पूर्वाफाल्गुनी" // 11 -> Purva Phalguni
        nakshatraTranslation[12] = "उत्तराफाल्गुनी" // 12 -> Uttara Phalguni
        nakshatraTranslation[13] = "हस्त"          // 13 -> Hasta
        nakshatraTranslation[14] = "चित्रा"        // 14 -> Chitra
        nakshatraTranslation[15] = "स्वाति"        // 15 -> Svati
        nakshatraTranslation[16] = "विशाखा"        // 16 -> Visakha
        nakshatraTranslation[17] = "अनुराधा"      // 17 -> Anuradha
        nakshatraTranslation[18] = "ज्येष्ठा"      // 18 -> Jyestha
        nakshatraTranslation[19] = "मूल"          // 19 -> Mula
        nakshatraTranslation[20] = "पूर्वाषाढ़ा"   // 20 -> Purva Shadha
        nakshatraTranslation[21] = "उत्तराषाढ़ा"   // 21 -> Uttara Shadha
        nakshatraTranslation[22] = "श्रवण"         // 22 -> Shravana
        nakshatraTranslation[23] = "धनिष्ठा"      // 23 -> Dhanistha
        nakshatraTranslation[24] = "शतभिषा"       // 24 -> Shatabhisha
        nakshatraTranslation[25] = "पूर्वभाद्रपदा" // 25 -> Purva Bhadrapada
        nakshatraTranslation[26] = "उत्तरभाद्रपदा" // 26 -> Uttara Bhadrapada
        nakshatraTranslation[27] = "रेवती"         // 27 -> Revati

        // Return the translated Nakshatra name or null if not found
        return nakshatraTranslation[number]
    }
    fun translateNumberToYoga(number: Int): String? {
        val yogaTranslation: MutableMap<Int, String> = HashMap()
        yogaTranslation[1] = "विष्कुम्भ"         // 1 -> Viskumbha
        yogaTranslation[2] = "प्रिति"             // 2 -> Priti
        yogaTranslation[3] = "आयुष्मान"           // 3 -> Ayusman
        yogaTranslation[4] = "सौभाग्य"           // 4 -> Saubhagya
        yogaTranslation[5] = "शोभना"            // 5 -> Sobhana
        yogaTranslation[6] = "अतिगंड"            // 6 -> Atiganda
        yogaTranslation[7] = "सुखर्मा"           // 7 -> Sukarma
        yogaTranslation[8] = "धृति"              // 8 -> Dhriti
        yogaTranslation[9] = "सूला"              // 9 -> Sula
        yogaTranslation[10] = "गंड"              // 10 -> Ganda
        yogaTranslation[11] = "वृद्धि"            // 11 -> Vriddhi
        yogaTranslation[12] = "ध्रुवा"            // 12 -> Dhruva
        yogaTranslation[13] = "व्याघात"           // 13 -> Vyaghata
        yogaTranslation[14] = "हर्षण"             // 14 -> Harshana
        yogaTranslation[15] = "वज्र"              // 15 -> Vajra
        yogaTranslation[16] = "सिद्धि (अस्रिक)"    // 16 -> Siddhi (Asrik)
        yogaTranslation[17] = "व्यतिपात"         // 17 -> Vyatipata
        yogaTranslation[18] = "वरियान"            // 18 -> Variyan
        yogaTranslation[19] = "परिघ"              // 19 -> Parigha
        yogaTranslation[20] = "शिव"               // 20 -> Siva
        yogaTranslation[21] = "सिद्ध"             // 21 -> Siddha
        yogaTranslation[22] = "साध्य"             // 22 -> Sadhya
        yogaTranslation[23] = "शुभ"              // 23 -> Subha
        yogaTranslation[24] = "सुख्ल (सुक्रा)"     // 24 -> Sukla (Sukra)
        yogaTranslation[25] = "ब्रह्म"            // 25 -> Brahma
        yogaTranslation[26] = "इन्द्र"            // 26 -> Indra
        yogaTranslation[27] = "विधृति"            // 27 -> Vaidhriti

        // Return the translated Yoga name or null if not found
        return yogaTranslation[number]
    }

    fun translateNumberToTithi(number: String): String? {
      val   number=number.toInt()
        val tithiTranslation: MutableMap<Int, String> = HashMap()
        tithiTranslation[1] = "प्रतिपदा"       // 1 -> Pratipada
        tithiTranslation[2] = "द्वितीया"         // 2 -> Dwitiya
        tithiTranslation[3] = "तृतीया"         // 3 -> Tritiya
        tithiTranslation[4] = "चतुर्थी"        // 4 -> Chaturthi
        tithiTranslation[5] = "पञ्चमी"         // 5 -> Panchami
        tithiTranslation[6] = "षष्ठी"          // 6 -> Shashthi
        tithiTranslation[7] = "सप्तमी"         // 7 -> Saptami
        tithiTranslation[8] = "अष्टमी"         // 8 -> Ashtami
        tithiTranslation[9] = "नवमी"           // 9 -> Navami
        tithiTranslation[10] = "दशमी"          // 10 -> Dashami
        tithiTranslation[11] = "एकादशी"        // 11 -> Ekadashi
        tithiTranslation[12] = "द्वादशी"        // 12 -> Dwadashi
        tithiTranslation[13] = "त्रयोदशी "         // 13 -> Tritiya (Repeat)
        tithiTranslation[14] = "चतुर्दशी"       // 14 -> Chaturdashi
        tithiTranslation[15] = "पूर्णिमा"       // 15 -> Purnima
        tithiTranslation[16] = "अमावस्या"      // 16 -> Amavasya

        // Return the translated Tithi name or null if not found
        return tithiTranslation[number]
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




    fun formatTime(hours: Int, minutes: Double): String {
        // Wrap hours around to 24-hour format
        val validHours = hours % 24

        // Calculate the actual minutes and fractional seconds
        val minutePart = floor(minutes).toInt()
        val secondsPart = ((minutes - floor(minutes)) * 60).toInt()

        // Adjust hours for AM/PM format
        val period = if (validHours >= 12) "अपराहअन" else "पूर्वाहअन"
        val adjustedHours = if (validHours == 0) 12 else if (validHours > 12) validHours - 12 else validHours

        // Format the time as "<hours> baje ke <minutes> minute aur <seconds> sec <AM/PM>"
        return String.format("  %s,  %2d बाइज   क  %02d  मिनट  आर  %02d सेकंड  अई  ", period, adjustedHours, minutePart, secondsPart )
    }




    fun speakFunction(
        month: String,
        date: String,
        day: String,
        year: String,
        tithi: String,
        tithiEndH: String,
        tithiEndM: String,
        nakshatra: String,
        nakshatraEndH: String,
        monthName: String,
        rashi: String,
        paksha: String
    ): String {
        val sentences = listOf(
            "आई, दिनांक ${date}. ${month} ${year}. दिन  ${translateAbbreviatedDayToHindi(day)}, तिथि  ${translateNumberToTithi(tithi)}, तिथि समाप्ति समय , ${formatTime(tithiEndH.toInt(),tithiEndM.toDouble())} , ${monthName} मास. ${translateToPaksha(paksha)}. ${translateNumberToNakshatra(nakshatra)} नक्षत्र , $rashi  राशि अइछ। मिथिला पंचांग उपयोग करबाक लेल धन्यवाद।",
 /*           "आई, ${day} दिन ${date} तारीख, ${month} ${year} अइछ। ${tithi} तिथि, ${nakshatra} नक्षत्र आ ${paksha} पक्ष अछि। अहाँक दिन शुभ हूयै!",
            "आई, ${day} दिन ${date} तारीख, ${month} ${year} अइछ। ${tithi} तिथि आ ${nakshatra} नक्षत्र अछि।   कृपया ,  ई ऐप शेयर करू।",
            "${monthName} महिनाक ${date} तारीख , ${day} दिन , ${nakshatra} नक्षत्र आ ${paksha} पक्ष अइछ,  अहाँक दिन मंगलमय हो!",*/

        )

        return sentences[Random.nextInt(sentences.size)]
    }



}
