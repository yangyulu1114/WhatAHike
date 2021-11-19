package com.ebookfrenzy.whatahike.model;

import android.util.Log;

import androidx.annotation.NonNull;

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

    private final DatabaseReference mDatabase;

    public FireBaseModel() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void insert(Listener<Void> listener) {
        Log.v("bush", "insert");
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
                listener.onFailed(e);
                Log.v("bush", "insert onFailure", e);
            }
        });
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
    }

    public abstract String getModelName();
    public abstract String[] keys();
}
