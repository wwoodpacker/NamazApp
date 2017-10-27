package com.aznstudio.namaz.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.R
import com.aznstudio.namaz.adapters.MosqueAdapter
import com.aznstudio.namaz.data.Response
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_mosque.view.*
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.support.v4.indeterminateProgressDialog
import org.jetbrains.anko.support.v4.progressDialog
import org.jetbrains.anko.uiThread
import java.net.URL


class MosqueFragment : Fragment() {

    val TAG: String = "MosqueFragment"


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v:View= inflater!!.inflate(R.layout.fragment_mosque, container, false)
        getMosque(v)
        return v
    }

    // делаем запрос к Google maps api
    fun getMosque(v:View){
        var findWord:String="мечеть"
        var mainActivity = activity as MainActivity
        val myCoords=mainActivity.loadLocation();
        val url = getUrl(myCoords[0], myCoords[1], findWord)
        var response: Response? = null
        val dialog = indeterminateProgressDialog("Загрузка")
        dialog.show()
        async {
            val result = URL(url).readText()
            uiThread {
                Log.d("Request", result)
                val gson = Gson()
                response = gson.fromJson(result, Response::class.java)
                val adapter = MosqueAdapter(mainActivity,response )
                v.listview.adapter=adapter
                dialog.dismiss()
            }
        }
    }

    //формирование ссылки на запрос к Google maps
    fun getUrl(lat:Double,lng:Double,word:String):String{
        var mapsApiKey:String= context.getString(R.string.google_maps_key)
        var latitute:String= lat.toString()
        var longitude:String=lng.toString()
        var photo:String="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CmRaAAAA8PL1inGpsle4LQ3rz8AIyadtIeHHiW3SyL5RjUNXmSpcpZIBtCKC0WrfcLX873fnPjv7Kl8Cv75y2ZD_p8WTJasaSIG6SWFMCXayBaDMp8whX1B4u_-pcj0NX-2aErRPEhB5g-q-a1ul-saXV1-mDTRLGhS0ZgeSOF-Ubh2zgaeS72THI7PZfg&key=AIzaSyCLG7qeUIPth_3AwelH4SKsO7OaPcoUGPE"
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitute,$longitude&radius=2000&keyword=$word&type=mosque&language=ru&key=$mapsApiKey"
        //return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=54.896460,52.292261&radius=1000&keyword=$word&language=ru&key=$mapsApiKey"
    }

}
