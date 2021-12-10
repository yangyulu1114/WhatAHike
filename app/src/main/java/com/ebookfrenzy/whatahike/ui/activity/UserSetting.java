package com.ebookfrenzy.whatahike.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.FirebaseTimeoutException;
import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserSetting extends AppCompatActivity {
    Map<String, CheckBox> checkBoxes;
    List<String> keys;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        checkBoxes = new HashMap<>();
        keys = new ArrayList<>();

        setupButtons();
        initCheckBoxed();
        initPrefView();


    }

    @Override
    public void onRestart() {
        super.onRestart();

        if (checkBoxes == null || checkBoxes.size() == 0) {
            initCheckBoxed();
        }
        initPrefView();
    }

    private void initCheckBoxed() {
        Set<String> activities = RestAPI.getActivities();
        LinearLayout activitiesBoxes = findViewById(R.id.activities);

        for (String activity: activities) {
            CheckBox cb = new CheckBox(this);
            cb.setText(activity);
            cb.setTag(activity);
            checkBoxes.put(activity, cb);
            activitiesBoxes.addView(cb);
        }
    }

    private void initPrefView() {
        RestAPI.getUserPreference(new Listener<Preference>() {
            @Override
            public void onSuccess(Preference data) {
                keys = data.getKeys();
                for (String key : keys) {
                    checkBoxes.get(key).setChecked(true);
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

    private void setupButtons() {
        Button btnReset = findViewById(R.id.reset);;
        Button btnSave = findViewById(R.id.save);
        Button btnlogout = findViewById(R.id.logout);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox checkBox: checkBoxes.values()) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    }
                }
                keys.clear();
                setPreference();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                keys.clear();
                for (CheckBox checkBox: checkBoxes.values()) {
                    if (checkBox.isChecked()) {
                        keys.add(checkBox.getText().toString());
                    }
                }
                setPreference();
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Log out", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Log.e(TAG, "onComplete: ", task.getException());
                                }
                            }
                        });
            }
        });


    }

    private void setPreference(){
        Preference preference;
        preference = new Preference(keys);
        RestAPI.setUserPreference(preference);
    }


}