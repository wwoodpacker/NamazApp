package com.aznstudio.namaz

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.aznstudio.namaz.fragments.KiblaFragment
import com.google.android.gms.maps.model.LatLng
import rx.Single
import rx.SingleSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.Locale.getDefault


object GeocodeHelper {

    fun findLocality(context: Context, latLng: LatLng, callback: GeocodeCallback) {
        Single.just(latLng).map(Func1<LatLng, String> { latLng ->
            //запрашиваем адресс по заданным координатам
            val geocoder = Geocoder(context, getDefault())
            try {
                val addressList = geocoder.getFromLocation(
                        latLng.latitude, latLng.longitude, 1)
                if (addressList != null && !addressList.isEmpty()) {
                    val address = addressList[0]
                    return@Func1 address.locality
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            null
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleSubscriber<String>() {
                    override fun onSuccess(s: String) {
                        callback.onLocalityFound(s)
                    }
                    override fun onError(error: Throwable) {
                    }
                })
    }

    fun findLocality2(context: Context, latLng: DoubleArray, callback: GeocodeCallback) {
        var subscribe = Single.just(latLng).map(Func1<DoubleArray, String> { latLng ->
            //запрашиваем адресс по заданным координатам
            val geocoder = Geocoder(context.applicationContext)
            try {
                val addressList = geocoder.getFromLocation(latLng[0], latLng[1], 7)
                if (addressList != null && !addressList.isEmpty()) {
                    var address: Address
                    for (i in 0..6) {
                        address = addressList[i]
                        if (address.locality != null) return@Func1 address.locality
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ""
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleSubscriber<String>() {
                    override fun onSuccess(s: String) {
                        callback.onLocalityFound(s)
                    }

                    override fun onError(error: Throwable) {
                    }
                })
    }

    fun findLocality3(context: Context, latLng: DoubleArray, callback: GeocodeCallback) {
            val geocoder = Geocoder(context)
            val addressList = geocoder.getFromLocation(latLng[0], latLng[1], 7)
            if (addressList != null && !addressList.isEmpty()) {
                var address: Address
                for (i in 0..6) {
                    address = addressList[i]
                    if (address.locality != null) address.locality
                }
            }
    }

    //интерфейс обратного вызова для населенного пункта
    interface GeocodeCallback {
        fun onLocalityFound(locality: String)
    }
}
