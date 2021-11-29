package com.ebookfrenzy.whatahike.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.adapter.UserCommentAdapter;
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
        initComments();

    }

    public void addComment(View view) {
        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra(String.valueOf(R.string.trailId), trailId);
        startActivity(intent);
    }

    private void initComments() {
        // for test
//        trailId = "1";

        RestAPI.getComments(trailId, new Listener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                commentList = data;
            }
            @Override
            public void onFailed(Exception e) {
                Log.e("comment activity: ", e.getMessage());
            }
        });

        // testing comment
//        commentList = new ArrayList<>();
//        Comment comment = new Comment("u1");
//        comment.setText("review of u1");
//        comment.setImages(Arrays.asList("http://gothomas.me/images/banners/0.jpg",
//                "http://gothomas.me/images/banners/1.jpg",
//                "http://gothomas.me/images/banners/3.jpg",
//                "http://gothomas.me/images/banners/4.jpg"));
//        commentList.add(comment);

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

        PingWebServiceTask task = new PingWebServiceTask();
        task.execute(trail.getBannerURL());
    }

    private class PingWebServiceTask extends AsyncTask<String, Integer, Drawable> {

        @Override
        protected Drawable doInBackground(String... strings) {

            Drawable drawable = null;
            try {
                InputStream iStream = (InputStream) new URL(strings[0]).getContent();
                drawable = Drawable.createFromStream(iStream, "image");
            } catch (Exception e) {
                Log.e("comment: ", e.toString());
            }

            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);

            ImageView image = findViewById(R.id.trail_image);
            image.setBackground(drawable);
        }
    }


}