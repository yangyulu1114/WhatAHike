package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;

import java.util.List;

public class UserSetting extends AppCompatActivity {
    protected List<String> activityPref;
    CheckBox cb, cb2, cb3, cb4, cb5, cb6, cb7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        setPrefInfo();
        cb = (CheckBox) findViewById(R.id.Birding);
        cb2 = (CheckBox) findViewById(R.id.hiking);
        cb3 = (CheckBox) findViewById(R.id.naturetrips);
        cb4 = (CheckBox) findViewById(R.id.trailrunning);
        cb5 = (CheckBox) findViewById(R.id.fishing);
        cb6 = (CheckBox) findViewById(R.id.walking);
        cb7 = (CheckBox) findViewById(R.id.seaKayaking);

    }

    private void setPrefInfo() {
        activityPref.add("birding");
        activityPref.add("hiking");
        activityPref.add("trail-running");
    }

    public void checkone(View v) {
        if(cb.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked()
        || cb5.isChecked() || cb6.isChecked() || cb7.isChecked()){
            //TODO: store the userinputs into database
        }
    }

//    private List<String> getPreflist() {
//        return activityPref;
//    }

}