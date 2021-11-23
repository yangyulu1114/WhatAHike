package com.ebookfrenzy.whatahike.utils;

import android.content.Context;
import android.util.Log;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TrailsReadingUtil {

    private static int idIdx, nameIdx, areaIdx, cityIdx, stateIdx, countryIdx,
            locationIdx, lengthIdx, elevationIdx, difficultyIdx,
            typeIdx, ratingIdx, numReviewsIdx, featuresIdx, activitiesIdx;


    public static List<Trail> readCSVTrails() {
        InputStream file = MyApplication.getAppContext()
                .getResources().openRawResource(R.raw.trails);

        List<Trail> trails = new ArrayList<>();
        try {
            Scanner scan = new Scanner(file);

            // set up fields indexes
            String[] fields = scan.nextLine().split(",");
            setIdx(fields);

            // reading each row
            while (scan.hasNextLine()) {
                String[] record = scan.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                trails.add(generateTrail(record));
            }
            scan.close();

        } catch (Exception e) {
            trails = null;
            e.printStackTrace();
        }

        return trails;
    }

    private static Trail generateTrail(String[] record) {
        Trail trail = new Trail();

        trail.setId(record[idIdx]);
        trail.setName(record[nameIdx]);
        trail.setArea(record[areaIdx]);
        trail.setState(record[stateIdx]);
        trail.setCountry(record[countryIdx]);
        trail.setLength(Double.parseDouble(record[lengthIdx]));
        trail.setElevation(Double.parseDouble(record[elevationIdx]));
        trail.setDifficulty(Integer.parseInt(record[difficultyIdx]));
        trail.setType(record[typeIdx]);
        trail.setRating(Double.parseDouble(record[ratingIdx]));
        trail.setNumReviews(Integer.parseInt(record[numReviewsIdx]));

        String[] location = record[locationIdx]
                .substring(2, record[locationIdx].length() - 2)
                .split(",");
        trail.setLocation(new double[]{
                Double.parseDouble(location[0].split(":")[1]),
                Double.parseDouble(location[1].split(":")[1]),
        });

        trail.setFeatures(Arrays.asList(record[featuresIdx]
                .substring(1, record[featuresIdx].length() - 1)
                .replace("'", "")
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "")
                .split(",")));
        trail.setActivities(Arrays.asList(record[activitiesIdx]
                .substring(1, record[activitiesIdx].length() - 1)
                .replace("'", "")
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "")
                .split(",")));

        return trail;
    }

    private static void setIdx(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].equals("id"))
                idIdx = i;
            else if (fields[i].equals("name"))
                nameIdx = i;
            else if (fields[i].equals("area"))
                areaIdx = i;
            else if (fields[i].equals("city"))
                cityIdx = i;
            else if (fields[i].equals("state"))
                stateIdx = i;
            else if (fields[i].equals("country"))
                countryIdx = i;
            else if (fields[i].equals("location"))
                locationIdx = i;
            else if (fields[i].equals("length"))
                lengthIdx = i;
            else if (fields[i].equals("elevation"))
                elevationIdx = i;
            else if (fields[i].equals("difficulty"))
                difficultyIdx = i;
            else if (fields[i].equals("type"))
                typeIdx = i;
            else if (fields[i].equals("rating"))
                ratingIdx = i;
            else if (fields[i].equals("numReviews"))
                numReviewsIdx = i;
            else if (fields[i].equals("features"))
                featuresIdx = i;
            else if (fields[i].equals("activities"))
                activitiesIdx = i;
        }
    }
}