package com.ebookfrenzy.whatahike.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.ui.adapter.ViewPagerAdapter;

import java.io.File;
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
    ActionBar mActionBar;

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

        if (mNeedEdit == false) {
            mActionBar.hide();
        }


//        for(int i = 0; i < 9; i++) {
//            mImageList.add(String.format("http://gothomas.me/images/banners/%s.jpg", i));
//            Log.v("bush", mImageList.get(i));
//        }
//        File file1 = new File("/sdcard/DCIM/Camera/20211117_150958.jpg");
//        File file2 = new File("/sdcard/DCIM/Camera/20211128_232428.jpg");
//        mImageList.add(Uri.fromFile(file1).toString());
//        mImageList.add(Uri.fromFile(file2).toString());
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mViewPagerAdapter);
        refreshViewPager();
    }

    public void refreshViewPager() {
        Log.v("bush", "refreshViewPager mCurrentPosition " + mCurrentPosition);
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
                mCurrentPosition += mCurrentPosition == 0 ? 1 : -1;
                refreshViewPager();
                break;
            default:
                break;
        }
    }

    private void back() {
        Intent intent = new Intent();
        intent.putIntegerArrayListExtra("deletedImages", (ArrayList<Integer>) mDeletedImageList);
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
        Log.v("bush", "onDestroy");
        mViewPager.removeOnPageChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        Log.v("bush", "onPageSelected : position " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}