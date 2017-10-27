package com.aznstudio.namaz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aznstudio.namaz.R
import kotlinx.android.synthetic.main.frgment_settings.*
import android.R.attr.data
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.service.NotificationReciver
import kotlinx.android.synthetic.main.frgment_settings.view.*
import java.util.*



class SettingsFragment : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    var TAG = "SettingsFragment"
    val APP_PREFERENCES: String = "appSetting"
    val APP_PREFERENCES_LAT: String = "lat"
    val APP_PREFERENCES_LNG: String = "lng"
    val FRAGMENT = R.layout.frgment_settings
    var latMoscow:Double = 55.7522200
    var lngMoscow:Double = 37.6155600
    lateinit var v:View
    var data = arrayOf("за 3 мин", "за 5 мин", "за 7 мин", "за 10 мин", "за 15 мин")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v=inflater!!.inflate(FRAGMENT, container, false)
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        v.spinner.adapter = adapter
        v.spinner.prompt = "Время до намаза"
        v.spinner.setSelection(0)
        findLocality(loadLocation())
        return v
    }

    fun loadLocation():DoubleArray{
        val mSettings: SharedPreferences
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        return if (mSettings.contains(APP_PREFERENCES_LAT) and mSettings.contains(APP_PREFERENCES_LNG)){
            doubleArrayOf(mSettings.getString(APP_PREFERENCES_LAT,"").toDouble(),mSettings.getString(APP_PREFERENCES_LNG,"").toDouble(),0.0)
        }else{
            doubleArrayOf(latMoscow,lngMoscow,0.0)
        }
    }

    fun findLocality(latLng: DoubleArray) {
        var s = ""
        val geocoder = Geocoder(context)
        Log.e(latLng[0].toString(), latLng[1].toString())
        val addressList = geocoder.getFromLocation(latLng[0], latLng[1], 3)
        if (addressList != null && !addressList.isEmpty()) {
            var address: Address

            for (i in 0..addressList.size-1) {
                address = addressList[i]
                if (address.locality != null) s = address.locality
            }
        }
        v.textLocation.setText(s)
    }
    override fun onResume() {
        super.onResume()
        btn_location.setOnClickListener(this)
        switch_fagr.setOnCheckedChangeListener(this)
        switch_sunrise.setOnCheckedChangeListener(this)
        switch_juhr.setOnCheckedChangeListener(this)
        switch_asr.setOnCheckedChangeListener(this)
        switch_margib.setOnCheckedChangeListener(this)
        switch_isha.setOnCheckedChangeListener(this)
        spinner.setOnItemSelectedListener(this)
        getSwitchSate()
        getSpinnerState()
    }
    //Button Location Listener
    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0){
                btn_location-> ChooseLocationDialog().show(activity.fragmentManager,ChooseLocationDialog().TAG)
            }
        }

    }
    //Switch listener
    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p0 != null) {
            when (p0){
                switch_fagr-> setSwitchState("F",p1)
                switch_sunrise-> setSwitchState("S",p1)
                switch_juhr-> setSwitchState("J",p1)
                switch_asr-> setSwitchState("A",p1)
                switch_margib-> setSwitchState("M",p1)
                switch_isha-> setSwitchState("I",p1)
            }
        }
    }
    //Scroll Listener
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        setSpinnerState(position)
    }
    //Save Switch State
    fun setSwitchState(nameSwitcn:String,state:Boolean){
        val mSettings: SharedPreferences
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings.edit()
        editor.putBoolean(nameSwitcn, state)
        editor.apply()
    }

    fun getSwitchSate() {
        val mSettings: SharedPreferences
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(mSettings.contains("F")) {
            switch_fagr.isChecked=mSettings.getBoolean("F",false)
        }
        if(mSettings.contains("S")) {
            switch_sunrise.isChecked=mSettings.getBoolean("S",false)
        }
        if(mSettings.contains("J")) {
            switch_juhr.isChecked=mSettings.getBoolean("J",false)
        }
        if(mSettings.contains("A")) {
            switch_asr.isChecked=mSettings.getBoolean("A",false)
        }
        if(mSettings.contains("M")) {
            switch_margib.isChecked=mSettings.getBoolean("M",false)
        }
        if(mSettings.contains("I")) {
            switch_isha.isChecked=mSettings.getBoolean("I",false)
        }
    }
    //Save Spinner state
    fun setSpinnerState(position: Int){
        val mSettings: SharedPreferences
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings.edit()
        editor.putInt("SPINNER", position)
        editor.apply()
    }
    fun getSpinnerState(){
        val mSettings: SharedPreferences
        mSettings =  context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(mSettings.contains("SPINNER")) {
            spinner.setSelection(mSettings.getInt("SPINNER",0))
        }
    }

    override fun onDestroy() {
        val mainActivity = activity as MainActivity
        mainActivity.setNamazTimer()
        super.onDestroy()
    }

}
