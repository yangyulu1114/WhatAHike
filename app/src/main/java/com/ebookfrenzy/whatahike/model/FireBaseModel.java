package com.ebookfrenzy.whatahike.model;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.ebookfrenzy.whatahike.exception.FirebaseTimeoutException;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class FireBaseModel {
    private static final long TIMEOUT_MS = 15000;

    private final DatabaseReference mDatabase;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public FireBaseModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void insert(Listener<Void> listener) {
        final long timestamp = System.currentTimeMillis();
        DatabaseReference databaseReference = mDatabase.child(getModelName());
        for (String key : keys()) {
            databaseReference = databaseReference.child(key);
        }
        databaseReference.setValue(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                listener.onSuccess(unused);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (System.currentTimeMillis() - timestamp > TIMEOUT_MS) {
                    listener.onFailed(new FirebaseTimeoutException(e));
                } else {
                    listener.onFailed(e);
                }
            }
        });

        DatabaseReference finalDatabaseReference = databaseReference;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finalDatabaseReference.getDatabase().purgeOutstandingWrites();
            }
        }, TIMEOUT_MS);
    }

    public <T> void query(List<String> args, Listener<List<T>> listener) {
        List list = new ArrayList<>();
        DatabaseReference databaseReference = mDatabase.child(getModelName());
        for (String key : args) {
            databaseReference = databaseReference.child(key);
        }
        final Class clazz = getClass();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        list.add(snapshot.getValue(clazz));
                    }
                    listener.onSuccess(list);
                } else {
                    listener.onFailed(task.getException());
                }
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onFailed(new FirebaseTimeoutException(null));
            }
        }, TIMEOUT_MS);
    }

    public abstract String getModelName();
    public abstract String[] keys();
}
