package fr.c7regne.ousuisje;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String messageSms = "";
        String smsNumber = "";
        String smsBody = "";

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
        if (smsBody.equals("Ousuisje")) {
            Toast.makeText(context, "send SMS in return", Toast.LENGTH_LONG).show();
            SMSSender.setNumber(smsNumber);
            SMSSender.sendSMS(context, "Voici ma position: latitude=" + GPS.getLatitude() + " , longitude=" + GPS.getLongitude());
        }
    }
}
