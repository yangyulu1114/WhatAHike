package com.ebookfrenzy.whatahike.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        signInIfNeeded();
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
        if (result.getResultCode() == RESULT_OK) {
            onAuthCompleted(User.getCurrentUser());
        } else {
            // action if sign in failed;
        }
    }
}