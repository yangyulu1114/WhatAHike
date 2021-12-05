package com.ebookfrenzy.whatahike.ui.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.ui.adapter.RecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    EditText info;
    private List<Trail> trailList;
    private List<Trail> startTrailList;
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerAdapter.RecyclerViewClickListener listener;

    private static Location location;
    private LocationManager locationManager;
    private ActionBar mainActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActionBar = getSupportActionBar();
        mainActionBar.setCustomView(R.layout.mainactivity_actionbar);
        mainActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent = (Toolbar) mainActionBar.getCustomView().getParent();
        parent.setPadding(0, 0, 0, 0);
        parent.setContentInsetsAbsolute(0, 0);


        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.names, R.layout.activity_list_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

        Spinner mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> myAdapter2 = ArrayAdapter.createFromResource(this, R.array.sort, R.layout.activity_list_item);
        mySpinner2.setAdapter(myAdapter2);
        mySpinner2.setOnItemSelectedListener(this);

        info = findViewById(R.id.Search);


        recyclerView = findViewById(R.id.recyclerView);
        startTrailList = getStartTrail();
        //System.out.println(startTrailList);
        setAdapter2();
        ImageButton btn = findViewById(R.id.searchButton);
        ImageButton userProfile = findViewById(R.id.userButton);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSetting.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                getTrail(info.getText());
                setAdapter();
            }

        });
        //setAdapter();
        // get the arrayList data back, and then setAdapter();


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
        setOnClickListener();
        adapter = new RecyclerAdapter(trailList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setAdapter2() {
        setOnClickListener();
        adapter = new RecyclerAdapter(startTrailList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailedTrailActivity.class);
                intent.putExtra("trailId", "10020048");
                startActivity(intent);
            }
        };
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
    private void getTrail(Editable text) {
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

                if (o1dis < o2dis) {
                    return -1;
                } else if (o1dis > o2dis) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }.thenComparing(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                return t2.getNumReviews() - t1.getNumReviews();
            }
        }));
        //trailList = trailList.subList(0, 10);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private List<Trail> getStartTrail() {
        startTrailList = RestAPI.getTrails(new Filter<Trail>() {
            String state;

            @Override
            public boolean pass(Trail trail) {
                //implement pass function

                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    state = addresses.get(0).getAdminArea().toLowerCase();
                    //System.out.println(state);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return trail.getState().toLowerCase().equals(state) || trail.getName().toLowerCase().contains(state);
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                //implement comparator
                double o1dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o1.getLocation()[0], o1.getLocation()[1]);
                double o2dis = RestAPI.getDistance(location.getLatitude(), location.getLongitude(),
                        o2.getLocation()[0], o2.getLocation()[1]);

                if (o1dis < o2dis) {
                    return -1;
                } else if (o1dis > o2dis) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return startTrailList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), selection, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> adapterView) {

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