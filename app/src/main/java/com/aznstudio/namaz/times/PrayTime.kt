package com.aznstudio.namaz.pray.times

import java.util.*


/**
 * Created by SII on 11.06.2017.
 */
class PrayTime {


    //------------------------------------------------------------------------
    //------------------------- По Умолчанию Настройки -----------------------
    //------------------------------------------------------------------------
    var calcMethod = Methods.MWL // метод вычисления
    val numIterations: Int = 1 // количество итераций, необходимых для вычисления времени
    var asrFactor = AsrFactor.Standard.asrParam // метод вычисления Аср
    var highLats = true // высокие широты
    var midnight = calcMethod.midnight
    var timeFormat24 = true
    var imsakMinute: Double? = null    // imsak в минутах
    var ishaMinute: Double? = null    // isha в минутах
    var maghribMinute: Double? = null   // maghrib в минутах
    var highLatsSetting = "NightMiddle"
    val offsets = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    //------------------------------------------------------------------------
    //------------------------- Инициализация --------------------------------
    //------------------------------------------------------------------------

    init {

    }

    //------------------------------------------------------------------------
    //------------------ Локальные переменные --------------------------------
    //------------------------------------------------------------------------


    var lat: Double = 0.0       // широта
    var lng: Double = 0.0       // долгота
    var elv: Double = 0.0       // высота
    var timeZone: Int = 0        // часовой пояс
    var jDate: Double = 0.0      // Юлианский день



    //------------------------------------------------------------------------
    //------------------ Публичные функции -----------------------------------
    //------------------------------------------------------------------------


    //-------------- Возвращение время молитв на заданную дату ---------------

    fun getTimes(date: IntArray, coords: DoubleArray, timezone: Int, dst: Int, calcMethod:Methods): ArrayList<String> {

        lat = 1 * coords[0]
        lng = 1 * coords[1]
        elv = if (coords[2] != 0.0) coords[2] else 0.0

        if (calcMethod == Methods.Makkah) ishaMinute = 90.0
        //if (timezone == null) date.get(IslamCalendar.ZONE_OFFSET) // получаем часовой пояс
        //if (dst == null) date.get(IslamCalendar.DST_OFFSET) // провереям переход на летнее время
        timeZone = timezone + 1 * dst
        jDate = julian(date[0], date[1], date[2]) - lng / (15 * 24)

        return computeTimes()
    }



    //------------------------------------------------------------------------
    //-------------------- ФУНКЦИИ РАСЧЕТА ВРЕМЕНИ НАМАЗА --------------------
    //------------------------------------------------------------------------


    //region------ преобразование Григорианского дня в Юлианский день --------------
    fun julian(year: Int, month: Int, day: Int): Double {

        var year = year
        var month = month

        if (month <= 2) {
            year -= 1
            month += 12
        }

        val A = Math.floor(year / 100.0)

        val B = 2 - A + Math.floor(A / 4.0)

        val JD = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + day + B - 1524.5

        return JD
    }

    fun julianV2(year: Int, month: Int, day: Int): Double {

        var a = Math.floor((14.0 - month) / 12)
        var y = year + 4800 - a
        var m = month + 12 * a - 3

        var JD = day + Math.floor((153 * m + 2) / 5) + 365 * y + Math.floor(y / 4) - Math.floor(y / 100) + Math.floor(y / 400) - 32045

        return JD
    }


    //endregion




    //----------------------- вычисление времени намаза ----------------------
    private fun computeTimes(): ArrayList<String> {
    // настройка времени по умолчанию
        var times = doubleArrayOf(
                Times.imsak.time,   // 0 - imsak
                Times.fajr.time,    // 1 - fajr
                Times.sunrise.time, // 2 - sunrise
                Times.dhuhr.time,   // 3 - dhuhr
                Times.asr.time,     // 4 - asr
                Times.sunset.time,  // 5 - sunset
                Times.maghrib.time, // 6 - maghrib
                Times.isha.time     // 7 - isha
                )
        //if (calcMethod.fajr != 0.0) times[2] = calcMethod.fajr
        //if (calcMethod.isha != 0.0) times[7] = calcMethod.isha
        //if (calcMethod.maghrib != 0.0) times[6] = calcMethod.maghrib

        for (i in 1..numIterations) times = computePrayerTimes(times)

        times = adjustTimes(times)

        // добавить полночь время
        var midnight =
                if (midnight == "Jafari") times[5] + timeDiff(times[5], times[1]) / 2
                else times[5] + timeDiff(times[5], times[2]) / 2
        times = tuneTimes(times)

        return modifyFormats(times)
    }
    //------------------------------------------------------------------------

    //-------------- преобразовать время в заданный формат времени -----------
    private fun  modifyFormats(times: DoubleArray): ArrayList<String> {

        val result = ArrayList<String>()
            if (!timeFormat24)
                for (i in 0..7) result.add(floatToTime12(times[i], false))
            else
                for (i in 0..7) result.add(floatToTime24(times[i]))

        return result
    }
    //------------------------------------------------------------------------

    //-------------- преобразование двойных часов в формате 24ч --------------
    private fun  floatToTime24(time: Double): String {
        var time = time

        val result: String

        time = fixHour(time + 0.5 / 60.0) // add 0.5 minutes to round
        val hours = Math.floor(time).toInt()
        val minutes = Math.floor((time - hours) * 60.0)

        if (hours in 0..9 && minutes >= 0 && minutes <= 9) {
            result = "0" + hours + ":0" + Math.round(minutes)
        } else if (hours in 0..9) {
            result = "0" + hours + ":" + Math.round(minutes)
        } else if (minutes in 0..9) {
            result = hours.toString() + ":0" + Math.round(minutes)
        } else {
            result = hours.toString() + ":" + Math.round(minutes)
        }
        return result
    }
    //------------------------------------------------------------------------

    //-------------- преобразование двойных часов в формате 12ч --------------
    private fun  floatToTime12(time: Double, noSuffix: Boolean): String {
        var time = time
        time = fixHour(time + 0.5 / 60) // add 0.5 minutes to round
        var hours = Math.floor(time).toInt()
        val minutes = Math.floor((time - hours) * 60)
        val suffix: String
        val result: String
        if (hours >= 12) {
            suffix = "pm"
        } else {
            suffix = "am"
        }
        hours = (hours + 12 - 1) % 12 + 1
        /*hours = (hours + 12) - 1;
        int hrs = (int) hours % 12;
        hrs += 1;*/
        if (!noSuffix) {
            if (hours in 0..9 && minutes >= 0 && minutes <= 9) {
                result = "0" + hours + ":0" + Math.round(minutes) + " " +suffix
            } else if (hours in 0..9) {
                result = "0" + hours + ":" + Math.round(minutes) + " " + suffix
            } else if (minutes in 0..9) {
                result = hours.toString() + ":0" + Math.round(minutes) + " " + suffix
            } else {
                result = hours.toString() + ":" + Math.round(minutes) + " " + suffix
            }

        } else {
            if (hours in 0..9 && minutes >= 0 && minutes <= 9) {
                result = "0" + hours + ":0" + Math.round(minutes)
            } else if (hours in 0..9) {
                result = "0" + hours + ":" + Math.round(minutes)
            } else if (minutes in 0..9) {
                result = hours.toString() + ":0" + Math.round(minutes)
            } else {
                result = hours.toString() + ":" + Math.round(minutes)
            }
        }
        return result
    }
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    /*private fun  getFormattedTime(time: Double, timeFormat24: Boolean): Double {
        var time = time
        time = fixHour(time + 0.5 / 60.0) // add 0.5 minutes to round
        val hours = Math.floor(time).toInt()
        val minutes = Math.floor((time - hours) * 60.0)
    }*/
    //------------------------------------------------------------------------

    //--------------------------- Применяем смещение -------------------------
    private fun  tuneTimes(times: DoubleArray): DoubleArray {
        for (i in 0..7)
            times[i] += offsets[i] / 60
        return times
    }
    //------------------------------------------------------------------------


    //----------------------- регулирование времени --------------------------
    private fun  adjustTimes(times: DoubleArray): DoubleArray {
        var t = times
        for (i in 0..7) times[i] += timeZone - lng / 15
        if (highLats) t = adjustHighLats(times)

        if (imsakMinute != null) times[0] = times[1] - imsakMinute!! / 60.0
        if (maghribMinute != null) times[6] = times[5] - maghribMinute!! / 60.0
        if (ishaMinute != null) times[7] = times[6] - ishaMinute!! / 60.0
        return t
    }
    //------------------------------------------------------------------------

    //--------------- регулирование времени на высоких широтах ---------------
    private fun  adjustHighLats(times: DoubleArray): DoubleArray {
        var nightTime = timeDiff(times[5], times[2])
        times[0] = adjustHLTime(times[0], times[2], imsakMinute!!, nightTime, "ccw")
        times[1] = adjustHLTime(times[1], times[2], Times.fajr.time, nightTime, "ccw")
        times[7] = adjustHLTime(times[7], times[5], Times.isha.time, nightTime)
        times[6] = adjustHLTime(times[6], times[5], Times.maghrib.time, nightTime)
        return times
    }
    //------------------------------------------------------------------------

    //------------------ настроить время на высоких широтах ------------------
    private fun  adjustHLTime(time: Double, base: Double, angle: Double, night: Double, direction: String? = ""): Double {
        var time = time
        var portion = nightPortion(angle, night)
        var timeDiff = if (direction == "ccw") timeDiff(time, base) else timeDiff(base, time)
        if (java.lang.Double.isNaN(time) || timeDiff > portion)
            if (direction == "ccw") time = base - portion else time = base + portion
        return time
    }
    //------------------------------------------------------------------------

    //------- ночь используется для регулировки в более высоких широтах ------
    private fun  nightPortion(angle: Double, night: Double): Double {
        var portion = 1.0 / 2.0
        if (highLatsSetting == "AngleBased")
            portion = 1.0 / 60.0 * angle
        if (highLatsSetting == "OneSeventh")
            portion = 1.0 / 7.0
        return portion * night
    }
    //------------------------------------------------------------------------

    //----------- вычислить разницу между двумя значениями времени -----------
    private fun  timeDiff(time1: Double, time2: Double):Double {return fixHour(time2 - time1)}
    //------------------------------------------------------------------------

    //------------------------ расчет середины дня ---------------------------
    private fun midDay(time: Double): Double{
        var eqt = sunPosition(jDate + time)[1]
        var noon = fixHour(12 - eqt)
        return noon
    }
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    private fun computePrayerTimes(times: DoubleArray): DoubleArray {

        val times = this.dayPortion(times)

        var imsak = sunAngleTime(10.0, times[0], "ccw") // угол для imsak 10 мин
        var fajr = sunAngleTime(calcMethod.fajr, times[1], "ccw")
        var sunrise = sunAngleTime(riseSetAngle(), times[2], "ccw")
        var dhuhr = midDay(times[3])
        var asr = asrTime(asrFactor, times[4])
        var sunset = sunAngleTime(riseSetAngle(), times[5])
        var maghrib = sunAngleTime(0.0, times[6])
        var isha = sunAngleTime(calcMethod.isha, times[7])

        var getTimes = doubleArrayOf(imsak,fajr,sunrise,dhuhr,asr,sunset,maghrib,isha)
        return getTimes

    }
    //------------------------------------------------------------------------

    //---------------------- рассчет времени АСР------------------------------
    private fun  asrTime(factor: Double, time: Double): Double {
        var decl = this.sunPosition(jDate + time)[0]
        var angle = -arccot(factor + tan(Math.abs(lat - decl)))
        return sunAngleTime(angle, time)
    }
    //------------------------------------------------------------------------

    //---------------- возвращает угол солнце на закат/Восход ----------------
    private fun  riseSetAngle(): Double {
        var angle = 0.0347 * Math.sqrt(elv)
        return 0.833 + angle
    }
    //------------------------------------------------------------------------

    // вычисляем время, за которое солнце достигает определенный угол ниже горизонта
    private fun  sunAngleTime(angle:Double, time:Double, direction:String?=""): Double{
        var decl = sunPosition(jDate + time)[0]
        var noon = midDay(time)
        var t = (1.0 / 15.0) * arccos((-sin(angle) - sin(decl) * sin(lat)) / (cos(decl) * cos(lat)))
        return noon + if (direction == "ccw") - t else t
    }
    //------------------------------------------------------------------------

    // вычисляем угол склонения солнца и уравнения времени
    private fun  sunPosition(jd: Double): DoubleArray{
        var D = jd - 2451545.0
        var g = fixAngle(357.529 + 0.98560028 * D)
        var q = fixAngle(280.459 + 0.98564736 * D)
        var L = fixAngle(q + 1.915 * sin(g) + 0.020 * sin(2 * g))

        var R = 1.00014 - 0.01671 * cos(g) - 0.00014 * cos(2 * g)
        var e = 23.439 - 0.00000036 * D

        var RA = arctan2(cos(e) * sin(L), cos(L)) / 15
        var eqt = q / 15 - fixHour(RA)
        var decl = arcsin(sin(e) * sin(L))

        val sPosition = DoubleArray(2)
        sPosition[0] = decl
        sPosition[1] = eqt

        return sPosition
    }
    //------------------------------------------------------------------------




    //--------------- преобразование часов в день порциями -------------------
    private fun  dayPortion(times: DoubleArray): DoubleArray {
        for (i in 0..7) times[i] = times[i]/24.0
        return times
    }
    //------------------------------------------------------------------------

    //---------------------- МАТЕМАТИЧЕСКИЕ ФУНКЦИИ --------------------------

    private fun  fixAngle(a: Double): Double { return fix(a, 360.0)}

    private fun  fix(a: Double, b: Double): Double {
        var A = a
        A -=  b * (Math.floor(a / b))
        return if (A < 0) A + b else A
    }

    private fun  dtr(d: Double): Double { return (d * Math.PI) / 180.0 }
    private fun  rtd(r: Double): Double { return (r * 180.0) / Math.PI }

    private fun  sin(d: Double): Double { return Math.sin(dtr(d)) }
    private fun  cos(d: Double): Double { return Math.cos(dtr(d)) }
    private fun  tan(d: Double): Double { return Math.tan(dtr(d)) }
    private fun  arctan2(y: Double, x: Double): Double { return rtd(Math.atan2(y, x)) }
    private fun  arcsin(d: Double): Double { return rtd(Math.asin(d)) }
    private fun  arccos(d: Double): Double { return rtd(Math.acos(d)) }
    private fun  arccot(x: Double): Double { return rtd(Math.atan(1 / x)) }

    private fun  fixHour(a: Double): Double { return fix(a, 24.0) }



}





