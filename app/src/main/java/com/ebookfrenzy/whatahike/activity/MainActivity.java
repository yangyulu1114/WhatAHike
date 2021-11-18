package com.ebookfrenzy.whatahike.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.io.File;
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

        //test get comments
        RestAPI.getComments("1", new Listener<List<Comment>>() {
            @Override
            public void onSucceess(List<Comment> data) {
                Log.v("bush", "getComments onSucceess " + Thread.currentThread().getName());
                for (Comment comment : data) {
                    Log.v("bush", String.format("comment: %s", comment.toString()));
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("bush", "fail", e);
            }
        });
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
    }

    @Override
    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            onAuthCompleted(User.getCurrentUser());
        } else {
            // action if sign in failed;
        }
    }

    //if need to request permissions, extends BaseActivity and override function getRequestedPermissions()
    @Override
    String[] getRequestedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

   //test insert comment
    public void onMyClick(View view) {
        Comment comment = new Comment(User.getCurrentUser().getEmail());
        comment.setText("hello");
        comment.setTimeStamp(System.currentTimeMillis());
        comment.setTrailId("1");
        List<File> images = new ArrayList<>();
        images.add(new File("/sdcard/DCIM/Camera/20211117_150958.jpg"));
        images.add(new File("/sdcard/DCIM/Camera/20211118_102725.jpg"));
        RestAPI.postComment(comment, images, new Listener<Void>() {
            @Override
            public void onSucceess(Void data) {
                Log.v("bush", "postComment onSucceess " + Thread.currentThread().getName());
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }
}