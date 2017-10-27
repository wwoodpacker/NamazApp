package com.aznstudio.namaz.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aznstudio.namaz.GeocodeHelper
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.PermissionHelper
import com.aznstudio.namaz.R
import com.aznstudio.namaz.pray.times.Methods
import com.aznstudio.namaz.pray.times.PrayTime
import kotlinx.android.synthetic.main.fragment_time_namaz.*
import java.util.*
import android.R.id.edit
import android.content.Context


open class TimeNamazFragment : Fragment(), View.OnClickListener{
    var TAG = "TimeNamazFragment"
    val APP_PREFERENCES: String = "appSetting"
    val FRAGMENT = R.layout.fragment_time_namaz

    //region объявление переменных

    //lateinit var myLatLng: DoubleArray
    private lateinit var tvDayOfMonthActive: TextView
    var LeapYear = GregorianCalendar()
    private lateinit var date: IntArray

    //endregion

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(FRAGMENT, container, false)
        
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        tvDayOfMonthActive = day_of_month_1
        tvDayOfMonthActive.setBackgroundResource(R.drawable.oval_style)

        getDate()
        getDaysWeek(date)
        //myLatLng = locationService.loadLocation()
        getNamazTime()

        day_of_month_1.setOnClickListener(this)
        day_of_month_2.setOnClickListener(this)
        day_of_month_3.setOnClickListener(this)
        day_of_month_4.setOnClickListener(this)
        day_of_month_5.setOnClickListener(this)
        day_of_month_6.setOnClickListener(this)
        day_of_month_7.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v){
                day_of_month_1 -> dayOfMonthActive(this.day_of_month_1)
                day_of_month_2 -> dayOfMonthActive(this.day_of_month_2)
                day_of_month_3 -> dayOfMonthActive(this.day_of_month_3)
                day_of_month_4 -> dayOfMonthActive(this.day_of_month_4)
                day_of_month_5 -> dayOfMonthActive(this.day_of_month_5)
                day_of_month_6 -> dayOfMonthActive(this.day_of_month_6)
                day_of_month_7 -> dayOfMonthActive(this.day_of_month_7)
            }
        }
    }

    // Активная TextView
    private fun  dayOfMonthActive(day_of_month: TextView) {
        tvDayOfMonthActive.background = null
        tvDayOfMonthActive = day_of_month
        tvDayOfMonthActive.setBackgroundResource(R.drawable.oval_style)
        date[2] = Integer.parseInt(tvDayOfMonthActive.text as String?)
        getNamazTime()
    }

    // определение дня неденли
    private fun getDaysWeek(date: IntArray) {
        var month_days = 0
        var days = intArrayOf(0,0,0,0,0,0,0)
        var daysOfWeek = intArrayOf(0,0,0,0,0,0,0)

        when(date[1]){
            1,3,5,7,8,10,12 -> month_days = 31
            4,6,9,11 -> month_days = 30
            2 -> month_days = if (LeapYear.isLeapYear(date[0])) 29 else 28
        }

        for (i in 0..6){
            if ( (date[2] + i) > month_days ) days[i] = date[2] + i - month_days else days[i] = date[2] + i
            if ( (date[3] + i) > 7 ) daysOfWeek[i] = date[3] + i - 7 else daysOfWeek[i] = date[3] + i
        }

        for (i in 0..6){
            when(i) {
                0 -> {
                    day_of_month_1.text = days[i].toString()
                    day_of_week_1.text = day_of_month(daysOfWeek[i])}
                1 -> {
                    day_of_month_2.text = days[i].toString()
                    day_of_week_2.text = day_of_month(daysOfWeek[i])}
                2 -> {
                    day_of_month_3.text = days[i].toString()
                    day_of_week_3.text = day_of_month(daysOfWeek[i])}
                3 -> {
                    day_of_month_4.text = days[i].toString()
                    day_of_week_4.text = day_of_month(daysOfWeek[i])}
                4 -> {
                    day_of_month_5.text = days[i].toString()
                    day_of_week_5.text = day_of_month(daysOfWeek[i])}
                5 -> {
                    day_of_month_6.text = days[i].toString()
                    day_of_week_6.text = day_of_month(daysOfWeek[i])}
                6 -> {
                    day_of_month_7.text = days[i].toString()
                    day_of_week_7.text = day_of_month(daysOfWeek[i])}
            }
        }
    }

    // вывод дня недели в TextView
    private fun  day_of_month(day: Int): String {

        return when (day) {
            1 -> "Вс"
            2 -> "Пн"
            3 -> "Вт"
            4 -> "Ср"
            5 -> "Чт"
            6 -> "Пт"
            7 -> "Сб"
            else -> ""
        }
    }

    private fun getDate() {
    val now = Date()
    val cal = Calendar.getInstance()
    cal.time = now
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1
    val day = cal.get(Calendar.DATE)
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    date = intArrayOf(year,month,day,dayOfWeek)

}

    open fun getNamazTime (){
        val mainActivity = activity as MainActivity
        val coords = mainActivity.loadLocation()
        val timezone = 3
        val prayTime = PrayTime()
        prayTime.imsakMinute = 10.0
        prayTime.maghribMinute = 0.0
        val prayerTimes = prayTime.getTimes(date, coords, timezone, 0, Methods.Makkah)
        setTimeNamaz(prayerTimes)
        for (i in 0..6) {

            when (i) {
                0 -> time_fajr.text = prayerTimes[i + 1]
                1 -> time_sunrise.text = prayerTimes[i + 1]
                2 -> time_dhuhr.text = prayerTimes[i + 1]
                3 -> time_asr.text = prayerTimes[i + 1]
                5 -> time_maghrib.text = prayerTimes[i + 1]
                6 -> time_isha.text = prayerTimes[i + 1]
            }
        }

        if (ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //разрешения нет, делаем запрос
            PermissionHelper.isPermissionAllowedAndRequest(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }else {
            val s = mainActivity.findLocality(coords)
            onLocalityFound(s)
        }
    }
    fun setTimeNamaz(prayerTimes:ArrayList<String>){
        var namazArray = arrayOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha")
        var mSettings: SharedPreferences
        for (i in 0..6) {
            when (i) {
                0 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[0], cal.timeInMillis)
                    editor.apply()}
                1 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[1], cal.timeInMillis)
                    editor.apply()}
                2 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[2], cal.timeInMillis)
                    editor.apply()}
                3 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[3], cal.timeInMillis)
                    editor.apply()}
                5 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[4], cal.timeInMillis)
                    editor.apply()}
                6 -> {var times = prayerTimes[i + 1].split(":")
                    var hour = times[0].toInt()
                    var minutes = times[1].toInt()
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minutes)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                    val editor = mSettings.edit()
                    editor.putLong(namazArray[5], cal.timeInMillis)
                    editor.apply()}
            }
        }
    }
    private fun onLocalityFound(loc: String) {
        location.text = loc
    }
}
