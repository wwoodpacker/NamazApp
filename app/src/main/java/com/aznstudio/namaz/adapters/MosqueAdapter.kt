package com.aznstudio.namaz.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import android.widget.TextView
import android.widget.RelativeLayout
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.location.Location
import android.view.LayoutInflater
import com.aznstudio.namaz.R
import com.aznstudio.namaz.data.Response
import kotlinx.android.synthetic.main.mosque_item.view.*
import android.location.Location.distanceBetween
import android.util.Log
import com.aznstudio.namaz.MainActivity
import com.squareup.picasso.Picasso
import java.math.RoundingMode
import java.text.DecimalFormat



class MosqueAdapter(val activity: Activity, val response:Response?) : BaseAdapter() {
    override fun getItemId(p0: Int): Long {
        return p0.toLong();
    }

    override fun getCount(): Int {
        return response?.results?.size!!;
    }

    override fun getItem(p0: Int): Any {
        return p0;
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var vi: View? = p1

            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            if (vi == null)
                vi = inflater.inflate(R.layout.mosque_item, null)


            var mainActivity = activity as MainActivity
            val myCoords = mainActivity.loadLocation();
            var endLat: Double = response!!.results!!.get(p0)!!.geometry!!.location!!.lat!!
            var endLon: Double = response!!.results!!.get(p0)!!.geometry!!.location!!.lng!!
            var distanse: Double = CalculationDistanceByCoord(myCoords[0], myCoords[1], endLat, endLon);
            //проверка на наличие адреса
            if ((!response?.results?.get(p0)?.vicinity?.isEmpty()!!)||(!response?.results?.get(p0)?.vicinity?.equals("")!!)) {
                vi?.nameMosque?.setText(response?.results?.get(p0)?.name)
                vi?.adressMosque?.setText(response?.results?.get(p0)?.vicinity)
                vi?.distanceMosque?.setText(parseDistance(distanse))
                //подгружаем фото объекта
                var photoKey: String? = response.results?.get(p0)?.photos?.get(0)?.photoReference
                if (photoKey != null) {
                    var mapsApiKey: String = activity.baseContext.getString(R.string.google_maps_key)
                    var url: String = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoKey&key=$mapsApiKey"
                    Picasso.with(activity.baseContext).load(url).into(vi?.imgMosque);
                }
            }else{
                vi?.visibility=View.GONE
                vi?.nameMosque?.visibility=View.GONE
                vi?.adressMosque?.visibility=View.GONE
                vi?.distanceMosque?.visibility=View.GONE
                vi?.imgMosque?.visibility=View.GONE
            }
        return vi
    }

    //определяем дистанцию в метрах или километрах
    fun parseDistance(dis:Double):String{
        var k:Int=0
        var tmp:Double=dis;
        var result:String=""

        while ((tmp/10).toInt()!=0) {
            tmp=tmp/10
            k++
        }

        if(k==3){
            var kilometrs:Float= (dis*0.001F).toFloat();
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            result=df.format(kilometrs)+" км"
        }else{
           result=(dis.toInt()).toString()+" м"
        }

        return result
    }
    //расчитываем растояние между точками используя шыроту и долготу
    private fun CalculationDistanceByCoord(startPointLat: Double, startPointLon: Double, endPointLat: Double, endPointLon: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(startPointLat, startPointLon, endPointLat, endPointLon, results)
        return results[0].toDouble()
    }
}