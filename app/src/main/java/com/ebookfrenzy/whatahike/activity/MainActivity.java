package com.ebookfrenzy.whatahike.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Trail> trails = RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                return trail.getCity() == "Fremont";
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                return o1.getDifficulty() - o2.getDifficulty();
            }
        });
    }

}