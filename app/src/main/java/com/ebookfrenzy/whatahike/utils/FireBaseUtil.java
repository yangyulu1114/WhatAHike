package com.ebookfrenzy.whatahike.utils;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ebookfrenzy.whatahike.exception.FirebaseTimeoutException;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.FireBaseModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FireBaseUtil {
    private static final long TIMEOUT_MS = 2000;

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void uploadAsync(Uri uri, Listener<String> uploadListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + "-" + uri.getLastPathSegment());
        File file = null;
        try {
            file = BitmapUtil.saveBitmapToFile(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("bush", "upload file uri " + Uri.fromFile(file));
        Log.v("bush", "file size " + file.length());

        UploadTask uploadTask = fileRef.putFile(Uri.fromFile(file));
       // UploadTask uploadTask = fileRef.putFile(uri);

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
                    uploadListener.onSuccess(downloadUri.toString());
                } else {
                    uploadListener.onFailed(task.getException());
                }
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("bush", "delay cancel");
                if (!uploadTask.isComplete()) {
                    Log.v("bush", "cancel");
                    uploadTask.cancel();
                    uploadListener.onFailed(new FirebaseTimeoutException(null));
                }
            }
        }, TIMEOUT_MS);
    }

    public static String uploadSync(Uri uri) throws Exception {
        UploadListener listener = new UploadListener();
        uploadAsync(uri, listener);
        return listener.getResult();
    }

    private static class UploadListener implements Listener<String> {

        private String mResult;
        private Exception mException;
        private final CountDownLatch mLatch = new CountDownLatch(1);

        @Override
        public void onSuccess(String data) {
            mResult = data;
            mLatch.countDown();
        }

        @Override
        public void onFailed(Exception e) {
            mException = e instanceof FirebaseTimeoutException ? e : new UploadException(e);
            mLatch.countDown();
        }

        public String getResult() throws Exception {
            mLatch.await();
            if (mException != null) {
                throw mException;
            }
            return mResult;
        }
    }
}
