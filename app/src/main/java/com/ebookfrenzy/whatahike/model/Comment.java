package com.ebookfrenzy.whatahike.model;

import com.ebookfrenzy.whatahike.utils.StringUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Comment extends FireBaseModel {

    private final String id;
    private final String userId;
    private String trailId;
    private long timeStamp;
    private String text;
    private List<String> images;

    public Comment(String userId) {
        this.userId = userId;
        this.id = StringUtil.createMD5(userId + System.currentTimeMillis());
    }

    public String getTrailId() {
        return trailId;
    }

    public void setTrailId(String trailId) {
        this.trailId = trailId;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String getModelName() {
        return "Comment";
    }

    @Override
    public String getKey() {
        return id;
    }
}
