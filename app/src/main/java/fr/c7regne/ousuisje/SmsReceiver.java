package fr.c7regne.ousuisje;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver {

    private smsReceiveListener mSmsReceiveListener;


    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String messageSms = "";
        String smsNumber = "";
        String smsBody = "";


       // mSmsReceiveListener = new smsReceiveListener;

        if (bundle != null) {
            Object[] sms = (Object[]) bundle.get("pdus");


            for (int i = 0; i < sms.length; ++i) {

                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                smsNumber = smsMessage.getOriginatingAddress().toString();


                messageSms += "SMS From: " + smsNumber + "\n";
                messageSms += smsBody + "\n";
            }
        }


        Toast.makeText(context, messageSms, Toast.LENGTH_LONG).show();
        //GPS gps = new GPS(context);
        if (smsBody.equals("ousuisje")) {

            Toast.makeText(context,"send SMS in return", Toast.LENGTH_LONG).show();
            //SMSSender.setNumber(smsNumber);
            mSmsReceiveListener.returnSMS(smsNumber,smsBody);
            //SMSSender.sendSMS(context, "Voici ma position: latitude=" + gps.getLatitude() + " , longitude=" + gps.getLongitude());

        }
        else{
            Toast.makeText(context, "can't send sms", Toast.LENGTH_LONG).show();
        }
    }
    public interface smsReceiveListener{
        void returnSMS(String phoneNumber, String phoneMessage);
    }
}
