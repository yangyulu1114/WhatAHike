package com.ebookfrenzy.whatahike.ui.activity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.trailRecord;
import com.ebookfrenzy.whatahike.ui.adapter.RecyclerAdapter;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements LocationListener {
    private ArrayList<trailRecord> trailList;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    private static Location location;
    private LocationManager locationManager;

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
    public String[] getRequestedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    @Override
    public void onAllPermissionsGranted() {
        setLocation();
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.example_menu,menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//
//        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        return true;
//    }

    public static Location getLocation() {
        return location;
    }


    @SuppressLint("MissingPermission")
    private void setLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);

        String provider = locationManager.getBestProvider(criteria, false);

        location = locationManager.getLastKnownLocation(provider);
        // location could equal to null if there is no location history in user's phone.

        locationManager.requestLocationUpdates(provider, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        setLocation();
    }
}