package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.ui.adapter.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddCommentActivity extends AppCompatActivity {

    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    List<String> mImageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        mGridView = (GridView) findViewById(R.id.commentImage);
        mImageList.add("add");

        mGridViewAdapter = new GridViewAdapter(mImageList);
        mGridView.setAdapter(mGridViewAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });
    }
}