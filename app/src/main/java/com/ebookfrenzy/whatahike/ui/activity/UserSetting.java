package com.ebookfrenzy.whatahike.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.FirebaseTimeoutException;
import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserSetting extends AppCompatActivity {
    CheckBox cb, cb2, cb3, cb4, cb5, cb6, cb7;
    Button button;
    List<String> keys = new ArrayList<>();
    Button btnReset;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        button = findViewById(R.id.save);
        cb = (CheckBox) findViewById(R.id.birding);
        cb2 = (CheckBox) findViewById(R.id.hiking);
        cb3 = (CheckBox) findViewById(R.id.naturetrips);
        cb4 = (CheckBox) findViewById(R.id.trailrunning);
        cb5 = (CheckBox) findViewById(R.id.fishing);
        cb6 = (CheckBox) findViewById(R.id.walking);
        cb7 = (CheckBox) findViewById(R.id.seakayaking);



        String a1 = "Birding";
        String a2 = "Hiking";
        String a3 = "Naturaltrips";
        String a4 = "Trailrunning";
        String a5 = "Fishing";
        String a6 = "Walking";
        String a7 = "Seakayaking";

        //Reyclerview invisible before onsuccess adapter
        //checkbox unclickable

        getPreference();


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cb.isChecked()){
                    keys.add(a1);

                }else{
                    keys.remove(a1);
                }
                if (cb2.isChecked()){
                    keys.add(a2);
                }
                else{
                    keys.remove(a2);
                }
                if(!cb.isChecked() &&
                        !cb2.isChecked() &&
                        !cb3.isChecked() &&
                        !cb4.isChecked() &&
                        !cb5.isChecked() &&
                        !cb6.isChecked() &&
                        !cb7.isChecked()){
                }
                setPreference();
            }
        });


        btnReset = findViewById(R.id.reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    cb.setChecked(false);
                }
                if(cb2.isChecked()){
                    cb2.setChecked(false);
                }
                if(cb3.isChecked()){
                    cb3.setChecked(false);
                }
                if(cb4.isChecked()){
                    cb4.setChecked(false);
                }
                if(cb5.isChecked()){
                    cb5.setChecked(false);
                }
                if(cb6.isChecked()){
                    cb6.setChecked(false);
                }
                if(cb7.isChecked()){
                    cb7.setChecked(false);
                }
                keys = new ArrayList<String>();
            }
        });
        setPreference();
    }

    private void getPreference(){
        RestAPI.getUserPreference(new Listener<Preference>() {
            @Override
            public void onSuccess(Preference data) {
                 keys = data.getKeys();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setPreference(){
        Preference preference;
        preference = new Preference(keys);
        RestAPI.setUserPreference(preference);
        for (int i = 0; i<keys.size();i++) {
            Log.v("Grace", keys.get(i));
        }
    }


}