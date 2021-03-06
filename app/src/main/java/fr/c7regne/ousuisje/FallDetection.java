package fr.c7regne.ousuisje;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;import android.widget.Toast;

public class FallDetection implements SensorEventListener {
    private final Context c;
    float Ax,Ay, Az;
    float vAz= (float) 9.8;

    public FallDetection(Context context,SensorManager mSensorManager , Sensor sensor) {
        this.c=context;
        mSensorManager.registerListener(this , sensor , SensorManager.SENSOR_DELAY_NORMAL ) ;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Many sensors return 3 values , one for each axis .
        Ax = event.values [0];
        Ay = event.values [1];
        Az = event.values [2];
        // Do something with this sensor value .
        if (Math.abs(vAz-Az)>=15){
            Toast.makeText(c, "Device fall, sending message", Toast.LENGTH_SHORT).show();
            SMSSender.sendSMS(c,"AAAAAAAAAAAh Je tombe !!");
        }
        vAz=Az;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
