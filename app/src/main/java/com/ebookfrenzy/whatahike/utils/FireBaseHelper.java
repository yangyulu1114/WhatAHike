package com.ebookfrenzy.whatahike.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ebookfrenzy.whatahike.model.FireBaseModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FireBaseHelper {

    public static void upload(File file, Listener<String> uploadListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Uri fileUri = Uri.fromFile(file);
        StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + "-" + fileUri.getLastPathSegment());
        UploadTask uploadTask = fileRef.putFile(fileUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    uploadListener.onSucceess(downloadUri.toString());
                } else {
                    uploadListener.onFailed(task.getException());
                }
            }
        });
    }
}
