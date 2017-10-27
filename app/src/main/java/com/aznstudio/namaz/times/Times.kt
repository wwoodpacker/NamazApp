package com.aznstudio.namaz.pray.times


enum class Times (val time: Double) {
    imsak(5.0),
    fajr(5.0),
    sunrise(6.0),
    dhuhr(12.0),
    asr(13.0),
    sunset(18.0),
    maghrib(18.0),
    isha(18.0)
}