package fr.c7regne.ousuisje;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST_RECEIVE_SMS=0;

    Button btnSendSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to RECEIVE_SMS - requesting it");
                String[] permissions = {Manifest.permission.RECEIVE_SMS};
                requestPermissions(permissions, MY_PERMISSION_REQUEST_RECEIVE_SMS);
            }
        }

        btnSendSMS = (Button) findViewById(R.id.btn_sms);
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                sendSMS();
            }
        });


        /*//receive SMS
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String address = extras.getString("address");
            String message = extras.getString("message");
            TextView addressField = (TextView) findViewById(R.id.phone);
            TextView messageField = (TextView) findViewById(R.id.message);
            addressField.setText(address);
            messageField.setText(message);
        }*/
    }//onCreate()

    //send SMS


    public void sendSMS() {
        final int PERMISSION_REQUEST_CODE = 1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        String latitude = "lat";
        String longitude = "long";
        SmsManager sm = SmsManager.getDefault();
        String number = "0651391408";
        String msg = "Voici ma position: latitude=" + latitude + " , longitude=" + longitude;
        sm.sendTextMessage(number, null, msg, null, null);
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }



   



}
