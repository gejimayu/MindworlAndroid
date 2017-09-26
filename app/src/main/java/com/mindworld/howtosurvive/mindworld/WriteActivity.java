package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

public class WriteActivity extends AppCompatActivity {
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorageRef = FirebaseStorage.getInstance().getReference();
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

            File file = new File(getFilesDir() + "/" + filename);

            // upload memory.txt to Firebase storage
            Uri fileUri = Uri.fromFile(file);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();
            StorageReference memoryRef = mStorageRef.child(fileUri.getLastPathSegment());
            UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
