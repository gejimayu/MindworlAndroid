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
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindworld.howtosurvive.mindworld.models.ImageFile;
import com.mindworld.howtosurvive.mindworld.models.TextFile;
import com.mindworld.howtosurvive.mindworld.models.VideoFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_REPLY = "com.mindworld.howtosurvive.mindworld.extra.REPLY";
    public static final String EXTRA_UID = "com.mindworld.howtosurvive.mindworld.extra.UID";

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 2003;
    private static final int READ_FILE_BROWSER_REQUEST_CODE = 2001;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2002;

    private static final String FIRST_TIMER = "FIRST_TIMER";

    private static final String SHARED_PREF_FILE = "com.mindworld.howtosurvive.mindworld";

    String filename;
    String filelocation;
    String mimetype;

    Sensor mAccelerometer;
    Sensor mMagnetometer;
    float[] mGravity;
    float[] mGeomagnetic;
    float mPitch;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    View mView;

    private String mUserId;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private SharedPreferences mPreferences;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mUserId = getIntent().getStringExtra(LoginActivity.EXTRA_UID);
        // initialize Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        // initialize shared preferences
        mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        final SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        // check if the user use the app for the first time
        if (mPreferences.getBoolean(FIRST_TIMER, true)) {
            Toast.makeText(getApplicationContext(), "Welcome to Mindworld.", Toast.LENGTH_LONG).show();

            preferencesEditor.putBoolean(FIRST_TIMER, false).apply();
        }

        // initialize device sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (item.getItemId() == R.id.sign_out) {
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_REPLY, "OK");
            setResult(RESULT_OK, replyIntent);

            finish();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE: {
                // permission to read external storage granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getMemory();
                }
            }
            case ACCESS_COARSE_LOCATION_REQUEST_CODE: {
                // permission to read external storage granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCity();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_FILE_BROWSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // get selected file's URI
                Uri fileUri = data.getData();
                // build file's metadata
                mimetype = getContentResolver().getType(fileUri);
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType(mimetype)
                        .build();
                // upload file to Firebase Cloud Storage
                StorageReference memoryRef = mStorageRef.child("user/" + mUserId + "/" + fileUri.getLastPathSegment());
                UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

                // get file name
                filename = fileUri.getLastPathSegment();

                // get file location
                accessCity();

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed ", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        // Showing toast message after done uploading.
                        Toast.makeText(getApplicationContext(), "File Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        DatabaseReference db;
                        if (mimetype.contains("image")) {
                            @SuppressWarnings("VisibleForTests")
                            ImageFile imageUploadInfo = new ImageFile(filename, filelocation,
                                    taskSnapshot.getDownloadUrl().toString());
                            //push into database
                            db = mDatabaseRef.child("image").push();
                            db.setValue(imageUploadInfo);
                        } else if (mimetype.contains("text")) {
                            TextFile txt = new TextFile(filename, filelocation);
                            // push into database
                            db = mDatabaseRef.child("text").push();
                            db.setValue(txt);
                        } else if (mimetype.contains("video")) {
                            VideoFile txt = new VideoFile(filename, filelocation);
                            // push into database
                            db = mDatabaseRef.child("video").push();
                            db.setValue(txt);
                        }
                    }
                });
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

                Log.d("Sensor", temp.toString());

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
        intent.putExtra(EXTRA_UID, mUserId);

        startActivity(intent);
    }

    public void uploadMemory(View view) {
        // check permission to read external storage
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            getMemory();
        }
    }

    private void getMemory() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only texts, images, and videos using their MIME data types.
        String[] mimeTypes = {"text/plain", "image/*", "video/*"};
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, READ_FILE_BROWSER_REQUEST_CODE);
    }

    private void accessCity() {
        // check permission to access user location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            getCity();
        }
    }

    private void getCity() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                getUserGeoInfo(location.getLatitude(), location.getLongitude());
            }

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    getUserGeoInfo(location.getLatitude(), location.getLongitude());
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            // Set how often you want to request location updates and where you want to receive them
            locationManager.requestLocationUpdates(provider, 20000, 0, locationListener);
        }
    }

    private void getUserGeoInfo(double latitude, double longitude) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        if (Geocoder.isPresent()) {
            try {
                List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

                if (addresses.size() > 0) {
                    // obtain all information from addresses.get(0)
                    filelocation = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
