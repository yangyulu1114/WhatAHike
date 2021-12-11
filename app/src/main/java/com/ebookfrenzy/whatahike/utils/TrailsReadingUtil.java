package com.ebookfrenzy.whatahike.utils;

import com.ebookfrenzy.whatahike.MyApplication;
import com.ebookfrenzy.whatahike.R;
import com.ebookfrenzy.whatahike.model.Trail;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TrailsReadingUtil {

    private static int idIdx, nameIdx, areaIdx, cityIdx, stateIdx, countryIdx,
            locationIdx, lengthIdx, elevationIdx, difficultyIdx,
            typeIdx, ratingIdx, numReviewsIdx, featuresIdx, activitiesIdx,
            iconIdx, bannerIdx;


    public static Map<String, Trail> readCSVTrails() {
        InputStream file = MyApplication.getAppContext()
                .getResources().openRawResource(R.raw.trails);

        Map<String, Trail> trails = new HashMap<>();
        try {
            Scanner scan = new Scanner(file);

            // set up fields indexes
            String[] fields = scan.nextLine().split(",");
            setIdx(fields);

            // reading each row
            while (scan.hasNextLine()) {
                String[] record = scan.nextLine().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Trail trail = generateTrail(record);
                trails.put(trail.getId(), trail);
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

        String name = record[nameIdx];
        if (name.charAt(0) == '"' || name.charAt(0) == '\'') {
            name = name.substring(1, name.length() - 1);
        }
        trail.setName(name);

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

        String features = record[featuresIdx].toLowerCase();
        trail.setFeatures(Arrays.asList(features
                .substring(1, record[featuresIdx].length() - 1)
                .replace("'", "")
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "")
                .split(",")));

        String activities = record[activitiesIdx].toLowerCase();
        trail.setActivities(Arrays.asList(activities
                .substring(1, record[activitiesIdx].length() - 1)
                .replace("'", "")
                .replace(" ", "")
                .replace("[", "")
                .replace("]", "")
                .split(",")));

        trail.setIconURL(record[iconIdx]);
        trail.setBannerURL(record[bannerIdx]);

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
            else if (fields[i].equals("icon"))
                iconIdx = i;
            else if (fields[i].equals("banner"))
                bannerIdx = i;
        }
    }
}
