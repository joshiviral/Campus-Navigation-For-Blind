package com.example.dummy.campusnavforblind;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.Locale;

public class QRActivity extends AppCompatActivity {

    SurfaceView cameraPreview; // cameraview that will scan qr code
    TextView textView; // textview to print scanned qr code
    BarcodeDetector barcodeDetector; // barcode scanner
    CameraSource cameraSource;
    TextToSpeech tt; // this variable is for googlespeak
    final int RequestCameraPermissionID = 1001;
    Button b;

    // request for cameraview
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                // if premission granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return; // if no permission granted, return
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder()); // start camerasource
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        textView = (TextView) findViewById(R.id.textView);

        // building barcode detector
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        // pass barcode detector to cmerasource , to enable qr scanner
        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build();

        //start qr scanner
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            // on surface creation, ask for camera scan
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //if no camera Permission,return
                    ActivityCompat.requestPermissions(QRActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);

                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                //cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            // on barcode detection
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> qrcode = detections.getDetectedItems(); // store scanned qr code data to sparse array
                // if qrcode is not null
                if (qrcode.size() != 0) {
                   // set scanned data to textview
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            //Create Vibrator
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000); // vibrate phone
                            textView.setText(qrcode.valueAt(0).displayValue);
                            tt = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {


                                public void onInit(int s) {
                                    // Toast.makeText(getApplicationContext(), Integer.toString(s), Toast.LENGTH_SHORT).show();

                                    if (s != TextToSpeech.ERROR) {
                                        tt.setLanguage(Locale.CANADA); // set language to canadian english
                                        //  Toast.makeText(getApplicationContext(), "Language is set", Toast.LENGTH_SHORT).show();
                                        // speak out qr data value
                                        tt.speak(qrcode.valueAt(0).displayValue, TextToSpeech.QUEUE_FLUSH, null, null);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Google speech is not working", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                    try {
                        //1s = 1000
                        Thread.sleep(3000); // sleep for 3 sec, after speak out

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });



    }
}
