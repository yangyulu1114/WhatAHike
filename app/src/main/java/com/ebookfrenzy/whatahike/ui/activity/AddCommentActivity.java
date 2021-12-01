package com.ebookfrenzy.whatahike.ui.activity;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.ui.adapter.GridViewAdapter;
import com.ebookfrenzy.whatahike.utils.DisplayUtil;
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
    private ActionBar mActionBar;
    private View mMaskLayer;
    private ProgressBar mProgressBar;
    private Handler mHandler = new Handler();
    private EditText mEditText;

    private List<Uri> mImageList = new ArrayList<>();
    private String trailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        mGridView = (GridView) findViewById(R.id.commentImage);
        mMaskLayer = findViewById(R.id.maskLayer);
        mProgressBar = findViewById(R.id.progressBar);
        mEditText = findViewById(R.id.commentText);
        mEditText.setMaxHeight(DisplayUtil.getScreenSize()[1] / 5);

        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.addcomment_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent =(Toolbar)mActionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);

        mGridViewAdapter = new GridViewAdapter();
        mGridView.setAdapter(mGridViewAdapter);
        refreshGridView();

        trailId = getIntent().getStringExtra("trailId");
//        trailId = "1";
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
        Log.v("bush","onActivityResult");
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
            case IMAGE_PREVIEW:
                List<Integer> deletedList = (List<Integer>) data.getSerializableExtra("deletedImages");
                if (deletedList == null) {
                    Log.v("bush", "deletedList is null");
                }
                if (deletedList != null && deletedList.size() > 0) {
                    for (int i : deletedList) {
                        mImageList.remove(i);
                    }
                    refreshGridView();
                }
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
        mMaskLayer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        EditText editText = findViewById(R.id.commentText);
        Comment comment = new Comment(User.getCurrentUser().getEmail());
        comment.setText(editText.getText().toString());
        comment.setTimeStamp(System.currentTimeMillis());
        comment.setTrailId(trailId);
        if (editText.getText().length() == 0 && mImageList.size() == 0) {
            Toast.makeText(this, "Comment can't be empty!", Toast.LENGTH_SHORT).show();
            onPostComplete(0, false);
            return;
        }

        RestAPI.postComment(comment, mImageList, new Listener<Void>() {
            @Override
            public void onSuccess(Void data) {
                // comment succeed
                onPostComplete(600, true);
                Log.v("bush", "postComment onSuccess " + Thread.currentThread().getName());
            }

            @Override
            public void onFailed(Exception e) {
                onPostComplete(600, false);
                if (e instanceof UploadException) {
                    // image upload failed
                    Toast.makeText(AddCommentActivity.this, "Upload Failed, please retry", Toast.LENGTH_LONG).show();
                } else {
                    // comment failed
                    Toast.makeText(AddCommentActivity.this, "Comment Failed, please retry", Toast.LENGTH_LONG).show();
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
        intent.putStringArrayListExtra("imageList", (ArrayList<String>) list);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void onPostComplete(long delay, boolean finish) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMaskLayer.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                if (finish) {
                    finish();
                }
            }
        }, delay);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}

