package com.lttn.womenapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class OnShake extends AppCompatActivity implements LocationListener {
    private SensorManager mSensorManager;
    LocationManager locationManager;
    Button btn;
    public static String address;
    private float shake; //Acceleration value differ from gravity
    private float mAccelCurrent;// Current acceleration value and gravity
    private float mAccelLast;// Last acceleration value and gravity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onshake);
       // btn = findViewById(R.id.btnGPS);

        ActivityCompat.requestPermissions(OnShake.this,new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS},
                PackageManager.PERMISSION_GRANTED);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);// truy cap vao cam bien gia toc
        shake = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        if (ContextCompat.checkSelfPermission(
                OnShake.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    OnShake.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);}
       // btn.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //  public void onClick(View v) {
else{
                getLocation();

       //     }
       // });
    }}
    @SuppressLint("MissingPermission")

    private void getLocation() {
        try{
            locationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,  OnShake.this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this,""+location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_SHORT).show();
        try{
            Geocoder geocoder=new Geocoder(this, Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            address=addresses.get(0).getAddressLine(0);
            Log.d("ad",address);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        //onSensorChanged() được gọi khi dữ liệu cảm biến thay đổi
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            shake = shake * 0.9f + delta;
            if(shake > 6) {
                Log.i("Send SMS", "");

                try {
                    // Toast.makeText(getApplicationContext(),GPS.address, Toast.LENGTH_LONG).show();

                    // Intent intent = getIntent();
                    //  String data = intent.getStringExtra("key");
                    // Log.d("key", data);
                    SQLiteDatabase db;
                    db = openOrCreateDatabase("NumDB", Context.MODE_PRIVATE, null);
                    Cursor c = db.rawQuery("SELECT * FROM details", null);
                   // c.moveToFirst();//den dong dau tap du lieu
                    while (c.moveToNext()) {
                        String num = c.getString(1);//gia tri sdt
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(num, null,
                                "Please help me. I need help immediately. This is where i am now:" + address, null, null);
                        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

                    }
                }


                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    @Override
    //Đăng kí Sensor listener trong hàm onResume()
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    // Hủy đăng kí trong hàm onPause() để tránh việc sử dụng không cần thiết và tiêt kiệm pin cho thiết bị
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }
}

