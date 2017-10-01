package com.mindworld.howtosurvive.mindworld;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_UID = "com.mindworld.howtosurvive.mindworld.extra.UID";

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 2003;
    private static final int READ_FILE_BROWSER_REQUEST_CODE = 2001;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 2002;

    private String mUserId;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    Ringtone filename;
    String filelocation;
    String mimetype;
    Uri fileUri;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = MainActivity.this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Set the text for each tab.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_texts_label));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_images_label));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_videos_label));
        // Set the tabs to fill the entire layout.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
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

        // initialize Firebase Cloud Storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                fileUri = data.getData();
                // build file metadata
                mimetype = getContentResolver().getType(fileUri);
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType(mimetype)
                        .build();
                // upload file to Firebase Cloud Storage
                StorageReference memoryRef = mStorageRef.child("user/" + mUserId + "/" + fileUri.getLastPathSegment());
                UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

                // get file name
                filename = RingtoneManager.getRingtone(this, fileUri);

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
                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        // Showing toast message after done uploading.
                        Toast.makeText(getApplicationContext(), "File Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        DatabaseReference db;

                        if (mimetype.contains("image")) {
                            @SuppressWarnings("VisibleForTests")
                            ImageFile imageUploadInfo = new ImageFile(filename.getTitle(context), filelocation, fileUri.toString(),
                                    downloadUrl);
                            //push into database
                            db = mDatabase.child("image").push();
                            db.setValue(imageUploadInfo);
                        } else if (mimetype.contains("text")) {
                            @SuppressWarnings("VisibleForTests")
                            TextFile txt = new TextFile(filename.getTitle(context), filelocation, fileUri.toString(),
                                    downloadUrl);
                            // push into database
                            db = mDatabase.child("text").push();
                            db.setValue(txt);
                        } else if (mimetype.contains("video")) {
                            @SuppressWarnings("VisibleForTests")
                            VideoFile vid = new VideoFile(filename.getTitle(context), filelocation, fileUri.toString(),
                                    downloadUrl);
                            // push into database
                            db = mDatabase.child("video").push();
                            db.setValue(vid);
                        }
                    }
                });
            }
        }
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
            
        }
    }

}
