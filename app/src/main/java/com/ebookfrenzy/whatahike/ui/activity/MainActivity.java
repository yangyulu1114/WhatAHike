package com.ebookfrenzy.whatahike.ui.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.whatahike.Filter;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.RestAPI;
import com.ebookfrenzy.whatahike.model.Preference;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.notification.NotificationScenarioType;
import com.ebookfrenzy.whatahike.ui.adapter.RecyclerAdapter;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.NotificationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements LocationListener, AdapterView.OnItemSelectedListener{
    private EditText info;
    private List<Trail> trailList;

    private RecyclerAdapter.RecyclerViewClickListener listener;

    private View mMaskLayer;
    private ProgressBar mProgressBar;
    private Handler mHandler = new Handler();
    private ImageView button;

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
    private List<String> mPrefActivities;
    private ActionBar mainActionBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActionBar = getSupportActionBar();
        mainActionBar.setCustomView(R.layout.mainactivity_actionbar);
        mainActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        Toolbar parent =(Toolbar)mainActionBar.getCustomView().getParent();
        parent.setPadding(0,0,0,0);
        parent.setContentInsetsAbsolute(0,0);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_list_item, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

        Spinner mySpinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(MainActivity.this,
                R.layout.activity_list_item, getResources().getStringArray(R.array.sort));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);
        mySpinner2.setOnItemSelectedListener(this);

        mMaskLayer = findViewById(R.id.maskLayer);
        mProgressBar = findViewById(R.id.progressBar);
        setMask();

        info = findViewById(R.id.Search);

        initFiltersAndComparators();
        setOnClickListener();
        updateView();
    }


    @Override
    public void onRestart() {
        super.onRestart();
        RestAPI.getUserPreference(new Listener<Preference>() {
            @Override
            public void onSuccess(Preference data) {
                if (mPrefActivities.size() != data.getKeys().size()) {
                    mPrefActivities = data.getKeys();
                    setAdapter();
                }
                else if (!mPrefActivities.containsAll(data.getKeys())) {
                    mPrefActivities = data.getKeys();
                    setAdapter();
                }
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initFiltersAndComparators() {
        distanceComparator = new Comparator<Trail>() {
            @Override
            public int compare(Trail o1, Trail o2) {
                if (location == null) {
                    return 0;
                }
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
        mPrefActivities = new ArrayList<>();

        stateFilter = new Filter<Trail>() {
            String state;
            @Override
            public boolean pass(Trail trail) {
                //implement pass function

                if (location == null || difficulty > 0 || mPrefActivities.size() > 0) {
                    return checkDifficulty(trail)
                            && checkActivities(trail);
                }
                return true;
            }
        };

        keywordFilter = new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                //implement pass function
                String keyword = info.getText().toString().trim().toLowerCase();

                return checkDifficulty(trail)
                        && (checkFields(trail.getName(), trail.getState(), trail.getCity(),
                                        trail.getCountry(), trail.getArea())
                        || trail.getActivities().contains(keyword)
                        || trail.getFeatures().contains(keyword));
            }
        };

        setCurFilter();
        currentComparator = defaultComparator;
    }

    private boolean checkFields(String... fields) {
        String keyword = keyword = info.getText().toString().trim().toLowerCase();

        keyword = " " + keyword + " ";
        for (String field: fields) {
            if (field == null || field.length() == 0) {
                continue;
            }
            field = " " + field + " ";
            if (field.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDifficulty(Trail trail) {
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
        return true;
    }

    private boolean checkActivities(Trail trail) {
        if(mPrefActivities.size() == 0){
            return true;
        }

        List<String> curActivities = trail.getActivities();

        for (String activity: curActivities) {
            if (mPrefActivities.contains(activity))
                return true;
        }
        return false;
    }

    private void updateView() {
        RestAPI.getUserPreference(new Listener<Preference>() {
            @Override
            public void onSuccess(Preference data) {
                mPrefActivities = data.getKeys();
                setAdapter();
            }

            @Override
            public void onFailed(Exception e) {

            }
        });
    }

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

    private void setMask() {
        mMaskLayer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void removeMask(long delay) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMaskLayer.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }, delay);
    }
    private void setAdapter() {
        Log.v("keywordfilter: ", currentFilter.equals(keywordFilter)+"");
        Log.v("pref: ", mPrefActivities.toString());

        setMask();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RestAPI.getTrails(currentFilter, currentComparator, new Listener<List<Trail>>() {
            @Override
            public void onSuccess(List<Trail> data) {
                removeMask(400);
                trailList = data;
                if(trailList.isEmpty()){
                    NoResultDialog dialog = new NoResultDialog();
                    dialog.show(getSupportFragmentManager(), "can't find dialog");
                }
                RecyclerAdapter adapter = new RecyclerAdapter(trailList, listener);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailed(Exception e) {
                removeMask(0);
            }
        });

    }

    private void setOnClickListener() {

        ImageView btn = findViewById(R.id.searchButton);
        button = (ImageView) findViewById(R.id.userButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSettingActivity.class);
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurFilter();
                setAdapter();
            }

        });

        listener = new RecyclerAdapter.RecyclerViewClickListener(){
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), DetailedTrailActivity.class);
                intent.putExtra("trailId", trailList.get(position).getId());
                startActivity(intent);
            }
        };

    }

    private void setCurFilter() {
        String input = info.getText().toString();
        if (input == null || input.trim().length() == 0) {
            currentFilter = stateFilter;
        } else {
            currentFilter = keywordFilter;
        }
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
//        try {
//            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            locationKeyword = addresses.get(0).getAdminArea().toLowerCase();
//        } catch (IOException e) {
//        }

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = adapterView.getItemAtPosition(i).toString();
        if(selection.equals("Filter")){
            difficulty = -1;
            setCurFilter();
            updateView();
        }
        if(selection.equals("Sort")){
            currentComparator = defaultComparator;
            updateView();
        }
        if(selection.equals("Difficulty - Easy")){
            difficulty = 1;
            updateView();
        }
        if(selection.equals("Difficulty - Medium")){
            difficulty = 3;
            updateView();
        }
        if(selection.equals("Difficulty - Hard")){
            difficulty = 5;
            updateView();
        }
        if(selection.equals("Difficulty - Extreme")){
            difficulty = 7;
            updateView();
        }
        if(selection.equals("Closest Trails")){
            currentComparator = distanceComparator;
            updateView();
        }
        if(selection.equals("Most popular Trails")){
            currentComparator = popularityComparator;
            updateView();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}