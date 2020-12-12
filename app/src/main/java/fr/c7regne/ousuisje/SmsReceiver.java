package fr.c7regne.ousuisje;
/*
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast


        //V1

        SmsMessage[] msgs = null;
        String messageReceived = "";
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] sms = (Object[]) bundle.get("pdus");
            for (Object sm : sms) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                messageReceived += "SMS From: " + address + "\n";
                messageReceived += smsBody + "\n";


            }
            Toast.makeText(context, messageReceived, Toast.LENGTH_SHORT).show();


        }
    }
}*/


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static android.widget.Toast.makeText;


public class SmsReceiver extends BroadcastReceiver {

    //V2

    private static final  String SMS_RECEIVED = "android.provide.Telephony.SMS_RECEIVED";
    private static final String TAG="SmsBroadcastReceiver";
    String msg, number = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG", "Intent Received: "+msg);
        if (intent.getAction()==SMS_RECEIVED)
        {
            Bundle bundle=intent.getExtras();
            if (bundle != null){
                Object[] mypdu = (Object[]) bundle.get("mypdu");
                final SmsMessage[] message= new SmsMessage[mypdu.length];

                        for (int i=0; i<mypdu.length;i++){
                            if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M){
                                String format = bundle.getString("format");
                                message[i] = SmsMessage.createFromPdu((byte[])mypdu[i],format);

                            }
                            else
                            {
                                message[i]=SmsMessage.createFromPdu((byte[]) mypdu[i]);
                            }
                            msg = message[i].getMessageBody();
                            number=message[i].getOriginatingAddress();

                        }
                        Toast.makeText(context,"Message: "+msg+"\nNumber :"+number, Toast.LENGTH_LONG).show();


            }
        }

    }
}

   /*

    //V3

    public void onReceive(Context context, Intent intent) {
        Log.i("test","message reçu");
        Toast.makeText(context, "toto",Toast.LENGTH_LONG).show();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] sms = (Object[]) bundle.get("pdus");
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {

                Toast.makeText(context, "titi",Toast.LENGTH_LONG).show();
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";
            }

            Toast.makeText(context, smsMessageStr, Toast.LENGTH_LONG).show();


        }
    }*/


    /*

    //V4

   // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    public void onReceive(Context context, Intent intent){
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] sms = (Object[]) bundle.get("pdus");

                for (int i = 0; i < sms.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);

                    Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, Toast.LENGTH_LONG).show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

}*/