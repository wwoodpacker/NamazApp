package com.aznstudio.namaz.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aznstudio.namaz.Compass
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.PermissionHelper
import com.aznstudio.namaz.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_kibla.*
import com.google.android.gms.maps.SupportMapFragment




class KiblaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener{

    val TAG: String = "KiblaFragment"
    val FRAGMENT = R.layout.fragment_kibla
    private val CONTAINER = R.id.mapKibla

    private lateinit var manager: FragmentManager
    private lateinit var transaction: FragmentTransaction

    private lateinit var compass: Compass
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v : View = inflater!!.inflate(FRAGMENT, container, false)
        val mapFragment = this.childFragmentManager
                .findFragmentById(R.id.mapKibla) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //mapKibla.getMapAsync(this)
        return v
    }


    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        val coords = mainActivity.loadLocation()
        if (ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //разрешения нет, делаем запрос
            PermissionHelper.isPermissionAllowedAndRequest(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }else {
            val s = mainActivity.findLocality(coords)
            onLocalityFound(s)
        }


        //делаем запрос на инициацию
        //mapKibla.getMapAsync {  }
        /*if (mainActivity.checkActiveFragment(TAG)) {
        val handler = Handler()
        val run = object : Runnable {
            override fun run() {
                mapFragment = mapKibla as SupportMapFragment
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this@KiblaFragment)
                } else {
                    handler.postDelayed(this, 500)
                }
            }
        }
        handler.postDelayed(run, 500)
        }*/

        //инициируем компасс
        compass = Compass(context, arrow_image)
        setTargetPoint(kaaba)
        start()
        //val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        //addView(compassView, params)
    }

    override fun onStop() {
        super.onStop()
        stop()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //наша карта готова к использованию
        //устанавливаем зум
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))

        //делаем запрос на разрешение нахождения местоположения
        setUpMapIfNeeded()
    }

    @SuppressLint("MissingPermission")
    private fun setUpMapIfNeeded() {
        //разрешение есть, включаем нахождение местоположения пользователя
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMyLocationChangeListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResult: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult)
        //обрабатываем запрос на разрешение
        if (PermissionHelper.isPermissionsAllowed(context, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            setUpMapIfNeeded()
        }
    }

    override fun onMyLocationChange(location: Location) {
        //местоположения пользователя изменилось
        //очищаем карту
        mMap.clear()
        val latLngs = arrayOfNulls<LatLng>(2)
        latLngs[0] = LatLng(location.latitude, location.longitude)

        //запрашиваем населенный пункт
        //GeocodeHelper.findLocality(context, latLngs[0]!!, this)

        //обновляем данные компаса
        updateCurrentPoint(latLngs[0]!!)

        latLngs[1] = kaaba

        //добавляем маркер и рисуем линию
        mMap.addMarker(MarkerOptions().position(kaaba))
        mMap.addPolyline(PolylineOptions().add(*latLngs).color(Color.RED))

        //отправляем карту в позицию пользователя
        val cameraUpdate = CameraUpdateFactory.newLatLng(latLngs[0])
        mMap.moveCamera(cameraUpdate)
    }

    private fun onLocalityFound(loc: String) {
        //показываем населенный пункт
        locality.text = loc
    }

    companion object {
        //наша целевая точка
        private val kaaba = LatLng(21.4225, 39.82611)
    }

    private fun setTargetPoint(latLng: LatLng) {
        compass.setTargetPoint(latLng)
    }

    private fun updateCurrentPoint(latLng: LatLng) {
        compass.setCurrentPoint(latLng)
    }

    private fun start() {
        compass.start()
    }

    private fun stop() {
        compass.stop()
    }

}// Required empty public constructor
