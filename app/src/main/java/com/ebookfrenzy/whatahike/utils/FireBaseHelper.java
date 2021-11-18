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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FireBaseHelper<T extends FireBaseModel> {
    private final DatabaseReference mDatabase;

    public FireBaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void insert(T data) throws Exception {
        String name = data.getModelName();
        mDatabase.child(name).child(data.getKey()).setValue(data);
    }

    public void update(T data) throws Exception {

    }

    public List<T> query(Map<String, String> map) {
        return Collections.emptyList();
    }

    public static void login() {

    }

    public static void upload(File file, UploadListener uploadListener) {
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
