package fr.c7regne.ousuisje;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class SMSSender {
    private static String number;
    //send SMS
    public static void sendSMS(Context c,String msg) {
        final int PERMISSION_REQUEST_CODE = 1;
        SmsManager sm = SmsManager.getDefault();
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {
            sm.sendTextMessage(number, null, msg, null, null);
        }
        Toast.makeText(c, msg,Toast.LENGTH_SHORT).show();


    }

    public static void setNumber(String n){
        number=n;
    }

    public static void receiveSMS() {

    }


}
