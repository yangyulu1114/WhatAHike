package com.ebookfrenzy.whatahike.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements ActivityResultCallback<FirebaseAuthUIAuthenticationResult> {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(), this);
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
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();

            signInLauncher.launch(signInIntent);
            finish();
        } else {
            onAuthCompleted(User.getCurrentUser());
        }
    }

    private void onAuthCompleted(User user) {
        //action after sign in
        Log.v("bush", String.format("onAuthCompleted: %s", user.toString()));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
        Log.v("bsuh", "result code " + result.getResultCode());

        if (result.getResultCode() == RESULT_OK) {
            onAuthCompleted(User.getCurrentUser());
        } else {
            // action if sign in failed;
        }
    }
}