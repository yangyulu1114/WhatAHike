package com.ebookfrenzy.whatahike.ui.activity;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.ui.adapter.GridViewAdapter;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddCommentActivity extends AppCompatActivity {
    private final int PICK_FROM_GALLERY = 1;
    private final int PHOTO_LIMIT = 9;
    private final int IMAGE_PREVIEW = 2;

    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    private List<Uri> mImageList = new ArrayList<>();
    private ActionBar mActionBar;
    private String trailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        mGridView = (GridView) findViewById(R.id.commentImage);
        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.addcomment_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        mGridViewAdapter = new GridViewAdapter();
        mGridView.setAdapter(mGridViewAdapter);
        refreshGridView();

//        trailId = getIntent().getStringExtra("trailId");
        trailId = "1";
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position >= mImageList.size()) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_FROM_GALLERY);
                } else {
                    openImagePreview(position);
                }
            }
        });
    }

    private void refreshGridView() {
        List<String> list = new ArrayList<>();
        for (Uri uri : mImageList) {
            list.add(uri.toString());
        }
        if (mImageList.size() < PHOTO_LIMIT) {
            list.add("add");
        }
        mGridViewAdapter.setImageList(list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    mImageList.add(uri);
                    refreshGridView();
                    Log.v("bush", "uri" + uri);
                }
                break;
        }
    }

    public void onMyClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.send:
                postComment();
                break;
            default:
                break;
        }
    }

    private void postComment() {
        EditText editText = findViewById(R.id.commentText);
        Comment comment = new Comment(User.getCurrentUser().getEmail());
        comment.setText(editText.getText().toString());
        comment.setTimeStamp(System.currentTimeMillis());
        comment.setTrailId(trailId);

        RestAPI.postComment(comment, mImageList, new Listener<Void>() {
            @Override
            public void onSuccess(Void data) {
                // comment succeed
                finish();
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

    private void openImagePreview(int position) {
        List<String> list = new ArrayList<>();
        for (Uri uri : mImageList) {
            list.add(uri.toString());
        }
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("activityName", "addComment");
        intent.putExtra("position", position);
        intent.putExtra("imageList", (Serializable) list);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }
}

