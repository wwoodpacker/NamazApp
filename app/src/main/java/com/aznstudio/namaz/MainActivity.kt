package com.aznstudio.namaz

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager


import android.location.*
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.aznstudio.namaz.data.Response
import com.aznstudio.namaz.fragments.*
import com.aznstudio.namaz.service.NotificationReciver
import com.aznstudio.namaz.service.NotificationsService
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_kibla.*
import kotlinx.android.synthetic.main.frgment_settings.*
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private val ACTIVITY = R.layout.activity_main
    private val CONTAINER = R.id.container
    val FILE_NAME = "filename"

    private lateinit var manager: FragmentManager
    private lateinit var transaction: FragmentTransaction
    private var activeFragment: Fragment? = null
    lateinit var myLatLng: DoubleArray

    //val LOG_TAG:String="logsLocationService"
    //Долгота и широта, по умолчанию указана Москва
    var latMoscow:Double = 55.7522200
    var lngMoscow:Double = 37.6155600
    lateinit var appSetting: SharedPreferences
    val APP_PREFERENCES: String = "appSetting"
    val APP_PREFERENCES_LAT: String = "lat"
    val APP_PREFERENCES_LNG: String = "lng"
    val APP_PREFERENCES_ALT: String = "alt"

    var intentArray= arrayOfNulls<Intent>(10)
    var pendingIntent= arrayOfNulls<PendingIntent>(10)
    var alarmManager= arrayOfNulls<AlarmManager>(10)
    var namazArray = arrayOf("fajr", "sunrise", "dhuhr", "asr", "maghrib", "isha")
    var isLocatinSearch:Boolean=true
    lateinit var locationManager: LocationManager


    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog=indeterminateProgressDialog("Определение местоположения")
        dialog!!.dismiss()
        appSetting = getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        manager = supportFragmentManager
        setContentView(ACTIVITY)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Оставить отзыв", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        startService(Intent(this,NotificationsService::class.java))
        //----
        setNamazTimer()
    }

//Вычисляем за сколько минут должно срабатывать уведомленние
    fun getTimeBefore():Long{
        var result:Long=0
        Log.e("Spinner state",getSpinnerState().toString())
        when(getSpinnerState()){
            0-> result=3*60*1000
            1-> result=5*60*1000
            2-> result=7*60*1000
            3-> result=10*60*1000
            4-> result=15*60*1000
        }
        Log.e("time before",result.toString())
        return result
    }
    //берем положение спинера
    fun getSpinnerState():Int{
        val mSettings: SharedPreferences
        mSettings =  getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        var state=0;
        if(mSettings.contains("SPINNER")) {
            state=mSettings.getInt("SPINNER",0)
        }
        return state
    }

    fun setNamazTimer(){
        val mSettings: SharedPreferences
        val cal = Calendar.getInstance()
        val cur_cal = Calendar.getInstance()
        val timeBefore=getTimeBefore()
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            if(mSettings.contains("F")) {
                if (mSettings.getBoolean("F",false)){
                    val cal = mSettings.getLong(namazArray[0],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[0] = Intent(getApplicationContext(), NotificationReciver::class.java);
                    intentArray[0]?.putExtra("NAME","Фаджр")
                    pendingIntent[0] = PendingIntent.getBroadcast(this, 0, intentArray[0], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[0] = getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[0]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[0])
                    };
                }else alarmManager[0]?.cancel(pendingIntent[0])
            }
            if(mSettings.contains("S")) {
                if (mSettings.getBoolean("S",false)){
                    val cal = mSettings.getLong(namazArray[1],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[1] = Intent(getApplicationContext(),NotificationReciver::class.java);
                    intentArray[1]?.putExtra("NAME","Восход")
                    pendingIntent[1] = PendingIntent.getBroadcast(this, 1, intentArray[1], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[1]=getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[0]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[1])
                    };
                }else alarmManager[1]?.cancel(pendingIntent[1])
            }
            if(mSettings.contains("J")) {
                if(mSettings.getBoolean("J",false)){
                    val cal = mSettings.getLong(namazArray[2],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[2] = Intent(getApplicationContext(),NotificationReciver::class.java);
                    intentArray[2]?.putExtra("NAME","Зухр")
                    pendingIntent[2] = PendingIntent.getBroadcast(this, 2, intentArray[2], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[2]=getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[2]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[2])
                    };
                }else alarmManager[2]?.cancel(pendingIntent[2])
            }
            if(mSettings.contains("A")) {
                if (mSettings.getBoolean("A",false)){
                    val cal = mSettings.getLong(namazArray[3],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[3] = Intent(getApplicationContext(),NotificationReciver::class.java);
                    intentArray[3]?.putExtra("NAME","Аср")
                    pendingIntent[3] = PendingIntent.getBroadcast(this, 3, intentArray[3], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[3]=getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[3]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[3])
                    };
                }else alarmManager[3]?.cancel(pendingIntent[3])
            }
            if(mSettings.contains("M")) {
                if(mSettings.getBoolean("M",false)){
                    val cal = mSettings.getLong(namazArray[4],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[4] = Intent(getApplicationContext(),NotificationReciver::class.java);
                    intentArray[4]?.putExtra("NAME","Магриб")
                    pendingIntent[4] = PendingIntent.getBroadcast(this, 4, intentArray[4], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[4]=getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[4]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[4])
                    };
                }else alarmManager[4]?.cancel(pendingIntent[4])
            }
            if(mSettings.contains("I")) {
                if(mSettings.getBoolean("I",false)){
                    val cal = mSettings.getLong(namazArray[5],0)
                    cur_cal.timeInMillis=cal-timeBefore
                    if(cur_cal.before(Calendar.getInstance())) {
                        cur_cal.add(Calendar.DATE, 1);
                    }
                    intentArray[5] = Intent(getApplicationContext(),NotificationReciver::class.java);
                    intentArray[5]?.putExtra("NAME","Иша")
                    pendingIntent[5] = PendingIntent.getBroadcast(this, 5, intentArray[5], PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager[5]=getSystemService(ALARM_SERVICE) as AlarmManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager[5]!!.setExact(AlarmManager.RTC_WAKEUP, cur_cal.timeInMillis, pendingIntent[5])
                    };
                }else alarmManager[5]?.cancel(pendingIntent[5])
            }
        }


    override fun onResume() {
        super.onResume()

        replaceFragment(TimeNamazFragment(),TimeNamazFragment().TAG)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //разрешения нет, делаем запрос
            PermissionHelper.isPermissionAllowedAndRequest(this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationManager.run {
                requestLocationUpdates(LocationManager.GPS_PROVIDER, (1000 * 10).toLong(), 10f, this@MainActivity)
                requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (1000 * 10).toLong(), 10f, this@MainActivity)
            }
        }
    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_time_namaz -> {
                replaceFragment(TimeNamazFragment(),TimeNamazFragment().TAG)
            }
            R.id.nav_kibla -> {
                replaceFragment(KiblaFragment(),KiblaFragment().TAG)
            }
            R.id.nav_calendar -> {
                replaceFragment(CalendarFragment(),CalendarFragment().TAG)
            }
            R.id.nav_mosque -> {
                replaceFragment(MosqueFragment(),MosqueFragment().TAG)
            }
            R.id.nav_setting -> {
                replaceFragment(SettingsFragment(),SettingsFragment().TAG)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onLocationChanged(p0: Location?) {

        myLatLng = loadLocation()
        if (checkActiveFragment(TimeNamazFragment().TAG)) {
            if (isLocatinSearch) saveLocation(p0!!)
            val fragment = manager.findFragmentByTag(TimeNamazFragment().TAG) as TimeNamazFragment
            fragment.getNamazTime()
            dialog?.dismiss()
            Toast.makeText(this,"Ваше местоположение обновилось", Toast.LENGTH_LONG).show()
        }else
            if (checkActiveFragment(KiblaFragment().TAG)){

            }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }
    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun replaceFragment(fragment: Fragment,TAG:String){
        transaction = manager.beginTransaction()
        if (activeFragment != null) transaction.replace(CONTAINER, fragment, TAG)
        else transaction.add(CONTAINER, fragment)
        activeFragment = fragment
        transaction.commit()
    }

    private fun checkActiveFragment(tag:String):Boolean{
        Log.e(tag,(activeFragment == manager.findFragmentByTag(tag)).toString());
        return activeFragment == manager.findFragmentByTag(tag)

    }


    @SuppressLint("ApplySharedPref")
    private fun saveLocation(location: Location){
        val editor: SharedPreferences.Editor = appSetting.edit()
        editor.putString(APP_PREFERENCES_LAT,location.latitude.toString())
        editor.putString(APP_PREFERENCES_LNG,location.longitude.toString())
        //editor.putString(APP_PREFERENCES_ALT,location.altitude.toString())
        editor.apply()
    }
    fun loadLocation():DoubleArray{
        return if (appSetting.contains(APP_PREFERENCES_LAT) and appSetting.contains(APP_PREFERENCES_LNG)){
            doubleArrayOf(appSetting.getString(APP_PREFERENCES_LAT,"").toDouble(),appSetting.getString(APP_PREFERENCES_LNG,"").toDouble(),0.0)
        }else{
            doubleArrayOf(latMoscow,lngMoscow,0.0)
        }
    }

    fun findLocality(latLng: DoubleArray):String {
        var s = ""
        val geocoder = Geocoder(this)
        Log.e(latLng[0].toString(), latLng[1].toString())
        val addressList = geocoder.getFromLocation(latLng[0], latLng[1], 3)
        if (addressList != null && !addressList.isEmpty()) {
            var address: Address

            for (i in 0..addressList.size-1) {
                address = addressList[i]
                if (address.locality != null) s = address.locality
            }
        }
        return s
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
        if (resultCode == RESULT_OK) {
        var place = PlacePicker.getPlace(data, this);
            isLocatinSearch=false
            saveChoosenLocation(place.latLng.latitude,place.latLng.longitude)
         }
        }
    }
    private fun saveChoosenLocation(latitude:Double,longitude:Double){
        val editor: SharedPreferences.Editor = appSetting.edit()
        editor.putString(APP_PREFERENCES_LAT,latitude.toString())
        editor.putString(APP_PREFERENCES_LNG,longitude.toString())
        editor.apply()
    }
}


