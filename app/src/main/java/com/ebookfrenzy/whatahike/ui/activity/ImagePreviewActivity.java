package com.ebookfrenzy.whatahike.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.ui.adapter.ViewPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private final int RESULT_OK = 1;

    private List<String> mImageList;
    private List<Integer> mDeletedImageList = new ArrayList<>();
    private int mCurrentPosition;
    private boolean mNeedEdit = false;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private ActionBar mActionBar;
    private TextView mTitleTextView;
    private TextView mDeleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        Intent intent = getIntent();
        mImageList = (List<String>) intent.getStringArrayListExtra("imageList");
        mCurrentPosition = intent.getIntExtra("position", 0);
        mNeedEdit = intent.getStringExtra("activityName") != null ? true : false;


        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter();
        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.imagepreview_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mTitleTextView = findViewById(R.id.text);
        mDeleteTextView = findViewById(R.id.delete);
        Toolbar parent =(Toolbar)mActionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);

        if (mNeedEdit == false) {
            mDeleteTextView.setVisibility(View.INVISIBLE);
        }

        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mViewPagerAdapter);
        refreshViewPager();
    }

    public void refreshViewPager() {
        mTitleTextView.setText(String.format("%s/%s", mCurrentPosition + 1, mImageList.size()));
        mViewPagerAdapter.setImageList(mImageList);
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.back:
                back();
                break;
            case R.id.delete:
                mImageList.remove(mCurrentPosition);
                mDeletedImageList.add(mCurrentPosition);
                if (mImageList.size() == 0) {
                    back();
                    return;
                }
                refreshViewPager();
                break;
            default:
                break;
        }
    }

    private void back() {
        Intent intent = new Intent();
        intent.putExtra("deletedImages", (Serializable) mDeletedImageList);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mViewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        mTitleTextView.setText(String.format("%s/%s", mCurrentPosition + 1, mImageList.size()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}