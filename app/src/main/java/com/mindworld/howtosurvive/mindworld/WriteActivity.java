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
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    String filename;
    String filelocation;

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

        EditText memoryName = (EditText) findViewById(R.id.memory_name);
        filename = memoryName.getText().toString();
        filelocation = "New York City";

        FileOutputStream outputStream;

        try {
            // write text to <filename>.txt
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();

            // get <filename>.txt's URI
            File file = new File(getFilesDir() + "/" + filename);
            Uri fileUri = Uri.fromFile(file);
            // build <filename>.txt metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("text/plain")
                    .build();
            // upload memory to Firebase Cloud Storage
            StorageReference memoryRef = mStorageRef.child("user/" + MainActivity.mUserId + "/" + fileUri.getLastPathSegment());
            UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

            // delete <filename>.txt
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
                    @SuppressWarnings("VisibleForTests")
                    TextFile txt = new TextFile(filename, filelocation, null,
                            taskSnapshot.getDownloadUrl().toString(), MainActivity.mUserId);
                    db = mDatabase.child("text").push();
                    db.setValue(txt);
                }
            });

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
