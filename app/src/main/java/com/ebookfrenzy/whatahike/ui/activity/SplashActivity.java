package com.ebookfrenzy.whatahike.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.User;


public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                signInIfNeeded();
            }
        }, 400);
    }

    private void signInIfNeeded() {
        if (User.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            onAuthCompleted(User.getCurrentUser());
        }
    }


    private void onAuthCompleted(User user) {
        startActivity(new Intent(this, MainActivity.class));

        finish();
    }

}