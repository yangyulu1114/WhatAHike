package com.ebookfrenzy.whatahike.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.FireBaseHelper;
import com.ebookfrenzy.whatahike.utils.UploadListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION = 1;

    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (requestPermission()) {
            uploadImage();
        }

        List<Trail> trails = RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                return trail.getCity() == "Fremont";
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                return o1.getDifficulty() - o2.getDifficulty();
            }
        });
    }

    private void uploadImage() {
        FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.v("bush", "signIn onSuccess");
                doUpload();
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("bush", "signInAnonymously:FAILURE", exception);
                    }
                });

    }

    private void doUpload() {
        File file = new File("/sdcard/DCIM/Camera/20211117_150958.jpg");
        Log.v("bush", "file = " + file.getPath());
        try {
            FireBaseHelper.upload(file, new UploadListener() {
                @Override
                public void onSucceess(String url) {
                    Log.v("bush", "url: " + url);
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("bush", "fail", e);
                }
            });
        } catch (Exception e) {
            Log.e("bush", e.toString(), e);
        }
    }

    private class MyFilter implements Filter {

        private String mName;

        public MyFilter(String name) {
            mName = name;
        }

        @Override
        public boolean pass(Object trail) {
            return false;
        }

    }

    private boolean requestPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadImage();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}