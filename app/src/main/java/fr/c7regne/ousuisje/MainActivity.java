package fr.c7regne.ousuisje;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.SSLContext;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity implements DialogNumber.DialogListener, SmsReceiveListener{
    private static final int MY_PERMISSION_REQUEST_RECEIVE_SMS=0;
    private static final int MY_PERMISSION_REQUEST_SEND_SMS=50;

    private GPS gps;
    private static final String TAG = "Test";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private TextView phone;
    private Button btnSendSMS,settingSMS,urgenceSms;
    private TextureView textureView;
    private static final SparseIntArray ORIENT=new SparseIntArray();
    static {
        ORIENT.append(Surface.ROTATION_0,90);
        ORIENT.append(Surface.ROTATION_90,0);
        ORIENT.append(Surface.ROTATION_180,270);
        ORIENT.append(Surface.ROTATION_270,180);
    }

    //to get the image
    private String IdCam;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageSize;
    private ImageReader imageReader;

    // for saving file and send
    private File file;
    public static final int REQ_CAM_PERM = 200;
    private boolean isFlashSupported;
    private Handler handler;
    private HandlerThread thread;

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    private static final int PERMISSONS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View v = findViewById(R.id.layoutView);
        sensorDetection();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                Log.v("permission", "permission denied to ACCESS_FINE_LOCATION - requesting it");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_RECEIVE_SMS);
            }
        }

        gps = new GPS(this,v);
        SMSSender.setView(v);
        //to set num fo debug
        SMSSender.setNumber("0781071100");
        btnSendSMS = (Button) findViewById(R.id.btn_sms);
        btnSendSMS.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                try{
                    SMSSender.sendSMS(getApplicationContext(), "Voici ma position: latitude=" + gps.getLatitude()+ " , longitude=" + gps.getLongitude());
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Can't have position, please retry", Toast.LENGTH_SHORT).show();
                }

            }
        });
        settingSMS = (Button) findViewById(R.id.settingSMS);
        settingSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //"0781071100"
                openDialogNumber();
            }
        });
        urgenceSms =findViewById(R.id.urgenceSMS);
        urgenceSms.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //takePicture();
                SMSSender.sendSMS(getApplicationContext(), "AU SECOURS AIDEZ MOI , COORD : latitude=" + gps.getLatitude()+ " , longitude=" + gps.getLongitude()+"\nC'est urgent !!!!!!!!!!!!");
                Toast.makeText(MainActivity.this, "Sending an urgence SMS", Toast.LENGTH_SHORT).show();
            }
        });

        textureView =findViewById(R.id.imageView);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        new FallDetection(this,mSensorManager, mAccelerometer);

    }//onCreate()

    private void openDialogNumber() {
        DialogNumber dialogNumber=new DialogNumber();
        dialogNumber.show(getSupportFragmentManager(),"dialog");
    }
    @Override
    public void applyTexts(String phoneNumber) {
        SMSSender.setNumber(phoneNumber);
    }
    @Override
    public void returnSMS(String phoneNumber, String phoneMessage) {
        SMSSender.setNumber(phoneNumber);
        SMSSender.setMessage(phoneMessage);
        SMSSender.sendSMS(this, "Voici ma position: latitude=" + gps.getLatitude() + " , longitude=" + gps.getLongitude());
    }



    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            IdCam = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(IdCam);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageSize = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQ_CAM_PERM);
                return;
            }
            manager.openCamera(IdCam,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        thread.quitSafely();
        try{
            thread.join();
            thread= null;
            handler= null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        thread = new HandlerThread("Camera Background");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePicture() {
        if(cameraDevice == null)
            return;
        CameraManager manager= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics cameraCharacteristics=manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] sjpeg = null;
            if(cameraCharacteristics != null)
                sjpeg = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            int w = 300;
            int h = 150;
            if (sjpeg != null && sjpeg.length>0){
                w=sjpeg[0].getHeight();
                h=sjpeg[0].getHeight();
            }

            // prepare to display the image on the texture view
            ImageReader reader=ImageReader.newInstance(w,h,ImageFormat.JPEG,1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));

            /// take the picture just one and in auto mode, flash auto, no adding features
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest((CameraDevice.TEMPLATE_STILL_CAPTURE));
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // check if the device is rotate and get the image in the correct rotation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENT.get(rotation));

            // save to file for sending after
            // create name of file that will be stored in external storage
            file = new File(Environment.getExternalStorageDirectory()+"/"+UUID.randomUUID().toString()+".jpeg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image =null;
                    try {
                        Log.i("Text","1");
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException a){
                        a.printStackTrace();
                    }finally {
                        if(image!=null) image.close();
                    }
                }
                private void save(byte[] bytes) throws IOException{
                    OutputStream outputStream=null;
                    try {
                        Log.i("Text","2");
                        outputStream=new FileOutputStream(file);
                        outputStream.write(bytes);
                    }finally {
                        if(outputStream != null) outputStream.close();
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, handler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Log.i("Text","3");
                    Toast.makeText(MainActivity.this,"Saved "+file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();

                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(),captureListener, handler );
                    } catch (CameraAccessException e) {
                        Log.i("Text","4");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, handler);

        }catch (CameraAccessException e){
            Log.i("Text","5");
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageSize.getWidth(),imageSize.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if(cameraDevice ==null) return;
                    cameraCaptureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(MainActivity.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            }, null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        };
    }

    private void updatePreview() {
        if(cameraDevice == null) Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),null,handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void sensorDetection () {

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE );
        List<Sensor> deviceSensors = mSensorManager.getSensorList ( Sensor.TYPE_ALL );
        if( deviceSensors != null && ! deviceSensors.isEmpty () ) {
            for ( Sensor mySensor : deviceSensors ) {
                Log.v( TAG , " info :" + mySensor.toString () );
            }
            if ( mSensorManager.getDefaultSensor ( Sensor.TYPE_ACCELEROMETER ) != null ){
                Log.v( TAG , " info : Accelerometer found !");
                mAccelerometer = mSensorManager.getDefaultSensor ( Sensor.TYPE_ACCELEROMETER );

            }
            else {
                Log.v( TAG , " info : Accelerometer not found !");
            }
        }
    }



}
