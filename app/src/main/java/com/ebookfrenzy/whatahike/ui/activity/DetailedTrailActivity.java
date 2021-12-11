package com.ebookfrenzy.whatahike.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.MyApplication;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DetailedTrailActivity extends AppCompatActivity {
    private List<Comment> commentList;
    private UserCommentAdapter adapter;
    private Map<String, Drawable> iconMap;

    private Random rand;

    private String trailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_trail);

        rand = new Random();
        iconMap = new HashMap<>();

        trailId = getIntent().getStringExtra(String.valueOf(R.string.trailId));
        if (trailId == null)
            trailId = getIntent().getStringExtra("trailId");

        // set up tool bar
        setupToolBar();

        // set trail info
        initTrailDetail();

        // set comments view
        setupCommentView();
    }


    @Override
    public void onRestart() {
        super.onRestart();
        setupCommentView();
        Log.v("comment activity: ", "onRestart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("comment activity: ", "onStop");
    }

    private void setupCommentView() {
        ProgressBar prograssBar = findViewById(R.id.commentProgressBar);
        TextView internet_hint = findViewById(R.id.internet_hint);
        TextView nocomment_hint = findViewById(R.id.nocomment_hint);

        RestAPI.getComments(trailId, new Listener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                commentList = data;
                Collections.sort(commentList, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment c1, Comment c2) {
                        if (c1.getTimeStamp() < c2.getTimeStamp())
                            return 1;
                        else if (c1.getTimeStamp() > c2.getTimeStamp())
                            return -1;
                        return 0;
                    }
                });

                if (commentList == null || commentList.size() == 0) {
                    nocomment_hint.setVisibility(View.VISIBLE);
                } else {
                    nocomment_hint.setVisibility(View.GONE);
                }

                internet_hint.setVisibility(View.GONE);
                prograssBar.setVisibility(View.GONE);

                if (iconMap.size() == 0 && commentList.size() > 0) {
                    for (Comment comment : commentList) {
                        putRandomIcon(comment.getUserId());
                    }
                } else if (commentList.size() > 0){
                    int top = 0;
                    while (top < commentList.size() && !iconMap.containsKey(commentList.get(top).getUserId())) {
                        putRandomIcon(commentList.get(top++).getUserId());
                    }
                }


                initComments();
            }
            @Override
            public void onFailed(Exception e) {
                if (e instanceof FirebaseTimeoutException) {
                    if (prograssBar.getVisibility() == View.VISIBLE) {
                        internet_hint.setVisibility(View.VISIBLE);
                        prograssBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void putRandomIcon(String userId) {
        if (!iconMap.containsKey(userId)) {
            int randint = rand.nextInt(6);
            if (randint == 1) {
                iconMap.put(userId, MyApplication.getAppContext().getDrawable(R.drawable.usericon2));
            } else if (randint == 2) {
                iconMap.put(userId, MyApplication.getAppContext().getDrawable(R.drawable.usericon3));
            } else if (randint == 3) {
                iconMap.put(userId, MyApplication.getAppContext().getDrawable(R.drawable.usericon4));
            } else if (randint == 4) {
                iconMap.put(userId, MyApplication.getAppContext().getDrawable(R.drawable.usericon5));
            } else if (randint == 5) {
                iconMap.put(userId, MyApplication.getAppContext().getDrawable(R.drawable.usericon6));
            }
        }
    }

    private void setupToolBar() {
        AppBarLayout appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
                int color = Color.argb(200,0,0,0);
                collapsingToolbar.setCollapsedTitleTextColor(color);
                if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) { // fold
                    collapsingToolbar.setTitle("User Comments");
                    collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
                } else { // not fold
                    collapsingToolbar.setTitle("");
                }
            }
        });

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.detailedtrail_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent =(Toolbar)mActionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);
    }

    public void backToMain(View view) {
        finish();
    }

    public void addComment(View view) {
        Intent intent = new Intent(this, AddCommentActivity.class);
        intent.putExtra("trailId", trailId);
        startActivity(intent);
    }

    
    private void initComments() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        adapter = new UserCommentAdapter(commentList, iconMap, trailId);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        Log.v("comment activity: ", "init comment");
    }


    @SuppressLint("SetTextI18n")
    private void initTrailDetail() {
        Trail trail = RestAPI.getTrailById(trailId);

        StringBuilder sb = new StringBuilder();

        TextView trailInfo = findViewById(R.id.trail_detail);
        sb.append(trail.getName());
        trailInfo.setText(sb.toString());
        sb.setLength(0);

        TextView trailLoca = findViewById(R.id.trail_location);
        sb.append(trail.getArea() + "\n");
        if (trail.getCity() != null)
            sb.append(trail.getCity() + ", ");
        if (trail.getState() != null)
            sb.append(trail.getState() + ", ");
        if (trail.getCountry() != null)
            sb.append(trail.getCountry());
        trailLoca.setText(sb.toString());
        sb.setLength(0);

        TextView length_elevation = findViewById(R.id.length_elevation);
        sb.append("Length: " + (int)trail.getLength() + "   ");
        sb.append("Elevation: " + (int)trail.getElevation());
        length_elevation.setText(sb.toString());
        sb.setLength(0);

        TextView rating_difficulty = findViewById(R.id.rating_difficulty);
        sb.append("Rating: " + trail.getRating() + "   ");
        sb.append("Difficulty: " + trail.getDifficulty());
        rating_difficulty.setText(sb.toString());
        sb.setLength(0);

        TextView features = findViewById(R.id.features);
        sb.append("Features: ");
        for (String feature: trail.getFeatures()) {
            sb.append(feature + ", ");
        }
        features.setText(sb.substring(0, sb.length() - 2));
        sb.setLength(0);

        TextView activities = findViewById(R.id.activities);
        sb.append("Activities: ");
        for (String activity: trail.getActivities()) {
            sb.append(activity + ", ");
        }
        activities.setText(sb.substring(0, sb.length() - 2));
        sb.setLength(0);

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