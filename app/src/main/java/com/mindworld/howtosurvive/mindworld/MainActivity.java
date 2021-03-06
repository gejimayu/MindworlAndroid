package com.mindworld.howtosurvive.mindworld;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_REPLY = "com.mindworld.howtosurvive.mindworld.extra.REPLY";
    public static final String EXTRA_USER_ID = "com.mindworld.howtosurvive.mindworld.extra.USER_ID";
    public static final String EXTRA_USER_LOCALITY = "com.mindworld.howtosurvive.mindworld.extra.USER_LOCALITY";
    public static final String EXTRA_NEWS = "com.mindworld.howtosurvive.mindworld.extra.NEWS";

    private static final int READ_FILE_BROWSER_REQUEST_CODE = 1001;

    private static final int ACCESS_COARSE_LOCATION_PERMISSION_CODE = 2003;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 2002;

    private static final String SHARED_PREF_FILE = "com.mindworld.howtosurvive.mindworld";
    public static String mUserId;

    Sensor mAccelerometer;
    Sensor mMagnetometer;
    float[] mGravity;
    float[] mGeomagnetic;
    float mPitch;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    View mView;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private SharedPreferences mPreferences;
    private SensorManager mSensorManager;

    private UserLocation mUserLocation;
    private MindMemory mMindMemory;

    private static String getJSON() {
        HttpsURLConnection connection = null;

        try {
            URL url = new URL("https://mindworld-4964e.firebaseio.com/env.json");
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();

            return stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String data = getJSON();
        if (data != null && data.contains("night")) {
            setTheme(R.style.AppThemeDark);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Set the text for each tab.
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_texts_label));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_images_label));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_videos_label));
        // Set the tabs to fill the entire layout.
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        mViewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // initialize User ID from Firebase Authentication
        mUserId = getIntent().getStringExtra(LoginActivity.EXTRA_USER_ID);
        // initialize Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // initialize shared preferences
        mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        final SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        // check if the user use the app for the first time
        if (mPreferences.getBoolean("FIRST_TIMER", true)) {
            Toast.makeText(getApplicationContext(), "Welcome to Mindworld.", Toast.LENGTH_LONG).show();

            preferencesEditor.putBoolean("FIRST_TIMER", false).apply();
        }

        // initialize device sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // initialize UserLocation
        mUserLocation = new UserLocation(this);
        findUserLocality();

        // initialize MindMemory
        mMindMemory = new MindMemory(this, mUserId, mStorageRef, mDatabaseRef, mUserLocation.getLocality());
    }

    @Override
    public void onResume() {
        super.onResume();

        String data = getJSON();
        if (data != null && data.contains("night")) {
            setTheme(R.style.AppThemeDark);
        }

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            finish();
            startActivity(getIntent());

            return true;
        } else if (item.getItemId() == R.id.sign_out) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_REPLY, "OK");
            setResult(RESULT_OK, intent);

            finish();

            return true;
        } else if (item.getItemId() == R.id.read_news) {
            String news = "news";
            Intent intent = new Intent(this, NewsActivity.class);
            intent.putExtra(EXTRA_NEWS, news);
            startActivity(intent);

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_CODE: {
                // permission to read external storage granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(mMindMemory.getMemory(), READ_FILE_BROWSER_REQUEST_CODE);
                }
            }
            case ACCESS_COARSE_LOCATION_PERMISSION_CODE: {
                // permission to read external storage granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mUserLocation.findLocation();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_FILE_BROWSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMindMemory.uploadMemory(data);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = sensorEvent.values;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = sensorEvent.values;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                mPitch = orientation[1]; // orientation contains: azimut, pitch and roll
                Float temp = new Float(mPitch);

                // Log.d("Sensor", temp.toString());

                if (mPitch > 0.3) {
                    writeMemory(mView);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void writeMemory(View view) {
        Intent intent = new Intent(this, WriteActivity.class);
        intent.putExtra(EXTRA_USER_ID, mUserId);
        intent.putExtra(EXTRA_USER_LOCALITY, mUserLocation.getLocality());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void addMemory(View view) {
        // check permission to read external storage
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        } else {
            startActivityForResult(mMindMemory.getMemory(), READ_FILE_BROWSER_REQUEST_CODE);
        }
    }

    public void findUserLocality() {
        // check permission to access user location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_PERMISSION_CODE);
        } else {
            mUserLocation.findLocation();
        }
    }
}
