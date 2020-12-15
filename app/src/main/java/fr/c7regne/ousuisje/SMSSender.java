package fr.c7regne.ousuisje;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class SMSSender {
    private static String number="No Number";
    private static View view;
    private static Context context;
    private static TextView phone, message;
    //send SMS
    public static void sendSMS(Context c,String msg) {
        final int PERMISSION_REQUEST_CODE = 1;
        SmsManager sm = SmsManager.getDefault();
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {
            if (number.length()>=10) {
                sm.sendTextMessage(number, null, msg, null, null);
                Toast.makeText(c, "Send : " + msg, Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(c, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
        }

    }

    public static void setView(View view) {
        SMSSender.view = view;
        phone = view.findViewById(R.id.phone);
        message = view.findViewById(R.id.message);

    }

    public static void setNumber(String n){
        number=n;
        phone.setText(SMSSender.getNumber());
    }

    public static void setMessage(String msg) {
        message.setText(msg);
    }

    public static String  getNumber(){
        return number;
    }

    public static void receiveSMS() {

    }


}
