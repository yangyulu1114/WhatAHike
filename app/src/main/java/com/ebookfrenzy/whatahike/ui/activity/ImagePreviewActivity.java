package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
    private List<String> mImageList = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;

    private ViewPager mViewPager;
    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter();
        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.imagepreview_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        for(int i = 0; i < 9; i++) {
//            mImageList.add(String.format("http://gothomas.me/images/banners/%s.jpg", i));
//            Log.v("bush", mImageList.get(i));
//        }
        File file1 = new File("/sdcard/DCIM/Camera/20211117_150958.jpg");
        File file2 = new File("/sdcard/DCIM/Camera/20211128_232428.jpg");
        mImageList.add(Uri.fromFile(file1).toString());
        mImageList.add(Uri.fromFile(file2).toString());
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mViewPagerAdapter);
        refreshViewPager();
    }

    public void refreshViewPager() {
        mViewPagerAdapter.setImageList(mImageList);
        mViewPager.setCurrentItem(1);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.delete:
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.v("bush", "onPageSelected : position " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}