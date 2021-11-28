package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.ui.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends BaseActivity {
    private List<String> mImagaList = new ArrayList<>();
    private ViewPagerAdapter mViewPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mViewPager = findViewById(R.id.viewpager);
        mViewPagerAdapter = new ViewPagerAdapter(mImagaList);
        for (int i = 0; i < 9; i++) {
            mImagaList.add(String.format("http://gothomas.me/images/banners/%s.jpg", i));
        }
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(1);
    }

}