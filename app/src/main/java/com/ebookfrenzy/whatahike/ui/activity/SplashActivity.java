package com.ebookfrenzy.whatahike.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.List;


public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RestAPI.getTrails(null, null, new Listener<List<Trail>>() {
            @Override
            public void onSuccess(List<Trail> data) {
                signInIfNeeded();
            }

            @Override
            public void onFailed(Exception e) {
                signInIfNeeded();
            }
        });
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