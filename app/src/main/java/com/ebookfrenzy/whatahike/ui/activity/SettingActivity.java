package com.ebookfrenzy.whatahike.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.ebookfrenzy.whatahike.R;

import java.util.HashSet;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
    private static Set<String> selected;
    private CheckBox cb1;
    private CheckBox cb2;
    private CheckBox cb3;
    private CheckBox cb4;
    private CheckBox cb5;
    private CheckBox cb6;
    private CheckBox cb7;
    private Button btnSubmit;
    private Button btnReset;
    //private Set<String> selected;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_setting);
        cb1 = findViewById(R.id.dogsno);
        cb2 = findViewById(R.id.forest);
        cb3 = findViewById(R.id.river);
        cb4 = findViewById(R.id.views);
        cb5 = findViewById(R.id.waterfall);
        cb6 = findViewById(R.id.wildflowers);
        cb7 = findViewById(R.id.wildlife);
        selected = new HashSet<>();
        btnReset = findViewById(R.id.reset);
        btnSubmit = findViewById(R.id.Submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb1.isChecked()) {
                    selected.add(cb1.getText().toString());
                }
                if(cb2.isChecked()) {
                    selected.add(cb2.getText().toString());
                }
                if(cb3.isChecked()) {
                    selected.add(cb3.getText().toString());
                }
                if(cb4.isChecked()) {
                    selected.add(cb4.getText().toString());
                }
                if(cb5.isChecked()) {
                    selected.add(cb5.getText().toString());
                }
                if(cb6.isChecked()) {
                    selected.add(cb6.getText().toString());
                }
                if(cb7.isChecked()) {
                    selected.add(cb7.getText().toString());
                }
                if(!cb1.isChecked() &&
                   !cb2.isChecked() &&
                   !cb3.isChecked() &&
                   !cb4.isChecked() &&
                   !cb5.isChecked() &&
                   !cb6.isChecked() &&
                   !cb7.isChecked()){
                    selected = new HashSet<>();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb1.isChecked()){
                    cb1.setChecked(false);
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
                selected = new HashSet<>();
            }
        });
    }

    public static Set<String> getSelected(){
        return selected;
    }
}
