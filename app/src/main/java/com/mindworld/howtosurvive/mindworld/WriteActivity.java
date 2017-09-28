package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindworld.howtosurvive.mindworld.models.TextFile;

import java.io.File;
import java.io.FileOutputStream;

public class WriteActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize Firebase Cloud Storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //initialize Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void sendMemory(View view) {
        EditText memoryText = (EditText) findViewById(R.id.memory_text);
        String text = memoryText.getText().toString();

        String filename = "memory.txt";
        FileOutputStream outputStream;

        try {
            // write text to memory.txt
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();

            // get memory.txt's URI
            File file = new File(getFilesDir() + "/" + filename);
            Uri fileUri = Uri.fromFile(file);
            // build memory.txt metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();
            // upload memory.txt to Firebase Cloud Storage
            StorageReference memoryRef = mStorageRef.child(fileUri.getLastPathSegment());
            UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

            // delete memory.txt
            file.delete();

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
