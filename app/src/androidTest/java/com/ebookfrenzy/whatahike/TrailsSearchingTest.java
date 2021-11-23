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

    //    @Override
//    public void callApplicationOnCreate(Application app) {
//        super.callApplicationOnCreate(app);
//    }

}