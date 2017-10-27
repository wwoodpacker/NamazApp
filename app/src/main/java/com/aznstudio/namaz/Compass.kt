package com.aznstudio.namaz

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

import com.google.android.gms.maps.model.LatLng

class Compass(context: Context, imageView: ImageView) : SensorEventListener {
    private val sensorManager: SensorManager
    private val gsensor: Sensor
    private val msensor: Sensor
    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f
    private var currentPoint: LatLng? = null
    private var targetPoint: LatLng? = null

    // compass arrow to rotate
    private var arrowView: ImageView? = null

    init {
        arrowView = imageView
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //достаем датчики
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun start() {
        //активируем датчики
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        //деактивируем датчики
        sensorManager.unregisterListener(this)
    }

    //поворачиваем стрелку компасса
    private fun adjustArrow() {
        if (arrowView == null) {
            Log.i(TAG, "arrow view is not set")
            return
        }

        val an = RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f)
        currentAzimuth = azimuth

        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true

        arrowView!!.startAnimation(an)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f

        //делаем расчет положения стрелки
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2]
            }

            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2]
            }

            val R = FloatArray(9)
            val I = FloatArray(9)
            val success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat() // orientation

                //добавляем поправку на направление к целевой точке
                azimuth = (azimuth - calculatePointAlpha()) % 360
                adjustArrow()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    fun setCurrentPoint(currentPoint: LatLng) {
        this.currentPoint = currentPoint
    }

    fun setTargetPoint(targetPoint: LatLng) {
        this.targetPoint = targetPoint
    }

    //расчет поправки по направлению к целевой точке
    private fun calculatePointAlpha(): Float {
        var alphaDegrees = 0f
        if (currentPoint != null && targetPoint != null) {
            val dLatitude = targetPoint!!.latitude - currentPoint!!.latitude
            val dLongitude = targetPoint!!.longitude - currentPoint!!.longitude
            alphaDegrees = Math.toDegrees(Math.atan2(dLongitude, dLatitude)).toFloat()
        }
        return alphaDegrees
    }

    companion object {
        private val TAG = "Compass"
    }
}
