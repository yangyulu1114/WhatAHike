package com.ebookfrenzy.whatahike.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BaseActivity extends AppCompatActivity {

    private static final int CODE_REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requestPermission(getRequestedPermissions())) {
            onAllPermissionsGranted();
        }
    }

    private boolean requestPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, CODE_REQUEST_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    String[] getRequestedPermissions() {
        return new String[0];
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODE_REQUEST_PERMISSIONS:
                boolean allGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
                if (!allGranted) {
                    finish();
                } else {
                    onAllPermissionsGranted();
                }
                break;
            default:
                break;
        }
    }

    void onAllPermissionsGranted() {

    }
}
