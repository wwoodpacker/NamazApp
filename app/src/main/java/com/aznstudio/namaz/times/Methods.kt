package com.aznstudio.namaz.pray.times

/**
 * Created by SII on 15.06.2017.
 */
//---------------------------- Методы Расчета ----------------------------
enum class Methods(var fajr:Double = 0.0,
                   var isha:Double = 0.0,
                   var maghrib:Double = 0.0,
                   var midnight: String? = "Standard"){
    MWL(18.0, 17.0),                    // Muslim World League
    ISNA(15.0, 15.0),                   // Islamic Society of North America (ISNA)
    Egypt(19.5, 17.5),                  // Egyptian General Authority of Survey
    Makkah(18.5, 1.5),                  // Umm Al-Qura University, Makkah
    Karachi(18.0, 18.0),                // University of Islamic Sciences, Karachi
    Tehran(17.7, 14.0, 4.5, "Jafari"),  // Institute of Geophysics, University of Tehran
    Jafari(16.0, 14.0, 4.0, "Jafari");   // Shia Ithna-Ashari, Leva Institute, Qum
}
//------------------------------------------------------------------------
