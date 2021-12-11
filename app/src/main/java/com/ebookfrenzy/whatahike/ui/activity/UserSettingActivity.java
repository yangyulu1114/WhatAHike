package com.ebookfrenzy.whatahike.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserSettingActivity extends AppCompatActivity {
    Map<String, CheckBox> checkBoxes;
    List<String> keys;
    private static final String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth;
    private TextView email;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        checkBoxes = new HashMap<>();
        email = findViewById(R.id.email);
        mFirebaseAuth = FirebaseAuth.getInstance();
        keys = new ArrayList<>();

        mActionBar = getSupportActionBar();
        mActionBar.setCustomView(R.layout.usersetting_actionbar);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent =(Toolbar)mActionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);

        setupButtons();
        initCheckBoxed();
        setEmail();
        initPrefView();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (checkBoxes == null || checkBoxes.size() == 0) {
            initCheckBoxed();
        }
        setEmail();
        initPrefView();
    }

    private void setEmail() {
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null){
            email.setText(mFirebaseUser.getEmail());
        }else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
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

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (CheckBox checkBox: checkBoxes.values()) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    }
                }
                keys.clear();
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
                Toast.makeText(UserSettingActivity.this, "Preference saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setPreference(){
        Preference preference;
        preference = new Preference(keys);
        RestAPI.setUserPreference(preference);
    }

    private void logout() {
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
                        }
                    }
                });
    }

    public void onMyClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.logout:
                logout();
                break;
            default:
                break;
        }
    }
}