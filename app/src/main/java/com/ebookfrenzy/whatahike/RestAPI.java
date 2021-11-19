package com.ebookfrenzy.whatahike;

import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.FireBaseUtil;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.MainThreadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestAPI {

    private static final String FILEPATH = "res/values/trails.csv";

    private static List<Trail> trails;
    private static int idIdx, nameIdx, areaIdx, cityIdx, stateIdx, countryIdx,
            locationIdx, lengthIdx, elevationIdx, difficultyIdx,
            typeIdx, ratingIdx, numReviewsIdx, featuresIdx, activitiesIdx;

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static List<Trail> getTrails(Filter<Trail> filter, Comparator<Trail> comparator) {
        List<Trail> trails = new ArrayList<>();
        for (Trail trail : readCSVTrails()) {
            if (filter.pass(trail)) {
                trails.add(trail);
            }
        }
        Collections.sort(trails, comparator);
        return trails;
    }

    private static List<Trail> readCSVTrails() {
        trails = new ArrayList<>();

        try {
            Scanner scan = new Scanner(new File(FILEPATH));

            // set up fields indexes
            idIdx = nameIdx = areaIdx = cityIdx = stateIdx = countryIdx
            = locationIdx = lengthIdx = elevationIdx = difficultyIdx
            = typeIdx = ratingIdx = numReviewsIdx = featuresIdx = activitiesIdx
            = -1;

            String[] fields = scan.nextLine().split(",");
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

            // reading each row
            while (scan.hasNextLine()) {
                String[] record = scan.nextLine().split(",");
                trails.add(generateTrail(record));
            }

        } catch (Exception e) {
            trails = null;
            e.printStackTrace();
        }

        return trails;
    }

    public static void getComments(String trailId, Listener<List<Comment>> listener) {
        Listener<List<Comment>> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Comment.DUMMY.query(Arrays.asList(trailId), mainThreadListener);
            }
        });
    }

    public static void postComment(Comment comment, List<File> images, Listener<Void> listener) {
        Listener<Void> mainThreadListener = new MainThreadListener(listener);
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<String> imageUrls = new ArrayList<>();
                for (File file : images) {
                    try {
                        String imageUrl = FireBaseUtil.uploadSync(file);
                        imageUrls.add(imageUrl);
                    } catch (Exception e) {
                        mainThreadListener.onFailed(e);
                        return;
                    }
                }
                comment.setImages(imageUrls);
                comment.insert(mainThreadListener);
            }
        });
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

        String[] location = record[locationIdx].split(",");
        trail.setLocation(new double[]{
                Double.parseDouble(location[0].split(":")[1]),
                Double.parseDouble(location[1].split(":")[1]),
        });

        trail.setFeatures(Arrays.asList(record[featuresIdx]
                .replace("'", "")
                .replace(" ", "")
                .split(",")));
        trail.setActivities(Arrays.asList(record[activitiesIdx]
                .replace("'", "")
                .replace(" ", "")
                .split(",")));

        return trail;
    }
}
