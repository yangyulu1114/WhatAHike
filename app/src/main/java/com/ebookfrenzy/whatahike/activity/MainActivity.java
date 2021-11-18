package com.ebookfrenzy.whatahike.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.FireBaseHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements ActivityResultCallback<FirebaseAuthUIAuthenticationResult> {

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Log.v("bush", String.format("onAuthCompleted: %s", user.toString()));
    }

    @Override
    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            onAuthCompleted(User.getCurrentUser());
        } else {
            // action if sign in failed;
        }
    }

    @Override
    String[] getRequestedPermissions() {
        return new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    public void onMyClick(View view) {
        Comment comment = new Comment(User.getCurrentUser().getEmail());
        comment.setText("hello");
        comment.setTimeStamp(System.currentTimeMillis());
        comment.setTrailId("1");
        List<String> imageUrl = new ArrayList<>();
        imageUrl.add("https://firebasestorage.googleapis.com/v0/b/whatahike-d3fe2.appspot.com/o/images%2F1637199022847-20211117_150958.jpg?alt=media&token=a5f52891-2913-4e1b-8f63-23ee59fb3092");
        imageUrl.add("https://firebasestorage.googleapis.com/v0/b/whatahike-d3fe2.appspot.com/o/images%2F1637209900698-20211117_150958.jpg?alt=media&token=91997563-55e9-4d46-b135-f7a426f868ed");
        comment.setImages(imageUrl);
        FireBaseHelper<Comment> fireBaseHelper = new FireBaseHelper<>();
        try {
            fireBaseHelper.insert(comment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}