package com.ebookfrenzy.whatahike.model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class User {

    private final FirebaseUser mRemoteUser;

    public User(FirebaseUser remoteUser) {
        mRemoteUser = remoteUser;
    }

    public String getEmail() {
        return mRemoteUser.getEmail();
    }

    public String getDisplayName() {
        return mRemoteUser.getDisplayName();
    }

    public Uri getPhotoUrl() {
        return mRemoteUser.getPhotoUrl();
    }

    public void updatePhotoUrl(Uri uri) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        mRemoteUser.updateProfile(request);
    }

    public void updateDisplayName(String displayName) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
        mRemoteUser.updateProfile(request);
    }

    public static User getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser != null ? new User(firebaseUser) : null;
    }

    @Override
    public String toString() {
        return getEmail();
    }
}
