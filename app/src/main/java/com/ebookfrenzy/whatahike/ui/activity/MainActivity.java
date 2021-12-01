package com.ebookfrenzy.whatahike.ui.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements LocationListener {
    EditText info;
    private List<Trail> trailList;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;

    private static Location location;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_list_item, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        Spinner mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_list_item, getResources().getStringArray(R.array.sort));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter2);

        info = findViewById(R.id.Search);

        recyclerView = findViewById(R.id.recyclerView);
        ImageButton btn = findViewById(R.id.searchButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                getTrail(info.getText());
                setAdapter();
            }

        });



                //setTrailInfo();
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

//    private void setTrailInfo() {
//        trailList.add(new trailRecord("1","Poo Poo Point", "It is the best place to hike", 10));
//        trailList.add(new trailRecord("2","Rattlesnake Ridge", "It is the good place to hike", 9));
//        trailList.add(new trailRecord("3","Snoqualmie falls", "It is the wonderful place to hike", 8));
//        trailList.add(new trailRecord("4","Rainier", "It is the best place to hike", 9));
//        trailList.add(new trailRecord("5","Olympic", "It is the good place to hike", 5));
//        trailList.add(new trailRecord("6","North Cascade", "It is the wonderful place to hike", 2));
//    }

    //@Override
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getTrail(Editable text){
        trailList = RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                //implement pass function
                String keyword = text.toString().trim().toLowerCase();
                return trail.getState().toLowerCase().equals(keyword) || trail.getName().toLowerCase().contains(keyword);
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                //implement comparator
                double o1dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o1.getLocation()[0], o1.getLocation()[1]);
                double o2dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o2.getLocation()[0], o2.getLocation()[1]);

                return (int) (o1dis - o2dis);
            }
        }.thenComparing(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                return t2.getNumReviews() - t1.getNumReviews();
            }
        }));
    }
}