package com.aznstudio.namaz.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.R
import kotlinx.android.synthetic.main.calendar_item.view.*


class CalendarAdapter (val activity: Activity, val nameHolliday:Array<String>, val hollidayIslamDay:Array<Int>, val hollidayIslamMonth:Array<String>, val gregorMonthH:Array<Int>, val gregorDayH: Array<Int>, val gregorYearArray:IntArray, val islamYearArray:IntArray) : BaseAdapter() {
    override fun getItemId(p0: Int): Long {
        return p0.toLong();
    }

    override fun getCount(): Int {
        return nameHolliday.size;
    }

    override fun getItem(p0: Int): Any {
        return p0;
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var vi: View? = p1
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (vi == null)
            vi = inflater.inflate(R.layout.calendar_item, null)


        var mainActivity = activity as MainActivity
        //формаруем дату праздника
        vi?.nameHolliday2?.setText(nameHolliday[p0])
        vi?.dateHolliday1?.setText(hollidayIslamDay[p0].toString()+" "+hollidayIslamMonth[p0]+" "+islamYearArray[p0].toString()+" AH")
        var tmpDate:String=""
        tmpDate=gregorDayH[p0].toString()
        when (gregorMonthH[p0]){
            1 -> tmpDate+=" января "
            2 -> tmpDate+=" февраля "
            3 -> tmpDate+=" марта "
            4 -> tmpDate+=" апреля "
            5 -> tmpDate+=" мая "
            6 -> tmpDate+=" июня "
            7 -> tmpDate+=" июля "
            8 -> tmpDate+=" августа "
            9 -> tmpDate+=" сентября "
            10 -> tmpDate+=" октября "
            11 -> tmpDate+=" ноября "
            12 -> tmpDate+=" декабря "
            else -> ""
        }
        tmpDate+=gregorYearArray[p0].toString()+" г."
        vi?.dateHolliday2?.setText(tmpDate)
        return vi
    }



}