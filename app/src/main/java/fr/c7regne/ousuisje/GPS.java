package fr.c7regne.ousuisje;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPS {

    private static final String TAG = "Test";

    private TextView txtLatitude, txtLongitude,txtAccuracy;
    private Button btnUpdateGPS,btnStopGPS;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallBack;

    Context context;
    View v;

    public GPS(Context c, View view) {
        //set context and view
        context=c;
        v=view;
        // to show information and update
        txtLatitude=v.findViewById(R.id.latitude);
        txtLongitude=v.findViewById(R.id.longitude);
        txtAccuracy=v.findViewById(R.id.accuracy);
        btnUpdateGPS=v.findViewById(R.id.btnUpdateGPS);
        btnStopGPS=v.findViewById(R.id.btnStopGPS);

        // to get last gps coords
        locationRequest=new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getGPScoord();

        //for setting traking gps coord
        locationCallBack = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateCoords(locationResult.getLastLocation());
            }
        };

        //for stopping traking gps coord
        btnUpdateGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null);
            }
        });
        btnStopGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
                txtLatitude.setText("Not available");
                txtLongitude.setText("Not available");
                txtAccuracy.setText("Not available");
            }
        });
    }

    // get the gps coords
    private void getGPScoord(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateCoords(location);
                }
            });
        }else{
            Toast.makeText(context,"You must activate your geolocalisation", Toast.LENGTH_SHORT).show();
        }
    }

    //update the view with the new coords
    private void updateCoords(Location location) {
        txtLatitude.setText(String.valueOf(location.getLatitude()));
        txtLongitude.setText(String.valueOf(location.getLongitude()));
        txtAccuracy.setText(String.valueOf(location.getAccuracy()));
    }


}