package com.ebookfrenzy.whatahike;

import com.ebookfrenzy.whatahike.model.Comment;
import com.ebookfrenzy.whatahike.model.Trail;
import com.ebookfrenzy.whatahike.utils.FireBaseUtil;
import com.ebookfrenzy.whatahike.utils.Listener;
import com.ebookfrenzy.whatahike.utils.MainThreadListener;
import com.ebookfrenzy.whatahike.utils.TrailsReadingUtil;

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
    private static List<Trail> trails;

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static List<Trail> getTrails(Filter<Trail> filter, Comparator<Trail> comparator) {
        if (trails == null) {
            readCSVTrails();
        }
        List<Trail> trails = new ArrayList<>();
        for (Trail trail : readCSVTrails()) {
            if (filter.pass(trail)) {
                trails.add(trail);
            }
        }
        Collections.sort(trails, comparator);
        return trails;
    }

    public static List<Trail> readCSVTrails() {
        trails = TrailsReadingUtil.readCSVTrails();
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


}
