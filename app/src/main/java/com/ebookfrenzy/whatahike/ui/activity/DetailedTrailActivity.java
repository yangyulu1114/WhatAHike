package com.ebookfrenzy.whatahike.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.FirebaseTimeoutException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.adapter.UserCommentAdapter;
import com.ebookfrenzy.whatahike.utils.ImageLoader;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailedTrailActivity extends AppCompatActivity {
    private List<Comment> commentList;
    private UserCommentAdapter adapter;

    private String trailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_trail);

        trailId = getIntent().getStringExtra(String.valueOf(R.string.trailId));
        if (trailId == null)
            trailId = getIntent().getStringExtra("trailId");

        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
                int color = Color.argb(200,0,0,0);
                collapsingToolbar.setCollapsedTitleTextColor(color);
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) { // fold
                    collapsingToolbar.setTitle("User Comments");
                } else { // not fold
                    collapsingToolbar.setTitle("");
                }
            }
        });

        initTrailDetail();
        RestAPI.getComments(trailId, new Listener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                commentList = data;
                initComments();
            }
            @Override
            public void onFailed(Exception e) {
                if (e instanceof FirebaseTimeoutException) {
                    Log.v("bush", "timeout");
                    //Toast no network
                }
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        RestAPI.getComments(trailId, new Listener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                commentList = data;
                initComments();
            }
            @Override
            public void onFailed(Exception e) {
                Log.e("comment activity: ", e.getMessage());
            }
        });
    }

    public void addComment(View view) {
        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra("trailId", trailId);
        startActivity(intent);
    }

    private void initComments() {
        // for text
//        commentList = new ArrayList<>();
//
//        Comment comment = new Comment("user1");
//        comment.setText("short text");
//        comment.setImages(Arrays.asList("http://gothomas.me/images/banners/0.jpg",
//                "http://gothomas.me/images/banners/1.jpg"));
//        commentList.add(comment);
//        comment = new Comment("user2");
//        comment.setText("long text\nl2\nl3\nl4\ndsfdasfdasfdasfdsa");
//        comment.setImages(Arrays.asList("http://gothomas.me/images/banners/2.jpg",
//                "http://gothomas.me/images/banners/3.jpg"));
//        commentList.add(comment);
        // end of hard code testing

        if (commentList != null) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new UserCommentAdapter(commentList, trailId);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("comment activity: ", "no comment list returned");
        }
    }


    @SuppressLint("SetTextI18n")
    private void initTrailDetail() {
        Trail trail = RestAPI.getTrailById(trailId);

        TextView trailInfo = findViewById(R.id.trail_detail);
        trailInfo.setText(trail.getName() + "\n"
                + trail.getArea() + ", " + trail.getCity() + ", " + trail.getCountry() + "\n"
                + trail.getFeatures().toString() + "\n"
                + trail.getActivities().toString());

        ImageView image = findViewById(R.id.trail_image);
        ImageLoader.loadImage(trail.getBannerURL(), new Listener<Bitmap>() {
            @Override
            public void onSuccess(Bitmap data) {
                image.setImageBitmap(data);
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }


}