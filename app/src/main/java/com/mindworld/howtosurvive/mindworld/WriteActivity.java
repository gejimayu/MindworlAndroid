package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindworld.howtosurvive.mindworld.models.TextFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 2003;

    String mFilename;
    String mFilelocation;

    private String mUserId;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize User ID from Firebase Authentication
        mUserId = getIntent().getStringExtra(LoginActivity.EXTRA_UID);
        // initialize Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_COARSE_LOCATION_REQUEST_CODE: {
                // permission to read external storage granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCity();
                }
            }
        }
    }

    public void sendMemory(View view) {
        EditText memoryText = (EditText) findViewById(R.id.memory_text);
        String text = memoryText.getText().toString();

        EditText memoryName = (EditText) findViewById(R.id.memory_name);
        mFilename = memoryName.getText().toString();
        accessCity();

        FileOutputStream outputStream;

        try {
            // write text to <mFilename>.txt
            outputStream = openFileOutput(mFilename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();

            // get <mFilename>.txt's URI
            File file = new File(getFilesDir() + "/" + mFilename);
            Uri fileUri = Uri.fromFile(file);
            // build <mFilename>.txt's metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();
            // upload memory to Firebase Cloud Storage
            StorageReference memoryRef = mStorageRef.child("user/" + mUserId + "/" + fileUri.getLastPathSegment());
            UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

            // delete <mFilename>.txt
            file.delete();

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Upload failed ", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Showing toast message after done uploading.
                    Toast.makeText(getApplicationContext(), "File Uploaded Successfully ", Toast.LENGTH_LONG).show();

                    DatabaseReference db;

                    TextFile txt = new TextFile(mFilename, mFilelocation);
                    db = mDatabase.child("text").push();
                    db.setValue(txt);
                }
            });

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void accessCity() {
        // check permission to access user location
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            getCity();
        }
    }

    private void getCity() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                Log.d("LatLon", location.getLatitude() + "," + location.getLongitude());
                getUserGeoInfo(location.getLatitude(), location.getLongitude());
            }

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    Log.d("Lat-Lng", location.getLatitude() + "," + location.getLongitude());
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
                    // Obtain all information from addresses.get(0)
                    mFilelocation = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
