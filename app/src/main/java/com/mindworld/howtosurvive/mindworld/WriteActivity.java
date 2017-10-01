package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static com.mindworld.howtosurvive.mindworld.MainActivity.mUserId;

public class WriteActivity extends AppCompatActivity {
    private String mUserId;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    String mLocality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize User ID from Firebase Authentication
        mUserId = getIntent().getStringExtra(MainActivity.EXTRA_USER_ID);
        // initialize Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // obtain user locality
        mLocality = getIntent().getStringExtra(MainActivity.EXTRA_USER_LOCALITY);
    }

    public void sendMemory(View view) {
        EditText memoryText = (EditText) findViewById(R.id.memory_text);
        String text = memoryText.getText().toString();

        EditText memoryName = (EditText) findViewById(R.id.memory_name);
        final String filename = memoryName.getText().toString() + ".txt";

        FileOutputStream outputStream;

        try {
            // write text to <mFilename>.txt
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();

            // get <mFilename>.txt URI
            File file = new File(getFilesDir() + "/" + filename);
            Uri fileUri = Uri.fromFile(file);
            // build <mFilename>.txt metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();
            // upload memory to Firebase Cloud Storage
            StorageReference memoryRef = mStorageRef.child("user/" + MainActivity.mUserId + "/" + fileUri.getLastPathSegment());
            UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

            // delete <mFilename>.txt
            file.delete();

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Upload failed.", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Showing toast message after done uploading.
                    Toast.makeText(getApplicationContext(), "Upload succeed.", Toast.LENGTH_LONG).show();

                    DatabaseReference databaseReference;
                    @SuppressWarnings("VisibleForTests")
                    TextFile txt = new TextFile(filename, mLocality, null,
                            taskSnapshot.getDownloadUrl().toString(), mUserId);
                    databaseReference = mDatabase.child("text").push();
                    databaseReference.setValue(txt);
                }
            });

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
