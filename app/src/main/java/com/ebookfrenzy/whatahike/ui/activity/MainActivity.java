package com.ebookfrenzy.whatahike.ui.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.adapter.RecyclerAdapter;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends BaseActivity implements LocationListener{
    private EditText info;
    private List<Trail> trailList;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;

    private static Location location;
    private LocationManager locationManager;

    private Comparator<Trail> defaultComparator; // distance then popularity
    private Comparator<Trail> distanceComparator;
    private Comparator<Trail> popularityComparator;

    private Filter<Trail> stateFilter;
    private Filter<Trail> keywordFilter;

    private Filter<Trail> currentFilter;
    private Comparator<Trail> currentComparator;

    private int difficulty; // 1 -> e, 3 -> m, 5 -> h, 7 -> ex
    private Set<String> features;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        //mySpinner.setAdapter(myAdapter2);


        initFiltersAndComparators();
        initView();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initFiltersAndComparators() {
        distanceComparator = new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                //implement comparator
                double o1dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o1.getLocation()[0], o1.getLocation()[1]);
                double o2dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o2.getLocation()[0], o2.getLocation()[1]);

                if (o1dis <  o2dis) {return -1;}
                else if (o1dis >  o2dis) {return 1;}
                else {return 0;}
            }
        };

        popularityComparator = new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                return t2.getNumReviews() - t1.getNumReviews();
            }
        };

        defaultComparator = distanceComparator.thenComparing(popularityComparator);

        difficulty = -1;
        features = new HashSet<String>();

        stateFilter = new Filter<Trail>() {
            String state;
            @Override
            public boolean pass(Trail trail) {
                //implement pass function

                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    state = addresses.get(0).getAdminArea().toLowerCase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return checkConditions(trail)
                        && (trail.getState().toLowerCase().equals(state)
                            || trail.getName().toLowerCase().contains(state));
            }
        };

        keywordFilter = new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                //implement pass function
                String keyword = info.getText().toString().trim().toLowerCase();
                return checkConditions(trail)
                        && (trail.getState().toLowerCase().equals(keyword)
                            || trail.getName().toLowerCase().contains(keyword));
            }
        };

        currentFilter = stateFilter;
        currentComparator = defaultComparator;
    }

    private boolean checkConditions(Trail trail) {
        int curDiff = trail.getDifficulty();
        if (difficulty == 1) {
            if (curDiff != 1 && curDiff != 2)
                return false;
        } else if (difficulty == 3) {
            if (curDiff != 3 && curDiff != 4)
                return false;
        } else if (difficulty == 5) {
            if (curDiff != 5 && curDiff != 6)
                return false;
        } else if (difficulty == 7) {
            if (curDiff != 7)
                return false;
        }

        List<String> curFeatures = trail.getFeatures();

        for (String feature: curFeatures) {
            if (features.contains(feature))
                return true;
        }
        return false;
    }

    private void initView() {
        info = findViewById(R.id.Search);
        recyclerView = findViewById(R.id.recyclerView);
        setOnClickListener();
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
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onAllPermissionsGranted() {
        setLocation();
    }

    private void setAdapter() {
        trailList = RestAPI.getTrails(currentFilter, currentComparator);

        adapter = new RecyclerAdapter(trailList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {

        ImageButton btn = findViewById(R.id.searchButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                currentFilter = keywordFilter;
                setAdapter();
            }

        });

        listener = new RecyclerAdapter.RecyclerViewClickListener(){
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailedTrailActivity.class);
                intent.putExtra("trailId", "10020048");
                startActivity(intent);
            }
        };
    }



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



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


   // @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String selection = adapterView.getItemAtPosition(i).toString();
//        startTrailList = getStartTrail2(selection);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

//    private List<Trail> getStartTrail2(String selection){
//        startTrailList = RestAPI.getTrails(new Filter<Trail>() {
//            String state;
//            @Override
//            public boolean pass(Trail trail) {
//                //implement pass function
//
//                try {
//                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                    state = addresses.get(0).getAdminArea().toLowerCase();
//                    System.out.println(state);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return trail.getState().toLowerCase().equals(state) || trail.getName().toLowerCase().contains(state);
//            }
//        }, new Comparator<Trail>() {
//            @Override
//            public int compare(Trail o1, Trail o2) {
//                //implement comparator
//                return o1.getNumReviews() - o2.getNumReviews();
//            }
//        });
//        return startTrailList;
//    }
}