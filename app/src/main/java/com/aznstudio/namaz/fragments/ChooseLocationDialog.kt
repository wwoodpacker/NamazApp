package com.aznstudio.namaz.fragments

import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aznstudio.namaz.MainActivity
import com.aznstudio.namaz.R
import kotlinx.android.synthetic.main.dialog_location.*
import kotlinx.android.synthetic.main.dialog_location.view.*
import android.support.v4.app.ShareCompat.IntentBuilder
import com.google.android.gms.location.places.ui.PlacePicker
import org.jetbrains.anko.startActivityForResult


//Диалог выбора определения местоположения
class ChooseLocationDialog:DialogFragment(), View.OnClickListener {
    var TAG = "ChooseLocationDialog"
    val APP_PREFERENCES: String = "appSetting"
    val FRAGMENT = R.layout.dialog_location
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v=inflater!!.inflate(FRAGMENT,null)
        v.btnChooseLocation.setOnClickListener(this)
        v.btnSearchLocation.setOnClickListener(this)
        v.btn_cancel.setOnClickListener(this)
        return v
    }
    override fun onClick(p0: View?) {
        var  main=activity as MainActivity
        if (p0!=null){
            when(p0){
                btn_cancel->dismiss()
                btnChooseLocation->{
                    dismiss()
                    //main.replaceFragment(MapViewFragment(),"MAps")
                    val PLACE_PICKER_REQUEST = 1
                    val builder = PlacePicker.IntentBuilder()
                    main.startActivityForResult(builder.build(main), PLACE_PICKER_REQUEST);
                }
                btnSearchLocation->{
                    dismiss()
                    main.isLocatinSearch=true
                    main.dialog?.show()
                    main.replaceFragment(TimeNamazFragment(),TimeNamazFragment().TAG)
                }
            }
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
    }
    override fun onStart() {
        super.onStart()
        if(getDialog()==null) {return;}
        getDialog().window.setTitle("Выберите определение местоположения");
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}


