package jp.slade.heartrate

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class MainActivity : Activity(), SensorEventListener {

    companion object {
        private const val TAG = "HeartRate"
        private const val REQ_BODY_SENSORS = 0
    }

    private lateinit var sm: SensorManager
    private var hr: Sensor? = null

    private lateinit var bpmView: TextView
    private lateinit var infoView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bpmView = findViewById(R.id.bpm)
        infoView = findViewById(R.id.info)

        Log.d(TAG, "onCreate")

        sm = getSystemService(SENSOR_SERVICE) as SensorManager
        hr = sm.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        if (hr == null) {
            Log.e(TAG, "No heart-rate sensor found")
            infoView.text = getString(R.string.no_hr_sensor)
            return
        }

        Log.d(TAG, "HR sensor found: ${hr!!.name}")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "BODY_SENSORS not granted, requesting")
            requestPermissions(
                arrayOf(Manifest.permission.BODY_SENSORS),
                REQ_BODY_SENSORS
            )
            infoView.text = getString(R.string.requesting_body_sensors)
            return
        }

        startSensor()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause → unregister listener")
        sm.unregisterListener(this)
    }

    private fun startSensor() {
        val s = hr ?: return

        Log.d(TAG, "Registering HR listener")

        sm.unregisterListener(this) // safety
        sm.registerListener(
            this,
            s,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        infoView.text = getString(
            R.string.sensor_info,
            s.name,
            s.vendor,
            s.resolution,
            s.maximumRange,
            s.minDelay
        )
    }

    override fun onSensorChanged(e: SensorEvent) {
        if (e.sensor.type != Sensor.TYPE_HEART_RATE) return

        val bpm = e.values[0]

        Log.v(TAG, "HR event: $bpm (accuracy=${e.accuracy})")

        bpmView.text = if (bpm > 0f) bpm.toInt().toString() else "--"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Accuracy changed: $accuracy")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult")

        if (requestCode == REQ_BODY_SENSORS &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "BODY_SENSORS granted")
            startSensor()
        } else {
            Log.e(TAG, "BODY_SENSORS denied")
            infoView.text = getString(R.string.body_sensors_denied)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy → unregister listener")
        sm.unregisterListener(this)
    }
}
