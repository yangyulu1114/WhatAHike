package com.ebookfrenzy.whatahike.ui.activity;

import android.net.Uri;
import android.os.Bundle;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.utils.Listener;

import android.util.Log;
import android.view.View;

import androidx.navigation.ui.AppBarConfiguration;


import com.ebookfrenzy.whatahike.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestActivity extends BaseActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        //get trails
        final List<Trail>[] trails = null;
        RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                //implement pass function
                return trail.getState().equals("CA") || trail.getName().contains("CA");
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                //implement comparator
                return 0;
            }
        }, new Listener<List<Trail>>() {
            @Override
            public void onSuccess(List<Trail> data) {
                trails[0] = data;
            }

            @Override
            public void onFailed(Exception e) {

            }
        });


        //get comments
        RestAPI.getComments("1", new Listener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
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

    //insert comment
    public void onMyClick(View view) {
        Comment comment = new Comment(User.getCurrentUser().getEmail());
        comment.setText("hello");
        comment.setTimeStamp(System.currentTimeMillis());
        comment.setTrailId("1");
        List<Uri> images = new ArrayList<>();
        File file1 = new File("/sdcard/DCIM/Camera/20211117_150958.jpg");
        File file2 = new File("/sdcard/DCIM/Camera/20211118_102725.jpg");
        images.add(Uri.fromFile(file1));
        images.add(Uri.fromFile(file2));

        RestAPI.postComment(comment, images, new Listener<Void>() {
            @Override
            public void onSuccess(Void data) {
                // comment succeed
                Log.v("bush", "postComment onSucceess " + Thread.currentThread().getName());
            }

            @Override
            public void onFailed(Exception e) {
                if (e instanceof UploadException) {
                    // image upload failed
                } else {
                    // comment failed
                }
            }
        });
    }

}