package com.ebookfrenzy.whatahike.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.exception.UploadException;
import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.model.User;
import com.ebookfrenzy.whatahike.trailRecord;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.RecyclerAdapter;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ArrayList<trailRecord> trailList;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        trailList = new ArrayList<>();

        setTrailInfo();
        setAdapter();
    }


    //if need to request permissions, extends BaseActivity and override function getRequestedPermissions()
    @Override
    String[] getRequestedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    private void setAdapter() {
        adapter = new RecyclerAdapter(trailList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setTrailInfo() {
        trailList.add(new trailRecord("1","Poo Poo Point", "It is the best place to hike", 10));
        trailList.add(new trailRecord("2","Rattlesnake Ridge", "It is the good place to hike", 9));
        trailList.add(new trailRecord("3","Snoqualmie falls", "It is the wonderful place to hike", 8));
        trailList.add(new trailRecord("4","Rainier", "It is the best place to hike", 9));
        trailList.add(new trailRecord("5","Olympic", "It is the good place to hike", 5));
        trailList.add(new trailRecord("6","North Cascade", "It is the wonderful place to hike", 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
}