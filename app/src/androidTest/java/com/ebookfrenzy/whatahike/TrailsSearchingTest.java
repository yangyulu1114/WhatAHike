package com.ebookfrenzy.whatahike;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.runner.AndroidJUnitRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.ebookfrenzy.whatahike.model.Trail;

import java.util.Comparator;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TrailsSearchingTest extends AndroidJUnitRunner {

    @Test
    public void distanceTest() {
        double lat1 = 37.4243686;
        double lon1 = -121.9561155;
        double lat2 = 37.4232473;
        double lon2 = -121.9698169;

        double distance = RestAPI.getDistance(lat1, lon1, lat2, lon2);
        Log.v("distance: ", distance + "");
    }

    @Test
    public void urlTest() {
        List<Trail> trails = RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                // Get all trails in Arizona
                return trail.getId().equals("10020048");
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                // Max to min
                return t2.getNumReviews() - t1.getNumReviews();
            }
        });

        assertEquals(trails.size(), 1);
        assertEquals(trails.get(0).getIconURL(), "http://gothomas.me/images/icons/0.jpg");
        assertEquals(trails.get(0).getBannerURL(), "http://gothomas.me/images/banners/0.jpg");
    }

    @Test
    public void readingTrailsTest() {
        List<Trail> trails = RestAPI.getTrails(new Filter<Trail>() {
            @Override
            public boolean pass(Trail trail) {
                // Get all trails in Arizona
                return trail.getState().equals("Arizona");
            }
        }, new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                // Max to min
                return t2.getNumReviews() - t1.getNumReviews();
            }
        });
        assertTrue(trails.size() > 0);
        assertTrue(trails.get(0).getNumReviews() > trails.get(1).getNumReviews());
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ebookfrenzy.whatahike", appContext.getPackageName());
    }

    @Override
    public void callApplicationOnCreate(Application app) {
        super.callApplicationOnCreate(app);
    }

}