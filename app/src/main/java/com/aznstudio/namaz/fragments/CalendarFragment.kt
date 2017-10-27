package com.aznstudio.namaz.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aznstudio.namaz.MainActivity

import com.aznstudio.namaz.R
import com.aznstudio.namaz.adapters.CalendarAdapter
import com.aznstudio.namaz.adapters.MosqueAdapter
import com.aznstudio.namaz.pray.times.PrayTime
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_mosque.view.*
import java.util.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener



class CalendarFragment : Fragment(), View.OnClickListener {

    val TAG: String = "CalendarFragment"
    val FRAGMENT = R.layout.fragment_calendar

    // region объявление переменных
    private lateinit var tvActive: TextView

    private var islamYear:Int = 0
    private var islamMonth:Int = 0
    private var islamDay :Int = 0

    private var year:Int = 0
    private var month:Int = 0
    private var day :Int = 0

    private var dayIteration:Int = 1


    private var islamicEpoch = 1948439.5
    private var LeapYearIslamic = false
    private var v:View?=null;
    private var activIteration = 0
    //endregion
    //масивы для вычисления праздников
    private val nameHolliday = arrayOf("Новый год", "Ночь Мирадж", "Ночь Бараат","Рамадан","Ураза байрам(~)","День Арафат","Курбан байрам")
    private val hollidayIslamMonth = arrayOf("Мухаррам", "Раджаб", "Ша’бан","Рамадан","Шавваль","Зуль-хиджа","Зуль-хиджа")
    private val hollidayIslamDay = arrayOf(1, 27, 15,1,1,9,10)
    private val islamMonthH= arrayOf(1,7,8,9,10,12,12)
    private val gregorMonthH= arrayOf(9,4,4,5,6,8,8)
    private val gregorDayH= arrayOf(21,12,30,15,14,20,21)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        v= inflater!!.inflate(FRAGMENT, container, false)
        return v
    }

    override fun onResume() {
        super.onResume()
        val now = Date()
        val cal = Calendar.getInstance()
        cal.time = now
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH) + 1
        day = cal.get(Calendar.DATE)
        btn_next.setOnClickListener(this@CalendarFragment)
        btn_prev.setOnClickListener(this@CalendarFragment)

        //region подключение обработчика нажатия к TextView

        week_1_day_1.setOnClickListener(this@CalendarFragment)
        week_1_day_2.setOnClickListener(this@CalendarFragment)
        week_1_day_3.setOnClickListener(this@CalendarFragment)
        week_1_day_4.setOnClickListener(this@CalendarFragment)
        week_1_day_5.setOnClickListener(this@CalendarFragment)
        week_1_day_6.setOnClickListener(this@CalendarFragment)
        week_1_day_7.setOnClickListener(this@CalendarFragment)
        week_2_day_1.setOnClickListener(this@CalendarFragment)
        week_2_day_2.setOnClickListener(this@CalendarFragment)
        week_2_day_3.setOnClickListener(this@CalendarFragment)
        week_2_day_4.setOnClickListener(this@CalendarFragment)
        week_2_day_5.setOnClickListener(this@CalendarFragment)
        week_2_day_6.setOnClickListener(this@CalendarFragment)
        week_2_day_7.setOnClickListener(this@CalendarFragment)
        week_3_day_1.setOnClickListener(this@CalendarFragment)
        week_3_day_2.setOnClickListener(this@CalendarFragment)
        week_3_day_3.setOnClickListener(this@CalendarFragment)
        week_3_day_4.setOnClickListener(this@CalendarFragment)
        week_3_day_5.setOnClickListener(this@CalendarFragment)
        week_3_day_6.setOnClickListener(this@CalendarFragment)
        week_3_day_7.setOnClickListener(this@CalendarFragment)
        week_4_day_1.setOnClickListener(this@CalendarFragment)
        week_4_day_2.setOnClickListener(this@CalendarFragment)
        week_4_day_3.setOnClickListener(this@CalendarFragment)
        week_4_day_4.setOnClickListener(this@CalendarFragment)
        week_4_day_5.setOnClickListener(this@CalendarFragment)
        week_4_day_6.setOnClickListener(this@CalendarFragment)
        week_4_day_7.setOnClickListener(this@CalendarFragment)
        week_5_day_1.setOnClickListener(this@CalendarFragment)
        week_5_day_2.setOnClickListener(this@CalendarFragment)
        week_5_day_3.setOnClickListener(this@CalendarFragment)
        week_5_day_4.setOnClickListener(this@CalendarFragment)
        week_5_day_5.setOnClickListener(this@CalendarFragment)
        week_5_day_6.setOnClickListener(this@CalendarFragment)
        week_5_day_7.setOnClickListener(this@CalendarFragment)
        week_6_day_1.setOnClickListener(this@CalendarFragment)

        //endregion

        //tvGrDate.text = dateNowString // отображаем текущую дату
        tvActive = this.week_1_day_1
        var nowDate = intArrayOf(JD_to_islamic()[0].toInt(),JD_to_islamic()[1].toInt(),JD_to_islamic()[2].toInt())
        islamYear = nowDate[0]
        islamMonth = nowDate[1]
        islamDay = nowDate[2]

        islamicMonth(islamYear, islamMonth)
        islamicCalendar(islamYear,islamMonth)

        activatedTV(dayIteration)
        getHolliday(year,month,islamYear,islamMonth,v)

    }

    private fun activatedTV(dayIteration: Int) {
        var num = JD_to_islamic()[2].toInt()
        if (dayIteration == num){
        }

    }


    // слушатель onClick
    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_next -> {
                if (islamMonth == 12){
                    islamYear++
                    islamMonth = 1
                } else islamMonth++

                tvActive.background = null
                dayIteration = 1
                islamicMonth(islamYear, islamMonth)
                islamicCalendar(islamYear, islamMonth)
            }
            R.id.btn_prev -> {
                if (islamMonth == 1){
                    islamYear--
                    islamMonth = 12
                } else islamMonth--

                tvActive.background = null
                dayIteration = 1
                islamicMonth(islamYear, islamMonth)
                islamicCalendar(islamYear, islamMonth)
            }
            else -> activateTV(v as TextView)
        }
    }

    // Активная TextView
    private fun activeTV(active: TextView){
        if (activIteration == 0) if (dayIteration == islamDay + 1) {
            activIteration++
            activateTV(active)
        }
        if (active.text == "1") {
            activateTV(active)
        }
    }

    private fun activateTV(active: TextView){
        if (active.text != ""){
            tvActive.background = null
            tvActive = active
            tvActive.setBackgroundResource(R.drawable.oval_style)
            grDateToTv(active)
        }
    }

    // отображение даты активного ТВ
    fun grDateToTv(active: TextView) {

        var JD = Islamic_to_JD(islamYear.toDouble(), islamMonth.toDouble(), active.text.toString().toDouble())
        var date = JD_to_Gr_date(JD)
        var day = date[2]
        if (day == 0) { date[1]--; day = 31 }
        var month:String = when(date[1]){
            1 -> " января "
            2 -> " февраля "
            3 -> " марта "
            4 -> " апреля "
            5 -> " мая "
            6 -> " июня "
            7 -> " июля "
            8 -> " августа "
            9 -> " сентября "
            10 -> " октября "
            11 -> " ноября "
            12 -> " декабря "
            else -> ""
        }
        this.gr_date.text = day.toString() + month + date[0].toString()

    }

    // функция для наполнения ТВ числами месяца
    private fun islamicCalendar(islamYear:Int, islamMonth: Int){

        val dayOfWeek = islamicWeekDays(islamYear, islamMonth)
        for (i in 0..36) {
            when (i + 1) {
                1  -> {week_1_day_1.text = isCal(dayOfWeek, i);activeTV(week_1_day_1)}
                2  -> {week_1_day_2.text = isCal(dayOfWeek, i);activeTV(week_1_day_2)}
                3  -> {week_1_day_3.text = isCal(dayOfWeek, i);activeTV(week_1_day_3)}
                4  -> {week_1_day_4.text = isCal(dayOfWeek, i);activeTV(week_1_day_4)}
                5  -> {week_1_day_5.text = isCal(dayOfWeek, i);activeTV(week_1_day_5)}
                6  -> {week_1_day_6.text = isCal(dayOfWeek, i);activeTV(week_1_day_6)}
                7  -> {week_1_day_7.text = isCal(dayOfWeek, i);activeTV(week_1_day_7)}
                8  -> {week_2_day_1.text = isCal(dayOfWeek, i);activeTV(week_2_day_1)}
                9  -> {week_2_day_2.text = isCal(dayOfWeek, i);activeTV(week_2_day_2)}
                10 -> {week_2_day_3.text = isCal(dayOfWeek, i);activeTV(week_2_day_3)}
                11 -> {week_2_day_4.text = isCal(dayOfWeek, i);activeTV(week_2_day_4)}
                12 -> {week_2_day_5.text = isCal(dayOfWeek, i);activeTV(week_2_day_5)}
                13 -> {week_2_day_6.text = isCal(dayOfWeek, i);activeTV(week_2_day_6)}
                14 -> {week_2_day_7.text = isCal(dayOfWeek, i);activeTV(week_2_day_7)}
                15 -> {week_3_day_1.text = isCal(dayOfWeek, i);activeTV(week_3_day_1)}
                16 -> {week_3_day_2.text = isCal(dayOfWeek, i);activeTV(week_3_day_2)}
                17 -> {week_3_day_3.text = isCal(dayOfWeek, i);activeTV(week_3_day_3)}
                18 -> {week_3_day_4.text = isCal(dayOfWeek, i);activeTV(week_3_day_4)}
                19 -> {week_3_day_5.text = isCal(dayOfWeek, i);activeTV(week_3_day_5)}
                20 -> {week_3_day_6.text = isCal(dayOfWeek, i);activeTV(week_3_day_6)}
                21 -> {week_3_day_7.text = isCal(dayOfWeek, i);activeTV(week_3_day_7)}
                22 -> {week_4_day_1.text = isCal(dayOfWeek, i);activeTV(week_4_day_1)}
                23 -> {week_4_day_2.text = isCal(dayOfWeek, i);activeTV(week_4_day_2)}
                24 -> {week_4_day_3.text = isCal(dayOfWeek, i);activeTV(week_4_day_3)}
                25 -> {week_4_day_4.text = isCal(dayOfWeek, i);activeTV(week_4_day_4)}
                26 -> {week_4_day_5.text = isCal(dayOfWeek, i);activeTV(week_4_day_5)}
                27 -> {week_4_day_6.text = isCal(dayOfWeek, i);activeTV(week_4_day_6)}
                28 -> {week_4_day_7.text = isCal(dayOfWeek, i);activeTV(week_4_day_7)}
                29 -> {week_5_day_1.text = isCal(dayOfWeek, i);activeTV(week_5_day_1)}
                30 -> {week_5_day_2.text = isCal(dayOfWeek, i);activeTV(week_5_day_2)}
                31 -> {week_5_day_3.text = isCal(dayOfWeek, i);activeTV(week_5_day_3)}
                32 -> {week_5_day_4.text = isCal(dayOfWeek, i);activeTV(week_5_day_4)}
                33 -> {week_5_day_5.text = isCal(dayOfWeek, i);activeTV(week_5_day_5)}
                34 -> {week_5_day_6.text = isCal(dayOfWeek, i);activeTV(week_5_day_6)}
                35 -> {week_5_day_7.text = isCal(dayOfWeek, i);activeTV(week_5_day_7)}
                36 -> if (dayOfWeek == 6){
                    when(islamMonth){
                        1,3,5,7,9,11 -> week_6_day_1.text = "30"
                        2,4,6,8,10,12 -> week_6_day_1.text = ""
                    }
                } else week_6_day_1.text = ""
            }
        }
    }

    // функция для вычисления чисел месяца
    private fun isCal(dayOfWeek:Int, i: Int): String {

        return if (dayOfWeek > i) ""
        else when(dayIteration){
            in 1..29 -> {
                dayIteration++
                (dayIteration - 1).toString()
            }
            else -> if (leap_Year_Islamic(islamYear)) when (islamMonth) {
                1,3,5,7,9,11,12 -> if (dayIteration == 30) {dayIteration++; "30"} else ""
                else -> ""
            }
            else when (islamMonth) {
                1,3,5,7,9,11 -> if (dayIteration == 30) {dayIteration++; "30"} else ""
                else -> ""
            }
        }
    }

    // проверяем является ли год високосным
    private fun leap_Year_Islamic(islamYear: Int):Boolean{

        var firstYearThickness: Int = 0
        var thickness = Math.floor(islamYear.toDouble() / 30.0)

        firstYearThickness = thickness.toInt() * 30

        return when(islamYear - firstYearThickness + 1){
            2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29 -> true
            else -> false
        }
    }

    // вычисляем день недели для исламского календаря
    private fun islamicWeekDays(islamYear:Int, islamMonth:Int):Int{

        var firstDayThickness = 0
        var firstYearThickness = 0
        var DayThickness = 0

        var thickness = Math.floor(islamYear.toDouble() / 30.0)
        for (i in 0..thickness.toInt()){
            firstDayThickness += 5
            if (firstDayThickness >= 7) firstDayThickness -= 7
        }

        firstYearThickness = thickness.toInt() * 30

        for (i in 0..(islamYear - firstYearThickness)) DayThickness += when(i + 1) {
            2, 5, 7, 10, 13, 16, 18, 21, 24, 26, 29 -> 355
            else -> 354
        }

        for (i in 0..islamMonth) when(i){
            1,3,5,7,9,11  -> DayThickness += 30
            2,4,6,8,10,12 -> DayThickness += 29
        }

        var ret = DayThickness - Math.floor(DayThickness / 7.0).toInt() * 7 + firstDayThickness
        return if (ret >= 7) ret - 7 else ret

    }

    // Преобразование Юлианского дня в Исламский
    private fun JD_to_islamic():DoubleArray{

        var JulianDate = PrayTime().julianV2(year, month, day)

        var jd = Math.floor(JulianDate) + 0.5
        var islamicDate:DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
        islamicDate[0] = Math.floor(((30 * (jd - islamicEpoch))+ 10646) / 10631)
        islamicDate[1] = Math.min(12.0, Math.ceil((jd - (29 + Islamic_to_JD(islamicDate[0],1.0,1.0))) / 29.5) + 1 )
        islamicDate[2] = (jd - Islamic_to_JD(islamicDate[0], islamicDate[1], 1.0)) + 1

        return islamicDate
    }

    // функция для отоброжения исламского месяца и года в tv

    @SuppressLint("SetTextI18n")
    private fun islamicMonth(year: Int, month: Int){
        when(month) {
            1  -> islam_date.text = getString(R.string.month_text_1) + " " + year.toString()
            2  -> islam_date.text = getString(R.string.month_text_2) + " " + year.toString()
            3  -> islam_date.text = getString(R.string.month_text_3) + " " + year.toString()
            4  -> islam_date.text = getString(R.string.month_text_4) + " " + year.toString()
            5  -> islam_date.text = getString(R.string.month_text_5) + " " + year.toString()
            6  -> islam_date.text = getString(R.string.month_text_6) + " " + year.toString()
            7  -> islam_date.text = getString(R.string.month_text_7) + " " + year.toString()
            8  -> islam_date.text = getString(R.string.month_text_8) + " " + year.toString()
            9  -> islam_date.text = getString(R.string.month_text_9) + " " + year.toString()
            10 -> islam_date.text = getString(R.string.month_text_10) + " " + year.toString()
            11 -> islam_date.text = getString(R.string.month_text_11) + " " + year.toString()
            12 -> islam_date.text = getString(R.string.month_text_12) + " " + year.toString()
        }
    }

    // преобразование исламской даты в юлианский день
    private fun Islamic_to_JD(year:Double, month:Double, day:Double): Double {
        return (day + Math.ceil(29.5 * (month - 1)) + (year - 1) * 354 + Math.floor((3 + (11 * year)) / 30) + islamicEpoch) - 1
    }

    // преобразование юлианского дня в григорьевскую дату
    private fun JD_to_Gr_date(JDN: Double):IntArray{

        var a = JDN + 32044.0
        var b = Math.floor((4.0 * a + 3.0) / 146097.0)
        var c = a - Math.floor((146097.0 * b) / 4.0)
        var d = Math.floor((4.0 * c + 3.0) / 1461.0)
        var e = c - Math.floor((1461.0 * d) / 4.0)
        var m = Math.floor((5.0 * e + 2.0) / 153.0)

        var day = e - Math.floor((153.0 * m + 2.0) / 5.0) + 1.0
        var month = m + 3.0 - 12.0 * Math.floor(m / 10.0)
        var year = 100.0 * b + d -4800 + Math.floor(m / 10.0)

        return intArrayOf(year.toInt(), month.toInt(), day.toInt())
    }
    //формирование и сортировка списка праздников
    private fun getHolliday(year: Int,month: Int,islamYear: Int,islamMonth: Int,v:View?) {
        var gregorYearArray = IntArray(7)
        var islamYearArray = IntArray(7)

        for (i in 0..islamMonthH.size - 1) {
            if (islamMonthH[i] >= islamMonth) islamYearArray[i] = islamYear
            else islamYearArray[i] = islamYear + 1
            if (gregorMonthH[i] <= month) gregorYearArray[i] = year + 1
            else gregorYearArray[i] = year

        }
        var temp = 0
        var strTemp=""
        for (i in 0..islamMonthH.size - 1) {
            for (j in 1..islamMonthH.size - 1 - i) {
                if (gregorYearArray[j - 1] > gregorYearArray[j]) {
                    //swap elements
                    temp = gregorYearArray[j - 1];
                    gregorYearArray[j - 1] = gregorYearArray[j];
                    gregorYearArray[j] = temp;
                    //----
                    temp = islamYearArray[j - 1];
                    islamYearArray[j - 1] = islamYearArray[j];
                    islamYearArray[j] = temp;
                    //--------
                    temp = islamMonthH[j - 1];
                    islamMonthH[j - 1] = islamMonthH[j];
                    islamMonthH[j] = temp;
                    //--------
                    temp = gregorMonthH[j - 1];
                    gregorMonthH[j - 1] = gregorMonthH[j];
                    gregorMonthH[j] = temp;
                    //--------
                    temp = hollidayIslamDay[j - 1];
                    hollidayIslamDay[j - 1] = hollidayIslamDay[j];
                    hollidayIslamDay[j] = temp;
                    //--------
                    temp = gregorDayH[j - 1];
                    gregorDayH[j - 1] = gregorDayH[j];
                    gregorDayH[j] = temp;
                    //--------
                    strTemp = nameHolliday[j - 1];
                    nameHolliday[j - 1] = nameHolliday[j];
                    nameHolliday[j] = strTemp;
                    //--------
                    strTemp = hollidayIslamMonth[j - 1];
                    hollidayIslamMonth[j - 1] = hollidayIslamMonth[j];
                    hollidayIslamMonth[j] = strTemp;
                }

            }
        }
        var flag=false
        for (i in 0..islamMonthH.size - 1)
            if (gregorYearArray[0]!=gregorYearArray[i]) {
                flag=true
                break
            }
        if (!flag){
            for (i in 0..islamMonthH.size - 1) {
                for (j in 1..islamMonthH.size - 1 - i) {
                    if (gregorMonthH[j - 1] > gregorMonthH[j]) {
                        //swap elements
                        temp = gregorYearArray[j - 1];
                        gregorYearArray[j - 1] = gregorYearArray[j];
                        gregorYearArray[j] = temp;
                        //----
                        temp = islamYearArray[j - 1];
                        islamYearArray[j - 1] = islamYearArray[j];
                        islamYearArray[j] = temp;
                        //--------
                        temp = islamMonthH[j - 1];
                        islamMonthH[j - 1] = islamMonthH[j];
                        islamMonthH[j] = temp;
                        //--------
                        temp = gregorMonthH[j - 1];
                        gregorMonthH[j - 1] = gregorMonthH[j];
                        gregorMonthH[j] = temp;
                        //--------
                        temp = hollidayIslamDay[j - 1];
                        hollidayIslamDay[j - 1] = hollidayIslamDay[j];
                        hollidayIslamDay[j] = temp;
                        //--------
                        temp = gregorDayH[j - 1];
                        gregorDayH[j - 1] = gregorDayH[j];
                        gregorDayH[j] = temp;
                        //--------
                        strTemp = nameHolliday[j - 1];
                        nameHolliday[j - 1] = nameHolliday[j];
                        nameHolliday[j] = strTemp;
                        //--------
                        strTemp = hollidayIslamMonth[j - 1];
                        hollidayIslamMonth[j - 1] = hollidayIslamMonth[j];
                        hollidayIslamMonth[j] = strTemp;
                    }

                }
            }
        }
        var mainActivity = activity as MainActivity
        val adapter = CalendarAdapter(mainActivity,nameHolliday,hollidayIslamDay,hollidayIslamMonth,gregorMonthH,gregorDayH,gregorYearArray,islamYearArray )
        v?.lv_nonscroll_list?.adapter=adapter
        v?.lv_nonscroll_list?.setOnItemClickListener(OnItemClickListener { arg0, arg1, arg2, arg3 ->
        })
    }
}
