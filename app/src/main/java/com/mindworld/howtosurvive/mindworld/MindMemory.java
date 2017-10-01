package com.mindworld.howtosurvive.mindworld;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindworld.howtosurvive.mindworld.models.ImageFile;
import com.mindworld.howtosurvive.mindworld.models.TextFile;
import com.mindworld.howtosurvive.mindworld.models.VideoFile;

import java.io.File;

public class MindMemory {
    private Context mContext;

    private String mUserId;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private String mFileName;
    private String mLocality;

    public MindMemory(Context context, String userId, StorageReference storageRef, DatabaseReference databaseRef, String locality) {
        mContext = context;
        mUserId = userId;
        mStorageRef = storageRef;
        mDatabaseRef = databaseRef;
        mLocality = locality;
    }


    public Intent getMemory() {
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

        return intent;
    }

    public void uploadMemory(Intent data) {
        // get selected file URI
        final Uri fileUri = data.getData();
        String fileUriString = fileUri.toString();

        // get selected file cursor
        File file = new File(fileUriString);
        String path = file.getAbsolutePath();
        Cursor fileCursor = mContext.getContentResolver().query(fileUri, null, null, null, null);

        // get file name
        if (fileUriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver().query(fileUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (fileUriString.startsWith("file://")) {
            mFileName = file.getName();
        }

        // build file metadata
        final String mimetype = mContext.getContentResolver().getType(fileUri);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mimetype)
                .build();
        // upload file to Firebase Cloud Storage
        StorageReference memoryRef = mStorageRef.child("user/" + mUserId + "/" + fileUri.getLastPathSegment());
        UploadTask uploadTask = memoryRef.putFile(fileUri, metadata);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(mContext.getApplicationContext(), "Upload failed.", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Showing toast message after done uploading.
                Toast.makeText(mContext.getApplicationContext(), "Upload succeed.", Toast.LENGTH_LONG).show();

                DatabaseReference databaseReference;
                if (mimetype.contains("image")) {
                    @SuppressWarnings("VisibleForTests")
                    ImageFile imageUploadInfo = new ImageFile(mFileName, mLocality, fileUri.toString(),
                            taskSnapshot.getDownloadUrl().toString(), mUserId);
                    //push into database
                    databaseReference = mDatabaseRef.child("image").push();
                    databaseReference.setValue(imageUploadInfo);
                } else if (mimetype.contains("text")) {
                    @SuppressWarnings("VisibleForTests")
                    TextFile textUploadInfo = new TextFile(mFileName, mLocality, fileUri.toString(),
                            taskSnapshot.getDownloadUrl().toString(), mUserId);
                    // push into database
                    databaseReference = mDatabaseRef.child("text").push();
                    databaseReference.setValue(textUploadInfo);
                } else if (mimetype.contains("video")) {
                    @SuppressWarnings("VisibleForTests")
                    VideoFile videoUploadInfo = new VideoFile(mFileName, mLocality, fileUri.toString(),
                            taskSnapshot.getDownloadUrl().toString(), mUserId);
                    // push into database
                    databaseReference = mDatabaseRef.child("video").push();
                    databaseReference.setValue(videoUploadInfo);
                }
            }
        });
    }
}
