package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;

import java.util.List;

public class UserSetting extends AppCompatActivity {
    protected List<String> activityPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        setPrefInfo();

    }

    private void setPrefInfo() {
        activityPref.add("birding");
        activityPref.add("hiking");
        activityPref.add("trail-running");
    }

//    private List<String> getPreflist() {
//        return activityPref;
//    }

}